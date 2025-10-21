package data.denarius.radarius.service.criterion;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.RadarBaseData;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.CameraRepository;
import data.denarius.radarius.repository.RadarBaseDataRepository;
import data.denarius.radarius.repository.RegionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CongestionCalculator implements CriterionCalculator {
    
    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private CriterionResultFactory resultFactory;
    
    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private RegionRepository regionRepository;

    @Override
    public CriterionCalculationResult calculate(Camera camera) {
        return calculateCongestionForCamera(camera, LocalDateTime.now());
    }
    
    public Map<Region, CriterionCalculationResult> calculateRegionCongestion(List<RadarBaseData> schedulerData) {
        // Encontra a data/hora mais recente dos dados
        LocalDateTime referenceTime = schedulerData.stream()
            .map(RadarBaseData::getDateTime)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());
            
        // Agrupa os dados por câmera
        Map<String, List<RadarBaseData>> dataByCamera = schedulerData.stream()
            .collect(Collectors.groupingBy(RadarBaseData::getCameraId));
            
        // Calcula congestionamento para cada câmera
        Map<Camera, Double> congestionByCamera = new HashMap<>();
        
        for (Map.Entry<String, List<RadarBaseData>> entry : dataByCamera.entrySet()) {
            Camera camera = cameraRepository.findById(Integer.parseInt(entry.getKey()))
                .orElseThrow(() -> new RuntimeException("Camera not found: " + entry.getKey()));
                
            CriterionCalculationResult result = calculateCongestionForCamera(camera, referenceTime);
            congestionByCamera.put(camera, result.getCalculatedValue().doubleValue());
        }
        
        // Agrupa câmeras por região e calcula média
        Map<Region, List<Double>> congestionByRegion = new HashMap<>();
        
        for (Map.Entry<Camera, Double> entry : congestionByCamera.entrySet()) {
            Camera camera = entry.getKey();
            Double congestion = entry.getValue();
            Region region = camera.getRegion();
            
            if (region != null) {
                congestionByRegion.computeIfAbsent(region, k -> new ArrayList<>()).add(congestion);
            }
        }
        
        // Calcula resultado final por região
        Map<Region, CriterionCalculationResult> results = new HashMap<>();
        
        for (Map.Entry<Region, List<Double>> entry : congestionByRegion.entrySet()) {
            Region region = entry.getKey();
            List<Double> congestions = entry.getValue();
            
            double averageCongestion = congestions.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
                
            results.put(region, resultFactory.createResult(
                getCriterionName(),
                null, // Não há câmera específica para alerta regional
                BigDecimal.valueOf(averageCongestion).setScale(2, RoundingMode.HALF_UP),
                congestions.size(),
                String.format("%.1f%% congestion average across %d cameras in region %s", 
                    averageCongestion, congestions.size(), region.getName())
            ));
        }
        
        return results;
    }
    
    public CriterionCalculationResult calculateCongestionForCamera(Camera camera, LocalDateTime referenceTime) {
        LocalDateTime windowStart = referenceTime.minusHours(1);
        
        List<RadarBaseData> vehicleData = radarBaseDataRepository.findByCameraId(camera.getId().toString())
            .stream()
            .filter(r -> !r.getDateTime().isBefore(windowStart) && !r.getDateTime().isAfter(referenceTime))
            .toList();
            
        if (vehicleData.isEmpty()) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No traffic data available for congestion analysis");
        }
        
        double totalRelativeSpeed = 0.0;
        int validReadings = 0;
        
        for (RadarBaseData data : vehicleData) {
            if (data.getVehicleSpeed() != null && data.getSpeedLimit() != null && data.getSpeedLimit() > 0) {
                double averageSpeed = data.getVehicleSpeed().doubleValue();
                double speedLimit = data.getSpeedLimit().doubleValue();
                double relativeSpeed = (1.0 - (averageSpeed / speedLimit)) * 100.0;
                totalRelativeSpeed += relativeSpeed;
                validReadings++;
            }
        }
        
        if (validReadings == 0) {
            return resultFactory.createResult(getCriterionName(), camera, BigDecimal.ZERO, 0,
                "No valid speed readings for congestion analysis");
        }
        
        double congestionPercentage = Math.min(100.0, Math.max(0.0, totalRelativeSpeed / validReadings));
        
        return resultFactory.createResult(getCriterionName(), camera, BigDecimal.valueOf(congestionPercentage), 
            validReadings, String.format("%.1f%% congestion based on relative speed (adjusted to 0-100%%)", congestionPercentage));
    }

    @Override
    public String getCriterionName() {
        return "Congestionamento";
    }
    

}