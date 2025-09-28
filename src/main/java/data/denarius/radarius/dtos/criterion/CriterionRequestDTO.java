package data.denarius.radarius.dtos.criterion;

public class CriterionRequestDTO {

    private String name;
    private Integer createdById;

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }
}
