package data.denarius.radarius.dto.road;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RoadRequestDTO {
    private String address;
    private BigDecimal speedLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters e Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getSpeedLimit() { return speedLimit; }
    public void setSpeedLimit(BigDecimal speedLimit) { this.speedLimit = speedLimit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
