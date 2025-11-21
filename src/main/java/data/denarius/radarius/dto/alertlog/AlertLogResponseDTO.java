package data.denarius.radarius.dto.alertlog;

import java.time.LocalDateTime;

public class AlertLogResponseDTO {
    private Integer id;
    private LocalDateTime createdAt;
    private Short previousLevel;
    private Short newLevel;
    private LocalDateTime closedAt;
    private String alertMessage;
    private String regionName;
    private Integer alertId;

    public Integer getAlertId() {
        return alertId;
    }

    public void setAlertId(Integer alertId) {
        this.alertId = alertId;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    private String criterionName;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Short getPreviousLevel() { return previousLevel; }
    public void setPreviousLevel(Short previousLevel) { this.previousLevel = previousLevel; }

    public Short getNewLevel() { return newLevel; }
    public void setNewLevel(Short newLevel) { this.newLevel = newLevel; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
}
