package data.denarius.radarius.dto.reading;

import data.denarius.radarius.enums.VehicleTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReadingResponseDTO {
    private Integer id;
    private LocalDateTime createdAt;
    private VehicleTypeEnum vehicleType;
    private BigDecimal speed;
    private String plate;
    private String cameraRegion;
    private String cameraRoad;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public VehicleTypeEnum getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleTypeEnum vehicleType) { this.vehicleType = vehicleType; }

    public BigDecimal getSpeed() { return speed; }
    public void setSpeed(BigDecimal speed) { this.speed = speed; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getCameraRegion() { return cameraRegion; }
    public void setCameraRegion(String cameraRegion) { this.cameraRegion = cameraRegion; }

    public String getCameraRoad() { return cameraRoad; }
    public void setCameraRoad(String cameraRoad) { this.cameraRoad = cameraRoad; }
}
