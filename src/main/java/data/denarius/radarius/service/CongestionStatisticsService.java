package data.denarius.radarius.service;

import data.denarius.radarius.dto.congestion.CongestionStatisticsDTO;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
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
public class CongestionStatisticsService {

    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private CriterionRepository criterionRepository;
    
    private static final String CONGESTION_CRITERION_NAME = "Congestionamento";
    private static final int TIME_WINDOW_MINUTES = 20;
    
    @Transactional(readOnly = true)
    public List<CongestionStatisticsDTO> calculateStatistics(
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

            log.info("Processing {} records from last {} minutes for congestion",
                recentRecords.size(), TIME_WINDOW_MINUTES);
            
            Map<String, List<RadarBaseData>> recordsByRoad = groupRecordsByRoad(
                recentRecords, roadCache);
            
            List<CongestionStatisticsDTO> statistics = calculateCongestionStatistics(
                recordsByRoad, roadCache, regionCache);
            
            processAlertsForStatistics(statistics, timeWindowStart, mostRecentDate);
            
            return statistics;
        } catch (Exception e) {
            log.error("Error calculating congestion statistics: {}", e.getMessage(), e);
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
    
    private List<CongestionStatisticsDTO> calculateCongestionStatistics(
            Map<String, List<RadarBaseData>> recordsByRoad,
            Map<String, Road> roadCache,
            Map<String, Region> regionCache) {
        
        List<CongestionStatisticsDTO> statistics = new ArrayList<>();
        
        for (Map.Entry<String, List<RadarBaseData>> entry : recordsByRoad.entrySet()) {
            String roadAddress = entry.getKey();
            List<RadarBaseData> records = entry.getValue();
            Road road = roadCache.get(roadAddress);
            
            if (road == null || road.getSpeedLimit() == null) {
                continue;
            }
            
            List<RadarBaseData> validRecords = records.stream()
                .filter(r -> r.getVehicleSpeed() != null)
                .collect(Collectors.toList());
            
            if (validRecords.isEmpty()) {
                continue;
            }
            
            BigDecimal totalSpeed = validRecords.stream()
                .map(RadarBaseData::getVehicleSpeed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal averageSpeed = totalSpeed.divide(
                new BigDecimal(validRecords.size()), 2, RoundingMode.HALF_UP);
            
            BigDecimal speedLimit = road.getSpeedLimit();
            BigDecimal relativeSpeed = BigDecimal.ONE.subtract(
                averageSpeed.divide(speedLimit, 4, RoundingMode.HALF_UP));
            Double congestionPercentage = relativeSpeed.multiply(new BigDecimal("100"))
                .doubleValue();
            
            if (congestionPercentage < 0) {
                congestionPercentage = 0.0;
            }
            
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
            
            statistics.add(CongestionStatisticsDTO.builder()
                .regionName(regionName)
                .roadAddress(roadAddress)
                .totalVehicles((long) validRecords.size())
                .averageSpeed(averageSpeed.doubleValue())
                .speedLimit(speedLimit.doubleValue())
                .congestionPercentage(congestionPercentage)
                .build());
        }
        
        statistics.sort(Comparator.comparing(CongestionStatisticsDTO::getCongestionPercentage).reversed());
        
        return statistics;
    }
    
    @Transactional
    private void processAlertsForStatistics(
            List<CongestionStatisticsDTO> statistics,
            LocalDateTime start,
            LocalDateTime end) {
        
        try {
            Criterion congestionCriterion = criterionRepository
                .findByName(CONGESTION_CRITERION_NAME)
                .orElse(null);
            
            if (congestionCriterion == null) {
                log.warn("Congestion criterion '{}' not found in database", CONGESTION_CRITERION_NAME);
                return;
            }
            
            Map<String, List<CongestionStatisticsDTO>> statsByRegion = statistics.stream()
                .collect(Collectors.groupingBy(CongestionStatisticsDTO::getRegionName));
            
            for (Map.Entry<String, List<CongestionStatisticsDTO>> entry : statsByRegion.entrySet()) {
                String regionName = entry.getKey();
                List<CongestionStatisticsDTO> regionStats = entry.getValue();
                
                long totalVehicles = regionStats.stream()
                    .mapToLong(CongestionStatisticsDTO::getTotalVehicles)
                    .sum();
                
                double weightedCongestion = regionStats.stream()
                    .mapToDouble(stat -> stat.getCongestionPercentage() * stat.getTotalVehicles())
                    .sum() / totalVehicles;
                
                CongestionStatisticsDTO regionalStat = CongestionStatisticsDTO.builder()
                    .regionName(regionName)
                    .roadAddress(regionStats.size() + " roads")
                    .totalVehicles(totalVehicles)
                    .averageSpeed(regionStats.stream()
                        .mapToDouble(s -> s.getAverageSpeed() * s.getTotalVehicles())
                        .sum() / totalVehicles)
                    .speedLimit(regionStats.stream()
                        .mapToDouble(CongestionStatisticsDTO::getSpeedLimit)
                        .average()
                        .orElse(0))
                    .congestionPercentage(weightedCongestion)
                    .build();
                
                processAlertForRegion(regionalStat, congestionCriterion, end);
            }
        } catch (Exception e) {
            log.error("Error processing alerts for congestion statistics: {}", e.getMessage(), e);
        }
    }
    
    private void processAlertForRegion(
            CongestionStatisticsDTO stat,
            Criterion criterion,
            LocalDateTime timestamp) {
        
        try {
            String regionName = stat.getRegionName();
            double congestionPercentage = stat.getCongestionPercentage();
            short newLevel = calculateAlertLevel(congestionPercentage);
            
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
            CongestionStatisticsDTO stat,
            LocalDateTime timestamp) {
        
        String message = String.format(
            "Congestion in region %s (road: %s): %.2f%% (avg speed: %.2f km/h, limit: %.2f km/h)",
            region.getName(),
            stat.getRoadAddress(),
            stat.getCongestionPercentage(),
            stat.getAverageSpeed(),
            stat.getSpeedLimit()
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
    
    private short calculateAlertLevel(double congestionPercentage) {
        if (congestionPercentage <= 20) return 1;
        if (congestionPercentage <= 40) return 2;
        if (congestionPercentage <= 60) return 3;
        if (congestionPercentage <= 80) return 4;
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
