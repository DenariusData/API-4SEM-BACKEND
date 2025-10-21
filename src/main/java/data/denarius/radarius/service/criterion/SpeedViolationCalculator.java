package data.denarius.radarius.service.criterion;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.RadarBaseData;
import data.denarius.radarius.repository.RadarBaseDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SpeedViolationCalculator implements CriterionCalculator {
    
    private static final int SPEED_VIOLATION_WINDOW_HOURS = 24;
    
    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private CriterionResultFactory resultFactory;

    @Override
    public CriterionCalculationResult calculate(Camera camera) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusHours(SPEED_VIOLATION_WINDOW_HOURS);

        List<RadarBaseData> recentData = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(windowStart) && !r.getDateTime().isAfter(now))
            .toList();

        if (recentData.isEmpty()) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No traffic data available for speed violation analysis");
        }

        Set<String> allVehicles = new HashSet<>();
        Set<String> speedingVehicles = new HashSet<>();

        for (RadarBaseData data : recentData) {
            String vehicleId = data.getCameraId() + "_" + data.getDateTime().toString();

            if (data.getVehicleSpeed() != null && data.getSpeedLimit() != null && data.getSpeedLimit() > 0) {
                allVehicles.add(vehicleId);
                
                double speedLimit = data.getSpeedLimit().doubleValue();
                double actualSpeed = data.getVehicleSpeed().doubleValue();
                double speedRatio = (actualSpeed / speedLimit) * 100;

                if (speedRatio >= 120) {
                    speedingVehicles.add(vehicleId);
                    log.info("Speed violation detected at camera {}: Vehicle {} speed: {}, limit: {}", 
                        camera.getId(), vehicleId, actualSpeed, speedLimit);
                }
            }
        }

        if (allVehicles.isEmpty()) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No valid speed readings available");
        }

        double violationPercentage = (double) speedingVehicles.size() / allVehicles.size() * 100.0;
        
        return resultFactory.createResult(getCriterionName(), camera, 
            BigDecimal.valueOf(violationPercentage), speedingVehicles.size(),
            String.format("%.1f%% violation rate (%d unique vehicles with violations out of %d total unique vehicles)", 
                violationPercentage, speedingVehicles.size(), allVehicles.size()));
    }

    @Override
    public String getCriterionName() {
        return "Infrações por excesso de velocidade";
    }
}