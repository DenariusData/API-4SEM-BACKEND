package data.denarius.radarius.dto;

import java.time.OffsetDateTime;

public class CriterionLevelResponseDTO {

    private Integer criterionLevelId;
    private Integer criterionId;
    private Short level;
    private OffsetDateTime createdAt;
    private Integer createdById;

    // Getters e Setters
    public Integer getCriterionLevelId() { return criterionLevelId; }
    public void setCriterionLevelId(Integer criterionLevelId) { this.criterionLevelId = criterionLevelId; }

    public Integer getCriterionId() { return criterionId; }
    public void setCriterionId(Integer criterionId) { this.criterionId = criterionId; }

    public Short getLevel() { return level; }
    public void setLevel(Short level) { this.level = level; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }
}
