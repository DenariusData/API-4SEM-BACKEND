package data.denarius.radarius.dto.radarbasedata;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RadarBaseDataRequestDTO {
    private BigDecimal cameraLatitude;
    private BigDecimal cameraLongitude;
    private String cameraId;
    private Integer cameraLane;
    private Integer totalLanes;
    private LocalDateTime dateTime;
    private String vehicleType;
    private BigDecimal vehicleSpeed;
    private Integer speedLimit;
    private String address;
    private String number;
    private String city;
    private String direction;

    public BigDecimal getCameraLatitude() { return cameraLatitude; }
    public void setCameraLatitude(BigDecimal cameraLatitude) { this.cameraLatitude = cameraLatitude; }

    public BigDecimal getCameraLongitude() { return cameraLongitude; }
    public void setCameraLongitude(BigDecimal cameraLongitude) { this.cameraLongitude = cameraLongitude; }

    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }

    public Integer getCameraLane() { return cameraLane; }
    public void setCameraLane(Integer cameraLane) { this.cameraLane = cameraLane; }

    public Integer getTotalLanes() { return totalLanes; }
    public void setTotalLanes(Integer totalLanes) { this.totalLanes = totalLanes; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public BigDecimal getVehicleSpeed() { return vehicleSpeed; }
    public void setVehicleSpeed(BigDecimal vehicleSpeed) { this.vehicleSpeed = vehicleSpeed; }

    public Integer getSpeedLimit() { return speedLimit; }
    public void setSpeedLimit(Integer speedLimit) { this.speedLimit = speedLimit; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}