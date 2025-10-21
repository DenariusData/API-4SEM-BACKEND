package data.denarius.radarius.service.criterion;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.RadarBaseData;
import data.denarius.radarius.enums.VehicleSpaceEnum;
import data.denarius.radarius.repository.RadarBaseDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VehicleDensityCalculator implements CriterionCalculator {
    
    private static final int REAL_TIME_WINDOW_MINUTES = 60;
    private static final BigDecimal RADAR_VISION_METERS = new BigDecimal("50.0");
    
    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private CriterionResultFactory resultFactory;

    @Override
    public CriterionCalculationResult calculate(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(REAL_TIME_WINDOW_MINUTES);
        
        Long vehicleCount = getVehicleCount(camera, windowStart, now);
        
        if (vehicleCount == 0) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No traffic data available for density analysis");
        }
        
        List<RadarBaseData> vehicleData = getVehicleData(camera, windowStart, now);
        
        BigDecimal totalSpaceOccupied = BigDecimal.ZERO;
        int validVehicleCount = 0;
        
        for (RadarBaseData data : vehicleData) {
            VehicleSpaceEnum vehicleSpace = VehicleSpaceEnum.fromString(data.getVehicleType());
            
            if (!vehicleSpace.isExcludedFromDensityCalculation()) {
                totalSpaceOccupied = totalSpaceOccupied.add(vehicleSpace.getSpaceOccupied());
                validVehicleCount++;
            }
        }
        
        if (validVehicleCount == 0) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No vehicles for density calculation (excluding buses)");
        }
        
        Integer totalLanes = getTotalLanes(camera);
        BigDecimal availableSpace = RADAR_VISION_METERS.multiply(BigDecimal.valueOf(totalLanes));
        
        BigDecimal densityPercentage = totalSpaceOccupied
            .divide(availableSpace, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        
        if (densityPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            densityPercentage = BigDecimal.valueOf(100);
        }
        
        return resultFactory.createResult(getCriterionName(), camera, densityPercentage, validVehicleCount,
            String.format("%.1f%% road occupation (adjusted to 0-100%%)", densityPercentage.doubleValue()));
    }

    @Override
    public String getCriterionName() {
        return "Densidade relativa de veículos por câmera";
    }
    
    private List<RadarBaseData> getVehicleData(Camera camera, LocalDateTime start, LocalDateTime end) {
        List<RadarBaseData> data = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(start) && !r.getDateTime().isAfter(end))
            .toList();
            
        log.info("Found {} vehicle readings for camera {} between {} and {}", 
            data.size(), camera.getId(), start, end);
            
        return data;
    }
    
    private Long getVehicleCount(Camera camera, LocalDateTime start, LocalDateTime end) {
        return radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(start) && !r.getDateTime().isAfter(end))
            .count();
    }
    
    private Integer getTotalLanes(Camera camera) {
        return radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> r.getTotalLanes() != null)
            .map(RadarBaseData::getTotalLanes)
            .findFirst()
            .orElse(2);
    }
}