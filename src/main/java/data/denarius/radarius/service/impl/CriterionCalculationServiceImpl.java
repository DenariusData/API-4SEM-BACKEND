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

    private static final int REAL_TIME_WINDOW_MINUTES = 60; // Aumentado de 20 segundos para 60 minutos
    private static final int SPEED_VIOLATION_WINDOW_HOURS = 24;
    private static final BigDecimal RADAR_VISION_METERS = new BigDecimal("50.0");
    private static final Integer SPEED_VIOLATION_THRESHOLD_PERCENT = 110; // Reduzido para 10% acima do limite

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
        LocalDateTime windowStart = now.minusMinutes(REAL_TIME_WINDOW_MINUTES);
        
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
        
        double congestionPercentage = Math.min(100.0, Math.max(0.0, totalRelativeSpeed / validReadings));
        
        return createResult("Congestionamento", camera, BigDecimal.valueOf(congestionPercentage), 
            validReadings, String.format("%.1f%% congestion based on relative speed (adjusted to 0-100%%)", congestionPercentage));
    }

    @Override
    public CriterionCalculationResult calculateVehicleDensity(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(REAL_TIME_WINDOW_MINUTES);
        
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
        
        // Limitando o resultado a 100%
        if (densityPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            densityPercentage = BigDecimal.valueOf(100);
        }
        
        return createResult("Densidade relativa de veículos por câmera", camera, densityPercentage, validVehicleCount,
            String.format("%.1f%% road occupation (adjusted to 0-100%%)", densityPercentage.doubleValue()));
    }

    @Override
    public CriterionCalculationResult calculateLargeVehicleCirculation(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(REAL_TIME_WINDOW_MINUTES);
        
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
        
        double largeVehiclePercentage = Math.min(100.0, (double) largeVehicleData.size() / validTotalVehicles * 100.0);
        
        return createResult("Circulação de veículos de grande porte", camera, 
            BigDecimal.valueOf(largeVehiclePercentage), largeVehicleData.size(),
            String.format("%.1f%% large vehicles (%d of %d vehicles, adjusted to 0-100%%)", 
                largeVehiclePercentage, largeVehicleData.size(), validTotalVehicles));
    }

    @Override
    public CriterionCalculationResult calculateSpeedViolations(Camera camera) {
        // Pega os dados processados pelo RadarBaseDataScheduler nas últimas 24h
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusHours(SPEED_VIOLATION_WINDOW_HOURS);

        // Busca todos os registros da câmera nas últimas 24h
        List<RadarBaseData> recentData = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(windowStart) && !r.getDateTime().isAfter(now))
            .toList();

        if (recentData.isEmpty()) {
            return createResult("Infrações por excesso de velocidade", camera, BigDecimal.ZERO, 0,
                "No traffic data available for speed violation analysis");
        }

        // Identifica veículos únicos e suas infrações (20% acima do limite)
        Set<String> allVehicles = new HashSet<>();
        Set<String> speedingVehicles = new HashSet<>();

        for (RadarBaseData data : recentData) {
            // Usa combinação de câmera + data/hora como identificador único
            String vehicleId = data.getCameraId() + "_" + data.getDateTime().toString();

            if (data.getVehicleSpeed() != null && data.getSpeedLimit() != null && data.getSpeedLimit() > 0) {
                allVehicles.add(vehicleId);
                
                double speedLimit = data.getSpeedLimit().doubleValue();
                double actualSpeed = data.getVehicleSpeed().doubleValue();
                double speedRatio = (actualSpeed / speedLimit) * 100;

                if (speedRatio >= 120) { // 20% acima do limite
                    speedingVehicles.add(vehicleId);
                    log.info("Speed violation detected at camera {}: Vehicle {} speed: {}, limit: {}", 
                        camera.getId(), vehicleId, actualSpeed, speedLimit);
                }
            }
        }

        if (allVehicles.isEmpty()) {
            return createResult("Infrações por excesso de velocidade", camera, BigDecimal.ZERO, 0,
                "No valid speed readings available");
        }

        // Calcula a porcentagem de veículos únicos com infração
        double violationPercentage = (double) speedingVehicles.size() / allVehicles.size() * 100.0;
        
        return createResult("Infrações por excesso de velocidade", camera, 
            BigDecimal.valueOf(violationPercentage), speedingVehicles.size(),
            String.format("%.1f%% violation rate (%d unique vehicles with violations out of %d total unique vehicles)", 
                violationPercentage, speedingVehicles.size(), allVehicles.size()));
    }

    @Override
    public Integer calculateAlertLevel(String criterionName, Double calculatedValue) {
        if (calculatedValue == null || calculatedValue < 0) {
            log.warn("Invalid value for criterion {}: {}", criterionName, calculatedValue);
            return 1;
        }
        
        // Aplicando as faixas percentuais:
        // level 1 - 0% até 19.99%
        // level 2 - 20% até 39,99%
        // level 3 - 40% até 59,99%
        // level 4 - 60% até 79,99%
        // level 5 - 80% até 100%
        
        if (calculatedValue >= 80.0 && calculatedValue <= 100.0) return 5;
        if (calculatedValue >= 60.0 && calculatedValue < 80.0) return 4;
        if (calculatedValue >= 40.0 && calculatedValue < 60.0) return 3;
        if (calculatedValue >= 20.0 && calculatedValue < 40.0) return 2;
        if (calculatedValue >= 0.0 && calculatedValue < 20.0) return 1;
        
        log.warn("Value out of expected range (0-100%) for criterion {}: {}", criterionName, calculatedValue);
        return 1; // valor default para casos fora da faixa esperada
    }

    private boolean hasLevelChanged(CriterionCalculationResult result) {
        String key = result.getCamera().getId() + "_" + result.getCriterion().getName();
        Integer previousLevel = previousLevels.get(key);
        Integer currentLevel = result.getCalculatedLevel();
        
        previousLevels.put(key, currentLevel);
        
        return !Objects.equals(previousLevel, currentLevel);
    }
    
    private List<RadarBaseData> getVehicleDataInWindow(Camera camera, LocalDateTime start, LocalDateTime end) {
        // Filtrando em memória já que não temos uma query específica
        List<RadarBaseData> data = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(start) && !r.getDateTime().isAfter(end))
            .toList();
            
        log.info("Found {} vehicle readings for camera {} between {} and {}", 
            data.size(), camera.getId(), start, end);
            
        return data;
    }
    
    private Long getVehicleCountInWindow(Camera camera, LocalDateTime start, LocalDateTime end) {
        // Contando em memória já que não temos uma query de contagem específica
        return radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(start) && !r.getDateTime().isAfter(end))
            .count();
    }
    
    private List<RadarBaseData> getSpeedViolationsInWindow(Camera camera, LocalDateTime start, LocalDateTime end, Integer thresholdPercent) {
        // Filtrando violações de velocidade em memória
        List<RadarBaseData> violations = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(start) && !r.getDateTime().isAfter(end))
            .filter(r -> r.getVehicleSpeed() != null && r.getSpeedLimit() != null)
            .filter(r -> r.getVehicleSpeed().doubleValue() > (r.getSpeedLimit().doubleValue() * thresholdPercent / 100.0))
            .toList();
            
        log.info("Found {} speed violations for camera {} between {} and {} (threshold: {}%)", 
            violations.size(), camera.getId(), start, end, thresholdPercent);
            
        return violations;
    }
    
    private List<RadarBaseData> getLargeVehiclesInWindow(Camera camera, LocalDateTime start, LocalDateTime end) {
        // Filtrando veículos grandes em memória
        List<RadarBaseData> largeVehicles = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(start) && !r.getDateTime().isAfter(end))
            .filter(r -> {
                String type = r.getVehicleType().toLowerCase();
                return type.contains("caminhão") || type.contains("van") || type.contains("camionete");
            })
            .toList();
            
        log.info("Found {} large vehicles for camera {} between {} and {}", 
            largeVehicles.size(), camera.getId(), start, end);
            
        return largeVehicles;
    }
    
    private Integer getTotalLanesForCamera(Camera camera) {
        // Buscando o número de faixas do último registro
        return radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> r.getTotalLanes() != null)
            .map(RadarBaseData::getTotalLanes)
            .findFirst()
            .orElse(2); // Default para 2 faixas se não encontrar informação
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
