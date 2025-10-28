package data.denarius.radarius.dto.speedviolation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeedViolationStatisticsDTO {
    private String regionName;
    private Long totalVehicles;
    private Long violatingVehicles;
    private Double violationRate;
}
