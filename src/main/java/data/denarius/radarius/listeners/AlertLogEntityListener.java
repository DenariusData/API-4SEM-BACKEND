package data.denarius.radarius.listeners;

import data.denarius.radarius.entity.AlertLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import jakarta.persistence.*;

@Slf4j
@Component
public class AlertLogEntityListener {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        AlertLogEntityListener.eventPublisher = eventPublisher;
    }

    @PostPersist
    public void afterCreate(AlertLog alertLog) {
        try {
            log.debug("AlertLog created with ID: {}, publishing event", alertLog.getId());
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new AlertLogCreatedEvent(alertLog));
            }
        } catch (Exception e) {
            log.error("Error publishing AlertLogCreatedEvent: {}", e.getMessage(), e);
        }
    }
    
    public static class AlertLogCreatedEvent {
        private final AlertLog alertLog;
        
        public AlertLogCreatedEvent(AlertLog alertLog) {
            this.alertLog = alertLog;
        }
        
        public AlertLog getAlertLog() {
            return alertLog;
        }
    }
}
