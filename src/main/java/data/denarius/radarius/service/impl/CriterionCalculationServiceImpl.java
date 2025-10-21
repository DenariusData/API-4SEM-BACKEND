package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.CriterionCalculationService;
import data.denarius.radarius.service.criterion.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CriterionCalculationServiceImpl implements CriterionCalculationService {

    @Override
    public CriterionCalculationResult calculateCongestion(Camera camera) {
        return congestionCalculator.calculate(camera);
    }

    @Override
    public CriterionCalculationResult calculateVehicleDensity(Camera camera) {
        return vehicleDensityCalculator.calculate(camera);
    }

    @Override
    public CriterionCalculationResult calculateLargeVehicleCirculation(Camera camera) {
        return largeVehicleCalculator.calculate(camera);
    }

    @Override
    public CriterionCalculationResult calculateSpeedViolations(Camera camera) {
        return speedViolationCalculator.calculate(camera);
    }

    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private CriterionRepository criterionRepository;
    
    @Autowired
    private AlertLevelCalculator alertLevelCalculator;
    
    @Autowired
    private CongestionCalculator congestionCalculator;
    
    @Autowired
    private VehicleDensityCalculator vehicleDensityCalculator;
    
    @Autowired
    private LargeVehicleCirculationCalculator largeVehicleCalculator;
    
    @Autowired
    private SpeedViolationCalculator speedViolationCalculator;

    private final Map<String, Integer> previousLevels = new ConcurrentHashMap<>();

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
            results.add(congestionCalculator.calculate(camera));
            // results.add(vehicleDensityCalculator.calculate(camera));
            // results.add(largeVehicleCalculator.calculate(camera));
            // results.add(speedViolationCalculator.calculate(camera));
            
        } catch (Exception e) {
            log.error("Error calculating criteria for camera {}: {}", camera.getId(), e.getMessage(), e);
        }
        
        return results;
    }

    @Override
    public CriterionCalculationResult calculateCriterionForCamera(Criterion criterion, Camera camera) {
        String criterionName = criterion.getName().toLowerCase();
        
        if (criterionName.contains("congestion") || criterionName.contains("congestionamento")) {
            return congestionCalculator.calculate(camera);
        } else if (criterionName.contains("density") || criterionName.contains("densidade")) {
            return vehicleDensityCalculator.calculate(camera);
        } else if (criterionName.contains("large") || criterionName.contains("grande porte")) {
            return largeVehicleCalculator.calculate(camera);
        } else if (criterionName.contains("speed") || criterionName.contains("velocidade")) {
            return speedViolationCalculator.calculate(camera);
        } else {
            log.warn("Unknown criterion type: {}", criterion.getName());
            return null;
        }
    }



    @Override
    public Integer calculateAlertLevel(String criterionName, Double calculatedValue) {
        return alertLevelCalculator.calculateAlertLevel(criterionName, calculatedValue);
    }

    private boolean hasLevelChanged(CriterionCalculationResult result) {
        String key = result.getCamera().getId() + "_" + result.getCriterion().getName();
        Integer previousLevel = previousLevels.get(key);
        Integer currentLevel = result.getCalculatedLevel();
        
        previousLevels.put(key, currentLevel);
        
        return !Objects.equals(previousLevel, currentLevel);
    }
    
    protected CriterionCalculationResult createResult(String criterionName, Camera camera, 
            BigDecimal value, int sampleSize, String description) {
        
        Criterion criterion = criterionRepository.findByName(criterionName)
            .orElseThrow(() -> new RuntimeException("Criterion not found: " + criterionName + 
                ". Please ensure all required criteria are created in the database via init.sql"));
        
        Integer alertLevel = calculateAlertLevel(criterionName, value.doubleValue());
        Region region = camera.getRegion();
        
        return new CriterionCalculationResult(criterion, camera, region, value, alertLevel, 
            sampleSize, description);
    }
}
