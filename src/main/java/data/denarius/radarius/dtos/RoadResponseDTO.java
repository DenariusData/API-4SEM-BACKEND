package data.denarius.radarius.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class RoadResponseDTO {

    private Integer roadId;
    private String address;
    private BigDecimal speedLimit;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<Integer> cameraIds;

    // Getters e Setters
    public Integer getRoadId() { return roadId; }
    public void setRoadId(Integer roadId) { this.roadId = roadId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getSpeedLimit() { return speedLimit; }
    public void setSpeedLimit(BigDecimal speedLimit) { this.speedLimit = speedLimit; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Integer> getCameraIds() { return cameraIds; }
    public void setCameraIds(List<Integer> cameraIds) { this.cameraIds = cameraIds; }
}
