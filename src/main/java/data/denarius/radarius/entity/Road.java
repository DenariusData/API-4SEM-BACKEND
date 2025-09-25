package data.denarius.radarius.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "road")
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roadId;

    private String address;

    @Column(name = "speed_limit")
    private BigDecimal speedLimit;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "update_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "road")
    private List<Camera> cameras;

    public Integer getRoadId() {
        return roadId;
    }

    public void setRoadId(Integer roadId) {
        this.roadId = roadId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(BigDecimal speedLimit) {
        this.speedLimit = speedLimit;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public void setCameras(List<Camera> cameras) {
        this.cameras = cameras;
    }
}
