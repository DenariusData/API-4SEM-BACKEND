package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.AdvancedAlertService;
import data.denarius.radarius.service.AlertLogService;
import data.denarius.radarius.service.CriterionCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AdvancedAlertServiceImpl implements AdvancedAlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertLogService alertLogService;

    @Autowired
    private CriterionCalculationService criterionCalculationService;

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    @Transactional
    public void processAllCriteriaAndGenerateAlerts() {
        log.info("Starting advanced alert processing for all cameras and criteria");
        
        try {
            List<Camera> allCameras = cameraRepository.findAll();
            log.info("Processing {} cameras for advanced alerts", allCameras.size());
            
            // Process cameras in batches to improve performance
            int batchSize = 10; // Process 10 cameras at a time
            int totalBatches = (int) Math.ceil((double) allCameras.size() / batchSize);
            
            for (int batch = 0; batch < totalBatches; batch++) {
                int startIndex = batch * batchSize;
                int endIndex = Math.min(startIndex + batchSize, allCameras.size());
                List<Camera> cameraBatch = allCameras.subList(startIndex, endIndex);
                
                log.debug("Processing batch {}/{} ({} cameras)", batch + 1, totalBatches, cameraBatch.size());
                
                Map<Camera, List<CriterionCalculationResult>> changedCriteria = new HashMap<>();
                
                // Calculate criteria for cameras in this batch
                for (Camera camera : cameraBatch) {
                    try {
                        List<CriterionCalculationResult> cameraResults = 
                            criterionCalculationService.calculateAllCriteriaForCamera(camera);
                        
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
                
                // Process alerts for this batch
                log.debug("Found level changes for {} cameras in batch {}", changedCriteria.size(), batch + 1);
                
                for (Map.Entry<Camera, List<CriterionCalculationResult>> entry : changedCriteria.entrySet()) {
                    Camera camera = entry.getKey();
                    List<CriterionCalculationResult> calculations = entry.getValue();
                    
                    for (CriterionCalculationResult calculation : calculations) {
                        try {
                            createOrUpdateAlert(calculation);
                        } catch (Exception e) {
                            log.error("Error creating alert for calculation {} at camera {}: {}", 
                                calculation.getCriterion().getName(), camera.getId(), e.getMessage(), e);
                        }
                    }
                }
                
                // Small delay between batches to avoid overwhelming the database
                if (batch < totalBatches - 1) {
                    try {
                        Thread.sleep(100); // 100ms delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("Batch processing interrupted");
                        break;
                    }
                }
            }
            
            log.info("Completed advanced alert processing for all cameras");
            
        } catch (Exception e) {
            log.error("Error in advanced alert processing: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private boolean hasLevelChanged(CriterionCalculationResult result) {
        if (result == null || result.getCriterion() == null || result.getCamera() == null) {
            return false;
        }
        
        String key = result.getCamera().getId() + "_" + result.getCriterion().getId();
        Integer currentLevel = result.getCalculatedLevel();
        Integer previousLevel = previousLevels.get(key);
        
        if (previousLevel == null || !previousLevel.equals(currentLevel)) {
            previousLevels.put(key, currentLevel);
            return true;
        }
        
        return false;
    }
    
    private final Map<String, Integer> previousLevels = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public Alert createOrUpdateAlert(CriterionCalculationResult calculation) {
        SourceTypeEnum sourceType = SourceTypeEnum.AUTOMATICO;
        Integer criterionId = calculation.getCriterion().getId();
        Integer cameraId = calculation.getCamera().getId();
        Integer regionId = calculation.getCamera().getRegion() != null ? 
            calculation.getCamera().getRegion().getId() : null;
        
        // Find existing ACTIVE alert with same unique combination (optimized query)
        Optional<Alert> existingActiveAlert = alertRepository
            .findActiveAlertBySourceTypeAndCriterionIdAndCameraIdAndRegionId(
                sourceType, criterionId, cameraId, regionId);
        
        if (existingActiveAlert.isPresent()) {
            Alert alert = existingActiveAlert.get();
            Short currentLevel = alert.getLevel();
            Integer newLevel = calculation.getCalculatedLevel();
            
            // Skip update if level hasn't changed
            if (Objects.equals(currentLevel, newLevel.shortValue())) {
                log.debug("Active alert {} already has level {}, no update needed", 
                    alert.getId(), currentLevel);
                return alert;
            }
            
            log.info("Updating active alert {} from level {} to level {}", 
                alert.getId(), currentLevel, newLevel);
            
            // Update the alert
            alert.setLevel(newLevel.shortValue());
            alert.setMessage(buildAlertDescription(calculation));
            
            Alert savedAlert = alertRepository.save(alert);
            alertLogService.create(newLevel.shortValue(), calculation.getCriterion(), calculation.getCamera().getRegion());
            return savedAlert;
        } else {
            // Create new alert
            return createNewAlert(calculation, sourceType);
        }
    }

    private Alert createNewAlert(CriterionCalculationResult calculation, SourceTypeEnum sourceType) {
        log.info("Creating new alert for criterion {} at camera {} with level {}", 
            calculation.getCriterion().getName(), 
            calculation.getCamera().getId(), 
            calculation.getCalculatedLevel());
        
        Alert alert = Alert.builder()
            .sourceType(sourceType)
            .level(calculation.getCalculatedLevel().shortValue())
            .criterion(calculation.getCriterion())
            .camera(calculation.getCamera())
            .region(calculation.getCamera().getRegion())
            .message(buildAlertDescription(calculation))
            .createdAt(LocalDateTime.now())
            .build();
        
        // Set responsible person if available
        Person responsiblePerson = getResponsiblePerson(calculation.getCamera().getRegion());
        if (responsiblePerson != null) {
            alert.setAssignedTo(responsiblePerson);
        }
        
        Alert savedAlert = alertRepository.save(alert);
        alertLogService.create(alert.getLevel(), calculation.getCriterion(), calculation.getCamera().getRegion());
        return savedAlert;
    }

    private String buildAlertDescription(CriterionCalculationResult calculation) {
        return String.format(
            "Alert for %s at Camera-%d (Region: %s). " +
            "Calculated value: %.2f, Level: %d. " +
            "Sample size: %d vehicles. %s",
            calculation.getCriterion().getName(),
            calculation.getCamera().getId(),
            calculation.getCamera().getRegion() != null ? calculation.getCamera().getRegion().getName() : "Unknown",
            calculation.getCalculatedValue().doubleValue(),
            calculation.getCalculatedLevel(),
            calculation.getSampleSize(),
            calculation.getDescription()
        );
    }

    private Person getResponsiblePerson(Region region) {
        if (region == null) return null;
        
        // For now, get any person responsible for this region
        // This could be enhanced with more sophisticated assignment logic
        return personRepository.findAll().stream()
            .findFirst()
            .orElse(null);
    }

    @Override
    @Transactional
    public void deactivateOldAlerts() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Alert> oldAlerts = alertRepository.findActiveAlertsOlderThan(cutoffTime);
        
        for (Alert alert : oldAlerts) {
            alert.setClosedAt(LocalDateTime.now());
            Alert savedAlert = alertRepository.save(alert);
            alertLogService.create((short)0, alert.getCriterion(), alert.getRegion()); // Nível 0 indica alerta desativado
        }
        
        if (!oldAlerts.isEmpty()) {
            log.info("Deactivated {} old alerts", oldAlerts.size());
        }
    }

    @Override
    public List<Alert> getActiveAlerts() {
        return alertRepository.findActiveAlerts();
    }

    @Override
    public List<Alert> getActiveAlertsByRegion(Integer regionId) {
        return alertRepository.findActiveAlertsByRegionId(regionId);
    }

    @Override
    public List<Alert> getActiveAlertsByLevel(Integer level) {
        return alertRepository.findActiveAlertsByLevel(level);
    }
}
