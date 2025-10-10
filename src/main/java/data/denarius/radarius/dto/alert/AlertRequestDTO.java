package data.denarius.radarius.dto.alert;

import data.denarius.radarius.enums.SourceTypeEnum;

import java.time.LocalDateTime;

public class AlertRequestDTO {
    private Short level;
    private String message;
    private String conclusion;
    private SourceTypeEnum sourceType;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Integer createdById;
    private Integer assignedToId;
    private Integer cameraId;
    private Integer criterionId;
    private Integer rootCauseId;

    // Getters e Setters
    public Short getLevel() { return level; }
    public void setLevel(Short level) { this.level = level; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public SourceTypeEnum getSourceType() { return sourceType; }
    public void setSourceType(SourceTypeEnum sourceType) { this.sourceType = sourceType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }

    public Integer getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Integer assignedToId) { this.assignedToId = assignedToId; }

    public Integer getCameraId() { return cameraId; }
    public void setCameraId(Integer cameraId) { this.cameraId = cameraId; }

    public Integer getCriterionId() { return criterionId; }
    public void setCriterionId(Integer criterionId) { this.criterionId = criterionId; }

    public Integer getRootCauseId() { return rootCauseId; }
    public void setRootCauseId(Integer rootCauseId) { this.rootCauseId = rootCauseId; }
}
