package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.AlertLogRepository;
import data.denarius.radarius.repository.AlertRepository;
import data.denarius.radarius.service.AlertLogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertLogServiceImpl implements AlertLogService {

    private final Short ACCEPTABLE_LEVEL = 2;

    @Autowired
    private AlertLogRepository alertLogRepository;
    @Autowired
    private AlertRepository alertRepository;

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
        } else {

            if (alert != null && alert.getClosedAt() == null) {
                alert.setClosedAt(LocalDateTime.now());
                alertRepository.save(alert);
                newAlertLog.setAlert(alert);
            }
        }

        return alertLogRepository.save(newAlertLog);
    }


    @Override
    public void delete(Integer id) {
        alertLogRepository.deleteById(id);
    }
}
