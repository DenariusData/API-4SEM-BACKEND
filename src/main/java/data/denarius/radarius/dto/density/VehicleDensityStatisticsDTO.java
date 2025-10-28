package data.denarius.radarius.dto.density;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDensityStatisticsDTO {
    private String regionName;
    private Integer cameraId;
    private String cameraLocation;
    private Long totalVehicles;
    private BigDecimal occupiedSpace;
    private BigDecimal availableSpace;
    private Double densityPercentage;
}
