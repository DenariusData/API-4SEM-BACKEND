package data.denarius.radarius.service;

import data.denarius.radarius.entity.AlertLog;

public interface AlertNotificationService {
    void notifyAlertLogCreated(AlertLog alertLog);
}
