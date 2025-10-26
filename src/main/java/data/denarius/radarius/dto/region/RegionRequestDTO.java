package data.denarius.radarius.dto.region;

import java.time.LocalDateTime;

public class RegionRequestDTO {
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
