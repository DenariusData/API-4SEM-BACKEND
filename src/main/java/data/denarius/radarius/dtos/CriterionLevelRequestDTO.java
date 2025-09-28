package data.denarius.radarius.dto;

import java.time.OffsetDateTime;

public class CriterionLevelRequestDTO {

    private Integer criterionId;
    private Short level;
    private Integer createdById;
    private OffsetDateTime createdAt;

    public Integer getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Integer criterionId) {
        this.criterionId = criterionId;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public Integer getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
