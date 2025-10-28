package data.denarius.radarius.dto.congestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongestionStatisticsDTO {
    private String regionName;
    private String roadAddress;
    private Long totalVehicles;
    private Double averageSpeed;
    private Double speedLimit;
    private Double congestionPercentage;
}
