package data.denarius.radarius.dto;

import data.denarius.radarius.enums.SourceTypeEnum;
import java.time.OffsetDateTime;
import java.util.List;

public class AlertResponseDTO {

    private Integer alertId;
    private Integer criterionId;
    private Integer protocolId;
    private Short level;
    private String status;
    private Integer assignedToId;
    private String message;
    private String conclusion;
    private Integer cameraId;
    private OffsetDateTime createdAt;
    private SourceTypeEnum sourceType;
    private List<Integer> incidentIds;
    private List<Integer> logIds;

    // Getters e Setters
    public Integer getAlertId() { return alertId; }
    public void setAlertId(Integer alertId) { this.alertId = alertId; }

    public Integer getCriterionId() { return criterionId; }
    public void setCriterionId(Integer criterionId) { this.criterionId = criterionId; }

    public Integer getProtocolId() { return protocolId; }
    public void setProtocolId(Integer protocolId) { this.protocolId = protocolId; }

    public Short getLevel() { return level; }
    public void setLevel(Short level) { this.level = level; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Integer assignedToId) { this.assignedToId = assignedToId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public Integer getCameraId() { return cameraId; }
    public void setCameraId(Integer cameraId) { this.cameraId = cameraId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public SourceTypeEnum getSourceType() { return sourceType; }
    public void setSourceType(SourceTypeEnum sourceType) { this.sourceType = sourceType; }

    public List<Integer> getIncidentIds() { return incidentIds; }
    public void setIncidentIds(List<Integer> incidentIds) { this.incidentIds = incidentIds; }

    public List<Integer> getLogIds() { return logIds; }
    public void setLogIds(List<Integer> logIds) { this.logIds = logIds; }
}
