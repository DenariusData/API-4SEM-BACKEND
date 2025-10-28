package data.denarius.radarius.service;


public interface AlertLogService {
    void delete(Integer id);
    
    void createLogForNewAlert(Integer alertId, Short level, Integer regionId, Integer criterionId);
    void createLogForUpdatedAlert(Integer alertId);
    void createLogForDeletedAlert(Integer alertId, Short level, Integer regionId, Integer criterionId);
}
