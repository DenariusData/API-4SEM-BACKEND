package data.denarius.radarius.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class CriterionResponseDTO {

    private Integer criterionId;
    private String name;
    private OffsetDateTime createdAt;
    private Integer createdById;
    private List<Integer> levelIds;
    private List<Integer> alertIds;

    // Getters e Setters
    public Integer getCriterionId() { return criterionId; }
    public void setCriterionId(Integer criterionId) { this.criterionId = criterionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }

    public List<Integer> getLevelIds() { return levelIds; }
    public void setLevelIds(List<Integer> levelIds) { this.levelIds = levelIds; }

    public List<Integer> getAlertIds() { return alertIds; }
    public void setAlertIds(List<Integer> alertIds) { this.alertIds = alertIds; }
}
