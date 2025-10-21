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
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LargeVehicleCirculationCalculator implements CriterionCalculator {
    
    private static final int REAL_TIME_WINDOW_MINUTES = 60;
    
    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private CriterionResultFactory resultFactory;

    @Override
    public CriterionCalculationResult calculate(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(REAL_TIME_WINDOW_MINUTES);
        
        List<RadarBaseData> largeVehicleData = getLargeVehicles(camera, windowStart, now);
        Long totalVehicleCount = getVehicleCount(camera, windowStart, now);
        
        if (totalVehicleCount == 0) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No traffic data available for large vehicle analysis");
        }
        
        List<RadarBaseData> allVehicleData = getVehicleData(camera, windowStart, now);
        int validTotalVehicles = 0;
        
        for (RadarBaseData data : allVehicleData) {
            VehicleSpaceEnum vehicleSpace = VehicleSpaceEnum.fromString(data.getVehicleType());
            if (!vehicleSpace.isExcludedFromDensityCalculation()) {
                validTotalVehicles++;
            }
        }
        
        if (validTotalVehicles == 0) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No vehicles for large vehicle analysis (excluding buses)");
        }
        
        double largeVehiclePercentage = Math.min(100.0, (double) largeVehicleData.size() / validTotalVehicles * 100.0);
        
        return resultFactory.createResult(getCriterionName(), camera, 
            BigDecimal.valueOf(largeVehiclePercentage), largeVehicleData.size(),
            String.format("%.1f%% large vehicles (%d of %d vehicles, adjusted to 0-100%%)", 
                largeVehiclePercentage, largeVehicleData.size(), validTotalVehicles));
    }

    @Override
    public String getCriterionName() {
        return "Circulação de veículos de grande porte";
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
    
    private List<RadarBaseData> getLargeVehicles(Camera camera, LocalDateTime start, LocalDateTime end) {
        List<RadarBaseData> largeVehicles = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(start) && !r.getDateTime().isAfter(end))
            .filter(r -> {
                String type = r.getVehicleType().toLowerCase();
                return type.contains("caminhão") || type.contains("van") || type.contains("camionete");
            })
            .toList();
            
        return largeVehicles;
    }
}