package data.denarius.radarius.dto.detectedincident;

import java.time.LocalDateTime;

public class DetectedIncidentRequestDTO {
    private String incidentType;
    private LocalDateTime createdAt;
    private Integer createdById;
    private Integer alertId;

    // Getters e Setters
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }

    public Integer getAlertId() { return alertId; }
    public void setAlertId(Integer alertId) { this.alertId = alertId; }
}
