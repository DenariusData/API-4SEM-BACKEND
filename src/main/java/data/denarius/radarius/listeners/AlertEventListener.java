package data.denarius.radarius.listeners;

import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.service.AlertLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class AlertEventListener {

    @Autowired
    private AlertLogService alertLogService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAlertCreated(AlertEntityListener.AlertCreatedEvent event) {
        try {
            Alert alert = event.getAlert();
            
            alertLogService.createLogForNewAlert(
                alert.getId(), 
                alert.getLevel(),
                alert.getRegion() != null ? alert.getRegion().getId() : null,
                alert.getCriterion() != null ? alert.getCriterion().getId() : null
            );
        } catch (Exception e) {
            log.error("Error handling AlertCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAlertUpdated(AlertEntityListener.AlertUpdatedEvent event) {
        try {
            Alert alert = event.getAlert();
            
            alertLogService.createLogForUpdatedAlert(alert.getId());
        } catch (Exception e) {
            log.error("Error handling AlertUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAlertDeleted(AlertEntityListener.AlertDeletedEvent event) {
        try {
            Alert alert = event.getAlert();
            
            alertLogService.createLogForDeletedAlert(
                alert.getId(),
                alert.getLevel(),
                alert.getRegion() != null ? alert.getRegion().getId() : null,
                alert.getCriterion() != null ? alert.getCriterion().getId() : null
            );
        } catch (Exception e) {
            log.error("Error handling AlertDeletedEvent: {}", e.getMessage(), e);
        }
    }
}
