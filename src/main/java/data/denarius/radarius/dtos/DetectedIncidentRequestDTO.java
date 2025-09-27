package data.denarius.radarius.dto;

public class DetectedIncidentRequestDTO {

    private Integer alertId;
    private Integer userId;
    private String incidentType;

    // Getters e Setters
    public Integer getAlertId() { return alertId; }
    public void setAlertId(Integer alertId) { this.alertId = alertId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
}
