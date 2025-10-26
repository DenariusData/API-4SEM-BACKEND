package data.denarius.radarius.service;

import data.denarius.radarius.dto.density.VehicleDensityStatisticsDTO;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
import data.denarius.radarius.enums.VehicleSpaceEnum;
import data.denarius.radarius.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VehicleDensityStatisticsService {

    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private CriterionRepository criterionRepository;
    
    private static final String DENSITY_CRITERION_NAME = "Densidade relativa de veículos por câmera";
    private static final int TIME_WINDOW_MINUTES = 20;
    private static final BigDecimal RADAR_VISION_METERS = new BigDecimal("50");
    
    @Transactional(readOnly = true)
    public List<VehicleDensityStatisticsDTO> calculateStatistics(
            LocalDateTime mostRecentDate,
            Map<String, Region> regionCache) {
        try {
            LocalDateTime timeWindowStart = mostRecentDate.minusMinutes(TIME_WINDOW_MINUTES);
            
            List<RadarBaseData> recentRecords = radarBaseDataRepository
                .findByDateTimeBetween(timeWindowStart, mostRecentDate);
            
            if (recentRecords.isEmpty()) {
                log.info("No records found in the last {} minutes", TIME_WINDOW_MINUTES);
                return Collections.emptyList();
            }
            
            log.info("Processing {} records from last {} minutes for density", 
                recentRecords.size(), TIME_WINDOW_MINUTES);
            
            Map<String, Camera> cameraCache = buildCameraCache(recentRecords);
            
            Map<String, List<RadarBaseData>> recordsByCamera = groupRecordsByCamera(
                recentRecords, cameraCache);
            
            List<VehicleDensityStatisticsDTO> statistics = calculateDensityStatistics(
                recordsByCamera, cameraCache, regionCache);
            
            processAlertsForStatistics(statistics, timeWindowStart, mostRecentDate);
            
            return statistics;
        } catch (Exception e) {
            log.error("Error calculating vehicle density statistics: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private Map<String, Camera> buildCameraCache(List<RadarBaseData> records) {
        List<RadarBaseData> validRecords = records.stream()
            .filter(r -> r.getCameraLatitude() != null && r.getCameraLongitude() != null)
            .collect(Collectors.toList());
        
        if (validRecords.isEmpty()) {
            return new HashMap<>();
        }
        
        List<Camera> existingCameras = cameraRepository.findAll();
        
        return existingCameras.stream()
            .collect(Collectors.toMap(
                camera -> camera.getLatitude() + "," + camera.getLongitude(),
                camera -> camera
            ));
    }
    
    private Map<String, List<RadarBaseData>> groupRecordsByCamera(
            List<RadarBaseData> records,
            Map<String, Camera> cameraCache) {
        
        Map<String, List<RadarBaseData>> recordsByCamera = new HashMap<>();
        
        for (RadarBaseData record : records) {
            if (record.getCameraLatitude() == null || record.getCameraLongitude() == null) {
                continue;
            }
            
            String coordinates = record.getCameraLatitude() + "," + record.getCameraLongitude();
            if (cameraCache.containsKey(coordinates)) {
                recordsByCamera
                    .computeIfAbsent(coordinates, k -> new ArrayList<>())
                    .add(record);
            }
        }
        
        return recordsByCamera;
    }
    
    private List<VehicleDensityStatisticsDTO> calculateDensityStatistics(
            Map<String, List<RadarBaseData>> recordsByCamera,
            Map<String, Camera> cameraCache,
            Map<String, Region> regionCache) {
        
        List<VehicleDensityStatisticsDTO> statistics = new ArrayList<>();
        
        for (Map.Entry<String, List<RadarBaseData>> entry : recordsByCamera.entrySet()) {
            String cameraCoordinates = entry.getKey();
            List<RadarBaseData> records = entry.getValue();
            Camera camera = cameraCache.get(cameraCoordinates);
            
            if (camera == null || camera.getRoad() == null) {
                continue;
            }
            
            Road road = camera.getRoad();
            int numberOfLanes = road.getLanes() != null ? road.getLanes() : 2;
            BigDecimal availableSpace = RADAR_VISION_METERS.multiply(new BigDecimal(numberOfLanes));
            
            // Agrupar registros por segundo para calcular densidade instantânea
            Map<LocalDateTime, List<RadarBaseData>> recordsBySecond = records.stream()
                .filter(r -> r.getVehicleType() != null && r.getDateTime() != null)
                .collect(Collectors.groupingBy(r -> r.getDateTime().withNano(0)));
            
            // Calcular densidade média ao longo do tempo
            double totalDensityPercentage = 0.0;
            int validSeconds = 0;
            
            for (List<RadarBaseData> secondRecords : recordsBySecond.values()) {
                // Calcular espaço ocupado neste segundo específico
                BigDecimal occupiedSpaceInSecond = secondRecords.stream()
                    .map(r -> VehicleSpaceEnum.fromString(r.getVehicleType()))
                    .filter(vehicleType -> !vehicleType.isExcludedFromDensityCalculation())
                    .map(VehicleSpaceEnum::getSpaceOccupied)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // Calcular densidade para este segundo
                double densityInSecond = occupiedSpaceInSecond
                    .divide(availableSpace, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
                
                totalDensityPercentage += densityInSecond;
                validSeconds++;
            }
            
            // Densidade média ao longo dos 20 minutos
            Double averageDensityPercentage = validSeconds > 0 ? 
                totalDensityPercentage / validSeconds : 0.0;
            
            // Calcular espaço ocupado total para informação
            BigDecimal totalOccupiedSpace = records.stream()
                .filter(r -> r.getVehicleType() != null)
                .map(r -> VehicleSpaceEnum.fromString(r.getVehicleType()))
                .filter(vehicleType -> !vehicleType.isExcludedFromDensityCalculation())
                .map(VehicleSpaceEnum::getSpaceOccupied)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Buscar região usando as coordenadas da câmera
            Region region = regionCache.get(cameraCoordinates);
            String regionName = region != null ? region.getName() : "N/A";
            String cameraLocation = road.getAddress();
            
            statistics.add(VehicleDensityStatisticsDTO.builder()
                .regionName(regionName)
                .cameraId(camera.getId())
                .cameraLocation(cameraLocation)
                .totalVehicles((long) records.size())
                .occupiedSpace(totalOccupiedSpace)
                .availableSpace(availableSpace)
                .densityPercentage(averageDensityPercentage)
                .build());
        }
        
        statistics.sort(Comparator.comparing(VehicleDensityStatisticsDTO::getDensityPercentage).reversed());
        
        return statistics;
    }
    
    @Transactional
    private void processAlertsForStatistics(
            List<VehicleDensityStatisticsDTO> statistics,
            LocalDateTime start,
            LocalDateTime end) {
        
        try {
            Criterion densityCriterion = criterionRepository
                .findByName(DENSITY_CRITERION_NAME)
                .orElse(null);
            
            if (densityCriterion == null) {
                log.warn("Density criterion '{}' not found in database", DENSITY_CRITERION_NAME);
                return;
            }
            
            // Agrupar estatísticas por região e calcular média ponderada
            Map<String, List<VehicleDensityStatisticsDTO>> statsByRegion = statistics.stream()
                .collect(Collectors.groupingBy(VehicleDensityStatisticsDTO::getRegionName));
            
            for (Map.Entry<String, List<VehicleDensityStatisticsDTO>> entry : statsByRegion.entrySet()) {
                String regionName = entry.getKey();
                List<VehicleDensityStatisticsDTO> regionStats = entry.getValue();
                
                // Calcular densidade média ponderada pelo número de veículos
                long totalVehicles = regionStats.stream()
                    .mapToLong(VehicleDensityStatisticsDTO::getTotalVehicles)
                    .sum();
                
                double weightedDensity = regionStats.stream()
                    .mapToDouble(stat -> stat.getDensityPercentage() * stat.getTotalVehicles())
                    .sum() / totalVehicles;
                
                BigDecimal totalOccupied = regionStats.stream()
                    .map(VehicleDensityStatisticsDTO::getOccupiedSpace)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal totalAvailable = regionStats.stream()
                    .map(VehicleDensityStatisticsDTO::getAvailableSpace)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // Criar DTO consolidado para a região
                VehicleDensityStatisticsDTO regionalStat = VehicleDensityStatisticsDTO.builder()
                    .regionName(regionName)
                    .cameraId(null)
                    .cameraLocation(regionStats.size() + " cameras")
                    .totalVehicles(totalVehicles)
                    .occupiedSpace(totalOccupied)
                    .availableSpace(totalAvailable)
                    .densityPercentage(weightedDensity)
                    .build();
                
                processAlertForRegion(regionalStat, densityCriterion, end);
            }
        } catch (Exception e) {
            log.error("Error processing alerts for density statistics: {}", e.getMessage(), e);
        }
    }
    
    private void processAlertForRegion(
            VehicleDensityStatisticsDTO stat,
            Criterion criterion,
            LocalDateTime timestamp) {
        
        try {
            String regionName = stat.getRegionName();
            double densityPercentage = stat.getDensityPercentage();
            short newLevel = calculateAlertLevel(densityPercentage);
            
            log.info("Vehicle Density - Region: {}, Density: {}%, Calculated Level: {}", 
                regionName, String.format("%.2f", densityPercentage), newLevel);
            
            Region region = findRegionByName(regionName);
            if (region == null) {
                log.warn("  - Region '{}' not found in database", regionName);
                return;
            }
            
            Alert openAlert = alertRepository
                .findTopByCriterionAndRegionAndClosedAtIsNullOrderByCreatedAtDesc(criterion, region)
                .orElse(null);
            
            if (openAlert != null) {
                if (openAlert.getLevel() != newLevel) {
                    openAlert.setLevel(newLevel);
                    alertRepository.save(openAlert);
                    log.debug("Updated open Alert ID {} for region '{}': level {} -> {}", 
                        openAlert.getId(), regionName, openAlert.getLevel(), newLevel);
                }
            } else {
                Alert lastAlert = alertRepository
                    .findFirstByCriterionIdAndRegionIdOrderByCreatedAtDesc(criterion.getId(), region.getId())
                    .orElse(null);
                
                if (lastAlert == null || lastAlert.getLevel() != newLevel) {
                    createNewAlert(region, criterion, newLevel, stat, timestamp);
                    log.debug("Created new Alert for region '{}' with level {} (previous level: {})", 
                        regionName, newLevel, lastAlert != null ? lastAlert.getLevel() : "none");
                } else {
                    log.debug("No Alert created for region '{}' - level unchanged at {}", regionName, newLevel);
                }
            }
        } catch (Exception e) {
            log.error("Error processing alert for region {}: {}", stat.getRegionName(), e.getMessage(), e);
        }
    }
    
    private void createNewAlert(
            Region region,
            Criterion criterion,
            short level,
            VehicleDensityStatisticsDTO stat,
            LocalDateTime timestamp) {
        
        String message = String.format(
            "Vehicle density in region %s (camera at %s): %.2f%% (%.2fm occupied of %.2fm available)",
            region.getName(),
            stat.getCameraLocation(),
            stat.getDensityPercentage(),
            stat.getOccupiedSpace(),
            stat.getAvailableSpace()
        );
        
        Alert newAlert = Alert.builder()
            .level(level)
            .message(message)
            .sourceType(SourceTypeEnum.AUTOMATICO)
            .createdAt(timestamp)
            .criterion(criterion)
            .region(region)
            .build();
        
        alertRepository.save(newAlert);
    }
    
    private short calculateAlertLevel(double densityPercentage) {
        if (densityPercentage <= 30) return 1;
        if (densityPercentage <= 50) return 2;
        if (densityPercentage <= 70) return 3;
        if (densityPercentage <= 90) return 4;
        return 5;
    }
    
    private Region findRegionByName(String regionName) {
        return regionRepository.findByName(regionName).orElse(null);
    }
}
