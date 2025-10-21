package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.AdvancedAlertService;
import data.denarius.radarius.service.AlertLogService;

import data.denarius.radarius.service.criterion.CongestionCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AdvancedAlertServiceImpl implements AdvancedAlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertLogService alertLogService;

    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private CongestionCalculator congestionCalculator;
    
    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;

    @Override
    @Transactional
    public void processAllCriteriaAndGenerateAlerts() {
        log.info("Starting advanced alert processing");
        
        try {
            // Busca os dados mais recentes do radar
            List<RadarBaseData> recentData = radarBaseDataRepository.findAll();
            if (recentData.isEmpty()) {
                log.info("No radar data available for processing");
                return;
            }
            
            // Calcula congestionamento por região
            Map<Region, CriterionCalculationResult> congestionByRegion = 
                congestionCalculator.calculateRegionCongestion(recentData);
            
            // Processa alertas por região
            for (Map.Entry<Region, CriterionCalculationResult> entry : congestionByRegion.entrySet()) {
                Region region = entry.getKey();
                CriterionCalculationResult calculation = entry.getValue();
                
                try {
                    createOrUpdateRegionalAlert(calculation);
                } catch (Exception e) {
                    log.error("Error creating regional alert for {}: {}", 
                        region.getName(), e.getMessage(), e);
                }
            }
            
            log.info("Completed advanced alert processing");
            
        } catch (Exception e) {
            log.error("Error in advanced alert processing: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private Alert createOrUpdateRegionalAlert(CriterionCalculationResult calculation) {
        SourceTypeEnum sourceType = SourceTypeEnum.AUTOMATICO;
        Integer criterionId = calculation.getCriterion().getId();
        Integer regionId = calculation.getRegion().getId();
        
        // Find existing ACTIVE regional alert
        Optional<Alert> existingActiveAlert = alertRepository
            .findActiveAlertBySourceTypeAndCriterionIdAndCameraIdAndRegionId(
                sourceType, criterionId, null, regionId);
        
        if (existingActiveAlert.isPresent()) {
            Alert alert = existingActiveAlert.get();
            Short currentLevel = alert.getLevel();
            Integer newLevel = calculation.getCalculatedLevel();
            
            // Skip update if level hasn't changed
            if (Objects.equals(currentLevel, newLevel.shortValue())) {
                log.debug("Active regional alert {} already has level {}, no update needed", 
                    alert.getId(), currentLevel);
                return alert;
            }
            
            log.info("Updating active regional alert {} from level {} to level {}", 
                alert.getId(), currentLevel, newLevel);
            
            // Update the alert
            alert.setLevel(newLevel.shortValue());
            alert.setMessage(buildRegionalAlertDescription(calculation));
            
            Alert savedAlert = alertRepository.save(alert);
            alertLogService.create(newLevel.shortValue(), calculation.getCriterion(), calculation.getRegion());
            return savedAlert;
        } else {
            // Create new regional alert
            return createNewRegionalAlert(calculation, sourceType);
        }
    }
    
    private Alert createNewRegionalAlert(CriterionCalculationResult calculation, SourceTypeEnum sourceType) {
        log.info("Creating new regional alert for criterion {} at region {} with level {}", 
            calculation.getCriterion().getName(), 
            calculation.getRegion().getName(), 
            calculation.getCalculatedLevel());
        
        Alert alert = Alert.builder()
            .sourceType(sourceType)
            .level(calculation.getCalculatedLevel().shortValue())
            .criterion(calculation.getCriterion())
            .camera(null)  // Regional alerts don't have a specific camera
            .region(calculation.getRegion())
            .message(buildRegionalAlertDescription(calculation))
            .createdAt(LocalDateTime.now())
            .build();
        
        // Set responsible person if available
        Person responsiblePerson = getResponsiblePerson(calculation.getRegion());
        if (responsiblePerson != null) {
            alert.setAssignedTo(responsiblePerson);
        }
        
        Alert savedAlert = alertRepository.save(alert);
        alertLogService.create(alert.getLevel(), calculation.getCriterion(), calculation.getRegion());
        return savedAlert;
    }
    
    private String buildRegionalAlertDescription(CriterionCalculationResult calculation) {
        return String.format(
            "Regional Alert for %s in %s. " +
            "Average value: %.2f, Level: %d. " +
            "Based on %d cameras. %s",
            calculation.getCriterion().getName(),
            calculation.getRegion().getName(),
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
        LocalDateTime now = LocalDateTime.now();
        
        for (Alert alert : oldAlerts) {
            try {
                alertLogService.create((short)0, alert.getCriterion(), alert.getRegion());
                
                alert.setClosedAt(now);
                alertRepository.save(alert);
            } catch (Exception e) {
                log.error("Error deactivating alert {}: {}", alert.getId(), e.getMessage(), e);
            }
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

    @Override
    @Transactional
    public Alert createOrUpdateAlert(CriterionCalculationResult calculation) {
        // For backward compatibility - delegate to regional alert creation
        // If the calculation has a region set directly, use it
        // Otherwise try to get the region from the camera
        if (calculation.getRegion() == null && calculation.getCamera() != null) {
            calculation.setRegion(calculation.getCamera().getRegion());
        }
        
        return createOrUpdateRegionalAlert(calculation);
    }
}
