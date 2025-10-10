package data.denarius.radarius.dto.criterion;

import java.time.LocalDateTime;

public class CriterionRequestDTO {
    private String name;
    private LocalDateTime createdAt;
    private Integer createdById;

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }
}
