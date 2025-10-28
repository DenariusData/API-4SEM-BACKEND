package data.denarius.radarius.dto.reading;

import data.denarius.radarius.enums.VehicleTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReadingRequestDTO {
    private LocalDateTime createdAt;
    private VehicleTypeEnum vehicleType;
    private BigDecimal speed;
    private String plate;
    private Integer cameraId;

    // Getters e Setters
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public VehicleTypeEnum getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleTypeEnum vehicleType) { this.vehicleType = vehicleType; }

    public BigDecimal getSpeed() { return speed; }
    public void setSpeed(BigDecimal speed) { this.speed = speed; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public Integer getCameraId() { return cameraId; }
    public void setCameraId(Integer cameraId) { this.cameraId = cameraId; }
}
