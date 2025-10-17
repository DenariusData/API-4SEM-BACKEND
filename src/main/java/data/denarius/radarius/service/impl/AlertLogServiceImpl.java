package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.alertlog.AlertLogResponseDTO;
import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.AlertLogRepository;
import data.denarius.radarius.repository.AlertRepository;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.AlertLogService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertLogServiceImpl implements AlertLogService {

    @Autowired
    private AlertLogRepository alertLogRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private RegionRepository regionRepository;

    @Override
    public AlertLog create(Short newLevel, Criterion criterion, Region region) {
        AlertLog newAlertLog = AlertLog.builder()
                .region(region)
                .criterion(criterion)
                .newLevel(newLevel)
                .createdAt(LocalDateTime.now())
                .build();

        AlertLog previousAlertLog = alertLogRepository.findLatestByCriterionAndRegion(criterion.getId(), region.getId())
                .orElse(null);
        Short previousLevel = previousAlertLog == null ? 1 : previousAlertLog.getNewLevel();
        newAlertLog.setPreviousLevel(previousLevel);

        Alert alert = alertRepository.findLatestAlertByCriterionAndRegion(criterion.getId(), region.getId())
                .orElse(null);

        if (alert != null) {
            if (alert.getClosedAt() == null) newAlertLog.setAlert(alert);
            else if (newAlertLog.getNewLevel() > newAlertLog.getPreviousLevel() && previousAlertLog != null) {
                Alert newAlert = alertRepository.save(Alert.builder()
                        .message("Queda de nível na região " +
                                region.getName() +
                                " para o critério " +
                                criterion.getName())
                        .level(newLevel)
                        .createdAt(LocalDateTime.now())
                        .region(region)
                        .criterion(criterion)
                        .build()
                );
                newAlertLog.setAlert(newAlert);
            }
        }

        return alertLogRepository.save(newAlertLog);
    }

    @Override
    public void delete(Integer id) {
        alertLogRepository.deleteById(id);
    }

    @Override
    public AlertLog findById(Integer id) {
        return alertLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AlertLog não encontrado"));
    }

    @Override
    public List<AlertLog> findByAlert(Alert alert) {
        return alertLogRepository.findByAlert(alert);
    }
}
