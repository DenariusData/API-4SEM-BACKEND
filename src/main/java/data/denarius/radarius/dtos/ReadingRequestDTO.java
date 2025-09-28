package data.denarius.radarius.dto;

import data.denarius.radarius.enums.VehicleTypeEnum;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ReadingRequestDTO {

    private Integer cameraId;
    private OffsetDateTime timestamp;
    private VehicleTypeEnum vehicleType;
    private BigDecimal speed;
    private String plate;

    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public VehicleTypeEnum getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleTypeEnum vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}
