package data.denarius.radarius.listeners;

import data.denarius.radarius.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import jakarta.persistence.*;

@Slf4j
@Component
public class AlertEntityListener {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        AlertEntityListener.eventPublisher = eventPublisher;
    }

    @PostPersist
    public void afterCreate(Alert alert) {
        try {
            log.debug("Alert created with ID: {}, publishing event", alert.getId());
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new AlertCreatedEvent(alert));
            }
        } catch (Exception e) {
            log.error("Error publishing AlertCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @PostUpdate
    public void afterUpdate(Alert alert) {
        try {
            log.debug("Alert ID: {} updated, publishing event", alert.getId());
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new AlertUpdatedEvent(alert));
            }
        } catch (Exception e) {
            log.error("Error publishing AlertUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    @PostRemove
    public void afterDelete(Alert alert) {
        try {
            log.debug("Alert ID: {} deleted, publishing event", alert.getId());
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new AlertDeletedEvent(alert));
            }
        } catch (Exception e) {
            log.error("Error publishing AlertDeletedEvent: {}", e.getMessage(), e);
        }
    }
    
    public static class AlertCreatedEvent {
        private final Alert alert;
        
        public AlertCreatedEvent(Alert alert) {
            this.alert = alert;
        }
        
        public Alert getAlert() {
            return alert;
        }
    }
    
    public static class AlertUpdatedEvent {
        private final Alert alert;
        
        public AlertUpdatedEvent(Alert alert) {
            this.alert = alert;
        }
        
        public Alert getAlert() {
            return alert;
        }
    }
    
    public static class AlertDeletedEvent {
        private final Alert alert;
        
        public AlertDeletedEvent(Alert alert) {
            this.alert = alert;
        }
        
        public Alert getAlert() {
            return alert;
        }
    }
}
