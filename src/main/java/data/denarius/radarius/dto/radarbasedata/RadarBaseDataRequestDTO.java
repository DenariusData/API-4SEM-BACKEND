package data.denarius.radarius.dto.radarbasedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
