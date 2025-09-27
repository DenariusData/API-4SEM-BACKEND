package data.denarius.radarius.dto;

import java.math.BigDecimal;

public class CameraRequestDTO {

    private Integer roadId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean active;

    public Integer getRoadId() { return roadId; }
    public void setRoadId(Integer roadId) { this.roadId = roadId; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
