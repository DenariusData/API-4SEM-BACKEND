package data.denarius.radarius.dto;

import java.math.BigDecimal;

public class RoadRequestDTO {

    private String address;
    private BigDecimal speedLimit;

    // Getters e Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getSpeedLimit() { return speedLimit; }
    public void setSpeedLimit(BigDecimal speedLimit) { this.speedLimit = speedLimit; }
}
