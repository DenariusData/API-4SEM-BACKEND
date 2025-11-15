package data.denarius.radarius.service;

import data.denarius.radarius.dto.largevehicle.LargeVehicleStatisticsDTO;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
import data.denarius.radarius.enums.VehicleSpaceEnum;
import data.denarius.radarius.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LargeVehicleStatisticsService {

    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private CriterionRepository criterionRepository;
    
    private static final String LARGE_VEHICLE_CRITERION_NAME = "Circulação de veículos de grande porte";
    private static final int TIME_WINDOW_MINUTES = 20;
    
    @Transactional(readOnly = true)
    public List<LargeVehicleStatisticsDTO> calculateStatistics(
            LocalDateTime mostRecentDate,
            Map<String, Road> roadCache,
            Map<String, Region> regionCache) {
        try {
            LocalDateTime timeWindowStart = mostRecentDate.minusMinutes(TIME_WINDOW_MINUTES);
            
            List<RadarBaseData> recentRecords = radarBaseDataRepository
                .findByDateTimeBetween(timeWindowStart, mostRecentDate);
            
            if (recentRecords.isEmpty()) {
                log.info("No records found in the last {} minutes", TIME_WINDOW_MINUTES);
                return Collections.emptyList();
            }

            log.info("Processing {} records from last {} minutes for large vehicles",
                recentRecords.size(), TIME_WINDOW_MINUTES);
            
            Map<String, List<RadarBaseData>> recordsByRoad = groupRecordsByRoad(
                recentRecords, roadCache);
            
            List<LargeVehicleStatisticsDTO> statistics = calculateLargeVehicleStatistics(
                recordsByRoad, roadCache, regionCache);
            
            processAlertsForStatistics(statistics, timeWindowStart, mostRecentDate);
            
            return statistics;
        } catch (Exception e) {
            log.error("Error calculating large vehicle statistics: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private Map<String, List<RadarBaseData>> groupRecordsByRoad(
            List<RadarBaseData> records,
            Map<String, Road> roadCache) {
        
        Map<String, List<RadarBaseData>> recordsByRoad = new HashMap<>();
        
        for (RadarBaseData record : records) {
            if (record.getAddress() == null || record.getAddress().trim().isEmpty()) {
                continue;
            }
            
            String address = buildCompleteAddress(record);
            if (roadCache.containsKey(address)) {
                recordsByRoad
                    .computeIfAbsent(address, k -> new ArrayList<>())
                    .add(record);
            }
        }
        
        return recordsByRoad;
    }
    
    private List<LargeVehicleStatisticsDTO> calculateLargeVehicleStatistics(
            Map<String, List<RadarBaseData>> recordsByRoad,
            Map<String, Road> roadCache,
            Map<String, Region> regionCache) {
        
        List<LargeVehicleStatisticsDTO> statistics = new ArrayList<>();
        
        for (Map.Entry<String, List<RadarBaseData>> entry : recordsByRoad.entrySet()) {
            String roadAddress = entry.getKey();
            List<RadarBaseData> records = entry.getValue();
            Road road = roadCache.get(roadAddress);
            
            if (road == null) {
                continue;
            }
            
            List<RadarBaseData> validRecords = records.stream()
                .filter(r -> r.getVehicleType() != null)
                .filter(r -> !VehicleSpaceEnum.fromString(r.getVehicleType())
                    .isExcludedFromDensityCalculation())
                .collect(Collectors.toList());
            
            if (validRecords.isEmpty()) {
                continue;
            }
            
            long largeVehicles = validRecords.stream()
                .map(r -> VehicleSpaceEnum.fromString(r.getVehicleType()))
                .filter(VehicleSpaceEnum::isLargeVehicle)
                .count();
            
            long totalVehicles = validRecords.size();
            double largeVehiclePercentage = totalVehicles > 0 ? 
                (double) largeVehicles / totalVehicles * 100.0 : 0.0;
            
            String regionName = "N/A";
            Region region = null;
            
            if (!validRecords.isEmpty()) {
                RadarBaseData firstRecord = validRecords.get(0);
                if (firstRecord.getCameraLatitude() != null && firstRecord.getCameraLongitude() != null) {
                    String coordinates = firstRecord.getCameraLatitude() + "," + firstRecord.getCameraLongitude();
                    region = regionCache.get(coordinates);
                    
                    if (region != null) {
                        regionName = region.getName();
                    } else {
                        log.debug("Region not found in cache for coordinates {} on road {}", 
                            coordinates, roadAddress);
                    }
                }
            }
            
            statistics.add(LargeVehicleStatisticsDTO.builder()
                .regionName(regionName)
                .roadAddress(roadAddress)
                .totalVehicles(totalVehicles)
                .largeVehicles(largeVehicles)
                .largeVehiclePercentage(largeVehiclePercentage)
                .build());
        }
        
        statistics.sort(Comparator.comparing(LargeVehicleStatisticsDTO::getLargeVehiclePercentage).reversed());
        
        return statistics;
    }
    
    @Transactional
    private void processAlertsForStatistics(
            List<LargeVehicleStatisticsDTO> statistics,
            LocalDateTime start,
            LocalDateTime end) {
        
        try {
            Criterion largeVehicleCriterion = criterionRepository
                .findByName(LARGE_VEHICLE_CRITERION_NAME)
                .orElse(null);
            
            if (largeVehicleCriterion == null) {
                log.warn("Large vehicle criterion '{}' not found in database", LARGE_VEHICLE_CRITERION_NAME);
                return;
            }
            
            Map<String, List<LargeVehicleStatisticsDTO>> statsByRegion = statistics.stream()
                .collect(Collectors.groupingBy(LargeVehicleStatisticsDTO::getRegionName));
            
            for (Map.Entry<String, List<LargeVehicleStatisticsDTO>> entry : statsByRegion.entrySet()) {
                String regionName = entry.getKey();
                List<LargeVehicleStatisticsDTO> regionStats = entry.getValue();
                
                long totalVehicles = regionStats.stream()
                    .mapToLong(LargeVehicleStatisticsDTO::getTotalVehicles)
                    .sum();
                
                long totalLargeVehicles = regionStats.stream()
                    .mapToLong(LargeVehicleStatisticsDTO::getLargeVehicles)
                    .sum();
                
                double weightedPercentage = totalVehicles > 0 ? 
                    (double) totalLargeVehicles / totalVehicles * 100.0 : 0.0;
                
                LargeVehicleStatisticsDTO regionalStat = LargeVehicleStatisticsDTO.builder()
                    .regionName(regionName)
                    .roadAddress(regionStats.size() + " roads")
                    .totalVehicles(totalVehicles)
                    .largeVehicles(totalLargeVehicles)
                    .largeVehiclePercentage(weightedPercentage)
                    .build();
                
                processAlertForRegion(regionalStat, largeVehicleCriterion, end);
            }
        } catch (Exception e) {
            log.error("Error processing alerts for large vehicle statistics: {}", e.getMessage(), e);
        }
    }
    
    private void processAlertForRegion(
            LargeVehicleStatisticsDTO stat,
            Criterion criterion,
            LocalDateTime timestamp) {
        
        try {
            String regionName = stat.getRegionName();
            double largeVehiclePercentage = stat.getLargeVehiclePercentage();
            short newLevel = calculateAlertLevel(largeVehiclePercentage);
            
            Region region = findRegionByName(regionName);
            if (region == null) {
                log.warn("  - Region '{}' not found in database", regionName);
                return;
            }
            
            Alert openAlert = alertRepository
                .findTopByCriterionAndRegionAndClosedAtIsNullOrderByCreatedAtDesc(criterion, region)
                .orElse(null);
            
            String newMessage = buildAlertMessage(region, stat);
            
            if (openAlert != null) {
                boolean needsUpdate = !newMessage.equals(openAlert.getMessage()) || 
                                    openAlert.getLevel() != newLevel;
                
                if (needsUpdate) {
                    openAlert.setLevel(newLevel);
                    openAlert.setMessage(newMessage);
                    alertRepository.save(openAlert);
                    log.debug("Updated open Alert ID {} for region '{}'", openAlert.getId(), regionName);
                }
            } else {
                createNewAlert(region, criterion, newLevel, newMessage, timestamp);
                log.debug("Created new Alert for region '{}' with level {}", regionName, newLevel);
            }
        } catch (Exception e) {
            log.error("Error processing alert for region {}: {}", stat.getRegionName(), e.getMessage(), e);
        }
    }
    
    private void createNewAlert(
            Region region,
            Criterion criterion,
            short level,
            String message,
            LocalDateTime timestamp) {
        
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
    
    private String buildAlertMessage(Region region, LargeVehicleStatisticsDTO stat) {
        return String.format(
            " Circulação de veículos grandes em região %s (rodovia: %s): %.2f%% (%d de %d veículos)",
            region.getName(),
            stat.getRoadAddress(),
            stat.getLargeVehiclePercentage(),
            stat.getLargeVehicles(),
            stat.getTotalVehicles()
        );
    }
    
    private short calculateAlertLevel(double largeVehiclePercentage) {
        if (largeVehiclePercentage <= 10) return 1;
        if (largeVehiclePercentage <= 20) return 2;
        if (largeVehiclePercentage <= 35) return 3;
        if (largeVehiclePercentage <= 50) return 4;
        return 5;
    }
    
    private Region findRegionByName(String regionName) {
        return regionRepository.findByName(regionName).orElse(null);
    }
    
    private String buildCompleteAddress(RadarBaseData record) {
        StringBuilder address = new StringBuilder(record.getAddress());
        
        if (record.getNumber() != null && !record.getNumber().trim().isEmpty()) {
            address.append(", ").append(record.getNumber());
        }
        
        if (record.getCity() != null && !record.getCity().trim().isEmpty()) {
            address.append(" - ").append(record.getCity());
        }
        
        return address.toString();
    }
}
