package data.denarius.radarius.dto.criterionlevel;

import java.time.LocalDateTime;

public class CriterionLevelResponseDTO {
    private Integer id;
    private Short level;
    private LocalDateTime createdAt;
    private String createdByName;
    private String criterionName;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Short getLevel() { return level; }
    public void setLevel(Short level) { this.level = level; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getCriterionName() { return criterionName; }
    public void setCriterionName(String criterionName) { this.criterionName = criterionName; }
}
