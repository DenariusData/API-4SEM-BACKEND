package data.denarius.radarius.service;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.Alert;

import java.util.List;

public interface AdvancedAlertService {
    void processAllCriteriaAndGenerateAlerts();
    Alert createOrUpdateAlert(CriterionCalculationResult calculation);
    void deactivateOldAlerts();
    List<Alert> getActiveAlerts();
    List<Alert> getActiveAlertsByRegion(Integer regionId);
    List<Alert> getActiveAlertsByLevel(Integer level);
}
