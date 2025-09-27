package data.denarius.radarius.dto;

public class CriterionLevelRequestDTO {

    private Integer criterionId;
    private Short level;
    private Integer createdById;

    public Integer getCriterionId() { return criterionId; }
    public void setCriterionId(Integer criterionId) { this.criterionId = criterionId; }

    public Short getLevel() { return level; }
    public void setLevel(Short level) { this.level = level; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }
}
