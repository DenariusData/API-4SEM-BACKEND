package data.denarius.radarius.dto.criterion;

import java.time.LocalDateTime;

public class CriterionRequestDTO {
    private String name;
    private String description;
    private String example;
    private String mathExpression;
    private LocalDateTime createdAt;
    private Integer createdById;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getMathExpression() { return mathExpression; }
    public void setMathExpression(String mathExpression) { this.mathExpression = mathExpression; }


    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }
}
