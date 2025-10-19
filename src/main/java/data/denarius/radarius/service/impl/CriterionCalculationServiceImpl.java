package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.VehicleSpaceEnum;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.CriterionCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CriterionCalculationServiceImpl implements CriterionCalculationService {

    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private CriterionRepository criterionRepository;
    
    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;

    private final Map<String, Integer> previousLevels = new ConcurrentHashMap<>();

    private static final int REAL_TIME_WINDOW_SECONDS = 20;
    private static final int SPEED_VIOLATION_WINDOW_HOURS = 24;
    private static final BigDecimal RADAR_VISION_METERS = new BigDecimal("50.0");
    private static final Integer SPEED_VIOLATION_THRESHOLD_PERCENT = 120; // 120% = 20% above limit

    @Override
    public Map<Camera, List<CriterionCalculationResult>> calculateAndDetectLevelChanges() {
        Map<Camera, List<CriterionCalculationResult>> changedCriteria = new HashMap<>();
        List<Camera> allCameras = cameraRepository.findAll();
        
        log.debug("Calculating criteria for {} cameras", allCameras.size());
        
        for (Camera camera : allCameras) {
            try {
                List<CriterionCalculationResult> cameraResults = calculateAllCriteriaForCamera(camera);
                List<CriterionCalculationResult> changedResults = new ArrayList<>();
                
                for (CriterionCalculationResult result : cameraResults) {
                    if (hasLevelChanged(result)) {
                        changedResults.add(result);
                    }
                }
                
                if (!changedResults.isEmpty()) {
                    changedCriteria.put(camera, changedResults);
                }
                
            } catch (Exception e) {
                log.error("Error calculating criteria for camera {}: {}", camera.getId(), e.getMessage(), e);
            }
        }
        
        return changedCriteria;
    }

    @Override
    public List<CriterionCalculationResult> calculateAllCriteriaForCamera(Camera camera) {
        List<CriterionCalculationResult> results = new ArrayList<>();
        
        try {
            results.add(calculateCongestion(camera));
            results.add(calculateVehicleDensity(camera));
            results.add(calculateLargeVehicleCirculation(camera));
            results.add(calculateSpeedViolations(camera));
            
        } catch (Exception e) {
            log.error("Error calculating criteria for camera {}: {}", camera.getId(), e.getMessage(), e);
        }
        
        return results;
    }

    @Override
    public CriterionCalculationResult calculateCriterionForCamera(Criterion criterion, Camera camera) {
        String criterionName = criterion.getName().toLowerCase();
        
        if (criterionName.contains("congestion") || criterionName.contains("congestionamento")) {
            return calculateCongestion(camera);
        } else if (criterionName.contains("density") || criterionName.contains("densidade")) {
            return calculateVehicleDensity(camera);
        } else if (criterionName.contains("large") || criterionName.contains("grande porte")) {
            return calculateLargeVehicleCirculation(camera);
        } else if (criterionName.contains("speed") || criterionName.contains("velocidade")) {
            return calculateSpeedViolations(camera);
        } else {
            log.warn("Unknown criterion type: {}", criterion.getName());
            return null;
        }
    }

    @Override
    public CriterionCalculationResult calculateCongestion(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusSeconds(REAL_TIME_WINDOW_SECONDS);
        
        // Use optimized count query first to check if there's data
        Long vehicleCount = getVehicleCountInWindow(camera, windowStart, now);
        
        if (vehicleCount == 0) {
            return createResult("Congestionamento", camera, BigDecimal.ZERO, 0, 
                "No traffic data available for congestion analysis");
        }
        
        // Only fetch full data if there are vehicles
        List<RadarBaseData> vehicleData = getVehicleDataInWindow(camera, windowStart, now);
        
        double totalRelativeSpeed = 0.0;
        int validReadings = 0;
        
        for (RadarBaseData data : vehicleData) {
            if (data.getVehicleSpeed() != null && data.getSpeedLimit() != null && data.getSpeedLimit() > 0) {
                double averageSpeed = data.getVehicleSpeed().doubleValue();
                double speedLimit = data.getSpeedLimit().doubleValue();
                double relativeSpeed = (1.0 - (averageSpeed / speedLimit)) * 100.0;
                totalRelativeSpeed += relativeSpeed;
                validReadings++;
            }
        }
        
        if (validReadings == 0) {
            return createResult("Congestionamento", camera, BigDecimal.ZERO, 0,
                "No valid speed readings for congestion analysis");
        }
        
        double congestionPercentage = Math.max(0.0, totalRelativeSpeed / validReadings);
        
        return createResult("Congestionamento", camera, BigDecimal.valueOf(congestionPercentage), 
            validReadings, String.format("%.1f%% congestion based on relative speed", congestionPercentage));
    }

    @Override
    public CriterionCalculationResult calculateVehicleDensity(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusSeconds(REAL_TIME_WINDOW_SECONDS);
        
        // Use optimized count query first
        Long vehicleCount = getVehicleCountInWindow(camera, windowStart, now);
        
        if (vehicleCount == 0) {
            return createResult("Densidade relativa de veículos por câmera", camera, BigDecimal.ZERO, 0,
                "No traffic data available for density analysis");
        }
        
        // Only fetch full data if there are vehicles
        List<RadarBaseData> vehicleData = getVehicleDataInWindow(camera, windowStart, now);
        
        BigDecimal totalSpaceOccupied = BigDecimal.ZERO;
        int validVehicleCount = 0;
        
        for (RadarBaseData data : vehicleData) {
            VehicleSpaceEnum vehicleSpace = VehicleSpaceEnum.fromString(data.getVehicleType());
            
            if (!vehicleSpace.isExcludedFromDensityCalculation()) {
                totalSpaceOccupied = totalSpaceOccupied.add(vehicleSpace.getSpaceOccupied());
                validVehicleCount++;
            }
        }
        
        if (validVehicleCount == 0) {
            return createResult("Densidade relativa de veículos por câmera", camera, BigDecimal.ZERO, 0,
                "No vehicles for density calculation (excluding buses)");
        }
        
        Integer totalLanes = getTotalLanesForCamera(camera);
        BigDecimal availableSpace = RADAR_VISION_METERS.multiply(BigDecimal.valueOf(totalLanes));
        
        BigDecimal densityPercentage = totalSpaceOccupied
            .divide(availableSpace, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        
        return createResult("Densidade relativa de veículos por câmera", camera, densityPercentage, validVehicleCount,
            String.format("%.1f%% road occupation", densityPercentage.doubleValue()));
    }

    @Override
    public CriterionCalculationResult calculateLargeVehicleCirculation(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusSeconds(REAL_TIME_WINDOW_SECONDS);
        
        // Use optimized query to get large vehicles directly
        List<RadarBaseData> largeVehicleData = getLargeVehiclesInWindow(camera, windowStart, now);
        Long totalVehicleCount = getVehicleCountInWindow(camera, windowStart, now);
        
        if (totalVehicleCount == 0) {
            return createResult("Circulação de veículos de grande porte", camera, BigDecimal.ZERO, 0,
                "No traffic data available for large vehicle analysis");
        }
        
        // Filter out buses from total count (need to get all data for this)
        List<RadarBaseData> allVehicleData = getVehicleDataInWindow(camera, windowStart, now);
        int validTotalVehicles = 0;
        
        for (RadarBaseData data : allVehicleData) {
            VehicleSpaceEnum vehicleSpace = VehicleSpaceEnum.fromString(data.getVehicleType());
            if (!vehicleSpace.isExcludedFromDensityCalculation()) {
                validTotalVehicles++;
            }
        }
        
        if (validTotalVehicles == 0) {
            return createResult("Circulação de veículos de grande porte", camera, BigDecimal.ZERO, 0,
                "No vehicles for large vehicle analysis (excluding buses)");
        }
        
        double largeVehiclePercentage = (double) largeVehicleData.size() / validTotalVehicles * 100.0;
        
        return createResult("Circulação de veículos de grande porte", camera, 
            BigDecimal.valueOf(largeVehiclePercentage), largeVehicleData.size(),
            String.format("%.1f%% large vehicles (%d of %d vehicles)", 
                largeVehiclePercentage, largeVehicleData.size(), validTotalVehicles));
    }

    @Override
    public CriterionCalculationResult calculateSpeedViolations(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusHours(SPEED_VIOLATION_WINDOW_HOURS);
        
        // Use optimized query to get only speed violations directly
        List<RadarBaseData> violationData = getSpeedViolationsInWindow(camera, windowStart, now, SPEED_VIOLATION_THRESHOLD_PERCENT);
        Long totalVehicleCount = getVehicleCountInWindow(camera, windowStart, now);
        
        if (totalVehicleCount == 0) {
            return createResult("Infrações por excesso de velocidade", camera, BigDecimal.ZERO, 0,
                "No traffic data available for speed violation analysis");
        }
        
        // Use unique vehicle identification to avoid counting same vehicle multiple times
        Set<String> uniqueViolatingVehicles = new HashSet<>();
        
        for (RadarBaseData data : violationData) {
            if (data.getCameraId() != null) {
                String vehicleKey = data.getCameraId() + "_" + data.getDateTime().toLocalDate();
                uniqueViolatingVehicles.add(vehicleKey);
            }
        }
        
        if (uniqueViolatingVehicles.isEmpty()) {
            return createResult("Infrações por excesso de velocidade", camera, BigDecimal.ZERO, 0,
                "No speed violations detected in the last 24 hours");
        }
        
        // Calculate violation percentage based on unique vehicles
        double violationPercentage = (double) uniqueViolatingVehicles.size() / totalVehicleCount * 100.0;
        
        return createResult("Infrações por excesso de velocidade", camera, 
            BigDecimal.valueOf(violationPercentage), uniqueViolatingVehicles.size(),
            String.format("%.1f%% violation rate (%d violations in %d vehicles)", 
                violationPercentage, uniqueViolatingVehicles.size(), totalVehicleCount.intValue()));
    }

    @Override
    public Integer calculateAlertLevel(String criterionName, Double calculatedValue) {
        if (calculatedValue == null || calculatedValue < 0) return 1;
        
        if (calculatedValue >= 80.0) return 5;
        if (calculatedValue >= 60.0) return 4;
        if (calculatedValue >= 40.0) return 3;
        if (calculatedValue >= 20.0) return 2;
        return 1;
    }

    private boolean hasLevelChanged(CriterionCalculationResult result) {
        String key = result.getCamera().getId() + "_" + result.getCriterion().getName();
        Integer previousLevel = previousLevels.get(key);
        Integer currentLevel = result.getCalculatedLevel();
        
        previousLevels.put(key, currentLevel);
        
        return !Objects.equals(previousLevel, currentLevel);
    }
    
    private List<RadarBaseData> getVehicleDataInWindow(Camera camera, LocalDateTime start, LocalDateTime end) {
        if (camera.getLatitude() == null || camera.getLongitude() == null) {
            log.warn("Camera {} has null coordinates", camera.getId());
            return new ArrayList<>();
        }
        
        return radarBaseDataRepository.findByCameraCoordinatesAndDateTimeBetween(
            camera.getLatitude(), camera.getLongitude(), start, end);
    }
    
    private Long getVehicleCountInWindow(Camera camera, LocalDateTime start, LocalDateTime end) {
        if (camera.getLatitude() == null || camera.getLongitude() == null) {
            return 0L;
        }
        
        return radarBaseDataRepository.countByCameraCoordinatesAndDateTimeBetween(
            camera.getLatitude(), camera.getLongitude(), start, end);
    }
    
    private List<RadarBaseData> getSpeedViolationsInWindow(Camera camera, LocalDateTime start, LocalDateTime end, Integer thresholdPercent) {
        if (camera.getLatitude() == null || camera.getLongitude() == null) {
            return new ArrayList<>();
        }
        
        return radarBaseDataRepository.findSpeedViolationsByCameraAndDateTimeBetween(
            camera.getLatitude(), camera.getLongitude(), start, end, thresholdPercent);
    }
    
    private List<RadarBaseData> getLargeVehiclesInWindow(Camera camera, LocalDateTime start, LocalDateTime end) {
        if (camera.getLatitude() == null || camera.getLongitude() == null) {
            return new ArrayList<>();
        }
        
        return radarBaseDataRepository.findLargeVehiclesByCameraAndDateTimeBetween(
            camera.getLatitude(), camera.getLongitude(), start, end);
    }
    
    private Integer getTotalLanesForCamera(Camera camera) {
        if (camera.getLatitude() == null || camera.getLongitude() == null) {
            return 2;
        }
        
        List<Integer> lanes = radarBaseDataRepository.findTotalLanesByCameraCoordinatesSince(
            camera.getLatitude(), camera.getLongitude(), LocalDateTime.now().minusHours(1));
        
        return lanes.isEmpty() ? 2 : lanes.get(0);
    }
    
    private CriterionCalculationResult createResult(String criterionName, Camera camera, 
            BigDecimal value, int sampleSize, String description) {
        
        Criterion criterion = criterionRepository.findByName(criterionName)
            .orElseThrow(() -> new RuntimeException("Criterion not found: " + criterionName + 
                ". Please ensure all required criteria are created in the database via init.sql"));
        
        Integer alertLevel = calculateAlertLevel(criterionName, value.doubleValue());
        
        return new CriterionCalculationResult(criterion, camera, value, alertLevel, 
            sampleSize, description);
    }
}
