package data.denarius.radarius.dto.criterionlevel;

import java.time.LocalDateTime;

public class CriterionLevelRequestDTO {
    private Short level;
    private LocalDateTime createdAt;
    private Integer createdById;
    private Integer criterionId;

    // Getters e Setters
    public Short getLevel() { return level; }
    public void setLevel(Short level) { this.level = level; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }

    public Integer getCriterionId() { return criterionId; }
    public void setCriterionId(Integer criterionId) { this.criterionId = criterionId; }
}
