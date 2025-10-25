package data.denarius.radarius.listeners;

import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.service.AlertNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class AlertLogEventListener {

    @Autowired
    private AlertNotificationService alertNotificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAlertLogCreated(AlertLogEntityListener.AlertLogCreatedEvent event) {
        try {
            AlertLog alertLog = event.getAlertLog();
            
            alertNotificationService.notifyAlertLogCreated(alertLog);
        } catch (Exception e) {
            log.error("Error handling AlertLogCreatedEvent: {}", e.getMessage(), e);
        }
    }
}
