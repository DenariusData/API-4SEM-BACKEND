package data.denarius.radarius.dto.alert;

import data.denarius.radarius.enums.SourceTypeEnum;

import java.time.LocalDateTime;

public class AlertResponseDTO {
    private Integer id;
    private Short level;
    private String message;
    private String conclusion;
    private SourceTypeEnum sourceType;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private String createdByName;
    private String assignedToName;
    private String cameraName;
    private String criterionName;
    private String rootCauseName;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public String getCameraName() { return cameraName; }
    public void setCameraName(String cameraName) { this.cameraName = cameraName; }

    public String getCriterionName() { return criterionName; }
    public void setCriterionName(String criterionName) { this.criterionName = criterionName; }

    public String getRootCauseName() { return rootCauseName; }
    public void setRootCauseName(String rootCauseName) { this.rootCauseName = rootCauseName; }
}
