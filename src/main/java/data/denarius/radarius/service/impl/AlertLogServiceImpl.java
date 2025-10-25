package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.AlertLogRepository;
import data.denarius.radarius.repository.AlertRepository;
import data.denarius.radarius.repository.CriterionRepository;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.AlertLogService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AlertLogServiceImpl implements AlertLogService {

    private final Short ACCEPTABLE_LEVEL = 2;

    @Autowired
    private AlertLogRepository alertLogRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private CriterionRepository criterionRepository;

    @Override
    @Transactional
    public AlertLog create(Short newLevel, Criterion criterion, Region region) {

        AlertLog newAlertLog = AlertLog.builder()
                .region(region)
                .criterion(criterion)
                .newLevel(newLevel)
                .createdAt(LocalDateTime.now())
                .build();

        AlertLog previousAlertLog = alertLogRepository
                .findFirstByCriterionIdAndRegionIdOrderByCreatedAtDesc(criterion.getId(), region.getId())
                .orElse(null);
        Short previousLevel = previousAlertLog == null ? ACCEPTABLE_LEVEL : previousAlertLog.getNewLevel();
        newAlertLog.setPreviousLevel(previousLevel);

        Alert alert = alertRepository
                .findFirstByCriterionIdAndRegionIdOrderByCreatedAtDesc(criterion.getId(), region.getId())
                .orElse(null);

        if (newLevel > ACCEPTABLE_LEVEL) {
            if (alert == null || alert.getClosedAt() != null) {
                Alert newAlert = Alert.builder()
                        .message("Nível acima do aceitável na região " + region.getName() +
                                " para o critério " + criterion.getName())
                        .level(newLevel)
                        .createdAt(LocalDateTime.now())
                        .region(region)
                        .criterion(criterion)
                        .build();
                alertRepository.save(newAlert);
                newAlertLog.setAlert(newAlert);
            } else {
                alert.setLevel(newLevel);
                alertRepository.save(alert);
                newAlertLog.setAlert(alert);
            }
        } else if (alert != null && alert.getClosedAt() == null) {
            alert.setClosedAt(LocalDateTime.now());
            alertRepository.save(alert);
            newAlertLog.setAlert(alert);
        }


        return alertLogRepository.save(newAlertLog);
    }


    @Override
    public void delete(Integer id) {
        alertLogRepository.deleteById(id);
    }
    
    @Override
    @Async
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLogForNewAlert(Integer alertId, Short level, Integer regionId, Integer criterionId) {
        try {
            Alert alert = alertRepository.findById(alertId).orElse(null);
            if (alert == null) {
                log.warn("Alert ID {} not found for log creation", alertId);
                return;
            }
            
            Region region = regionId != null ? regionRepository.findById(regionId).orElse(null) : null;
            Criterion criterion = criterionId != null ? criterionRepository.findById(criterionId).orElse(null) : null;
            
            Short previousLevel = null;
            if (regionId != null && criterionId != null) {
                Alert previousAlert = alertRepository
                    .findFirstByCriterionIdAndRegionIdOrderByCreatedAtDesc(criterionId, regionId)
                    .orElse(null);
                
                if (previousAlert != null && !previousAlert.getId().equals(alertId)) {
                    previousLevel = previousAlert.getLevel();
                    log.debug("Found previous Alert ID {} with level {} for new Alert ID {}", 
                        previousAlert.getId(), previousLevel, alertId);
                }
            }
            
            AlertLog alertLog = AlertLog.builder()
                .alert(alert)
                .createdAt(LocalDateTime.now())
                .previousLevel(previousLevel)
                .newLevel(level)
                .region(region)
                .criterion(criterion)
                .build();
            
            alertLogRepository.save(alertLog);
            log.info("AlertLog created for new Alert ID: {} (previousLevel: {})", alertId, previousLevel);
            
        } catch (Exception e) {
            log.error("Error creating AlertLog for new alert {}: {}", alertId, e.getMessage(), e);
        }
    }
    
    @Override
    @Async
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLogForUpdatedAlert(Integer alertId) {
        try {
            Alert alert = alertRepository.findById(alertId).orElse(null);
            if (alert == null) {
                log.warn("Alert ID {} not found for log creation", alertId);
                return;
            }
            
            AlertLog lastLog = alertLogRepository.findTopByAlertOrderByCreatedAtDesc(alert).orElse(null);
            Short previousLevel = lastLog != null ? lastLog.getNewLevel() : null;
            
            boolean levelChanged = previousLevel == null || !previousLevel.equals(alert.getLevel());
            boolean wasClosed = alert.getClosedAt() != null && 
                (lastLog == null || lastLog.getClosedAt() == null);
            
            if (levelChanged || wasClosed) {
                AlertLog alertLog = AlertLog.builder()
                    .alert(alert)
                    .createdAt(LocalDateTime.now())
                    .previousLevel(previousLevel)
                    .newLevel(alert.getLevel())
                    .closedAt(alert.getClosedAt())
                    .region(alert.getRegion())
                    .criterion(alert.getCriterion())
                    .build();
                
                alertLogRepository.save(alertLog);
                log.info("AlertLog created for updated Alert ID: {} (level: {} -> {}, closed: {})", 
                    alertId, previousLevel, alert.getLevel(), alert.getClosedAt() != null);
            }
            
        } catch (Exception e) {
            log.error("Error creating AlertLog for updated alert {}: {}", alertId, e.getMessage(), e);
        }
    }
    
    @Override
    @Async
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLogForDeletedAlert(Integer alertId, Short level, Integer regionId, Integer criterionId) {
        try {
            Alert alert = alertRepository.findById(alertId).orElse(null);
            if (alert == null) {
                log.warn("Alert ID {} not found for deletion log", alertId);
                return;
            }
            
            Region region = regionId != null ? regionRepository.findById(regionId).orElse(null) : null;
            Criterion criterion = criterionId != null ? criterionRepository.findById(criterionId).orElse(null) : null;
            
            AlertLog alertLog = AlertLog.builder()
                .alert(alert)
                .createdAt(LocalDateTime.now())
                .previousLevel(level)
                .newLevel(null)
                .closedAt(LocalDateTime.now())
                .region(region)
                .criterion(criterion)
                .build();
            
            alertLogRepository.save(alertLog);
            log.info("AlertLog created for deleted Alert ID: {}", alertId);
            
        } catch (Exception e) {
            log.error("Error creating AlertLog for deleted alert {}: {}", alertId, e.getMessage(), e);
        }
    }
}
