package data.denarius.radarius.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class CameraResponseDTO {

    private Integer cameraId;
    private Integer roadId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<Integer> readingIds;
    private List<Integer> alertIds;

    public Integer getCameraId() { return cameraId; }
    public void setCameraId(Integer cameraId) { this.cameraId = cameraId; }

    public Integer getRoadId() { return roadId; }
    public void setRoadId(Integer roadId) { this.roadId = roadId; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Integer> getReadingIds() { return readingIds; }
    public void setReadingIds(List<Integer> readingIds) { this.readingIds = readingIds; }

    public List<Integer> getAlertIds() { return alertIds; }
    public void setAlertIds(List<Integer> alertIds) { this.alertIds = alertIds; }
}
