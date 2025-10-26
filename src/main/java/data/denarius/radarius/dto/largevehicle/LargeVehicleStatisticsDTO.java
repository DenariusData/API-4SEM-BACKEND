package data.denarius.radarius.dto.largevehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LargeVehicleStatisticsDTO {
    private String regionName;
    private String roadAddress;
    private Long totalVehicles;
    private Long largeVehicles;
    private Double largeVehiclePercentage;
}
