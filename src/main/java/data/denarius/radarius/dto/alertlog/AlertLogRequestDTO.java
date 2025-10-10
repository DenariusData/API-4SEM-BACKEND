package data.denarius.radarius.dto.alertlog;

import java.time.LocalDateTime;

public class AlertLogRequestDTO {
    private LocalDateTime createdAt;
    private Short previousLevel;
    private Short newLevel;
    private LocalDateTime closedAt;
    private Integer alertId;
    private Integer regionId;

    // Getters e Setters
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Short getPreviousLevel() { return previousLevel; }
    public void setPreviousLevel(Short previousLevel) { this.previousLevel = previousLevel; }

    public Short getNewLevel() { return newLevel; }
    public void setNewLevel(Short newLevel) { this.newLevel = newLevel; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public Integer getAlertId() { return alertId; }
    public void setAlertId(Integer alertId) { this.alertId = alertId; }

    public Integer getRegionId() { return regionId; }
    public void setRegionId(Integer regionId) { this.regionId = regionId; }
}
