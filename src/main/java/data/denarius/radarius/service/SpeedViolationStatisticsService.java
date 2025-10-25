package data.denarius.radarius.service;

import data.denarius.radarius.dto.speedviolation.SpeedViolationStatisticsDTO;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
import data.denarius.radarius.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpeedViolationStatisticsService {

    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private RoadRepository roadRepository;
    
    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private data.denarius.radarius.repository.AlertRepository alertRepository;
    
    @Autowired
    private data.denarius.radarius.repository.CriterionRepository criterionRepository;
    
    private static final BigDecimal SPEED_VIOLATION_THRESHOLD = new BigDecimal("1.10");
    private static final String SPEED_VIOLATION_CRITERION_NAME = "Infrações por excesso de velocidade";
    
    @Transactional(readOnly = true)
    public List<SpeedViolationStatisticsDTO> calculateStatistics(LocalDateTime mostRecentDate) {
        try {
            log.info("Calculating speed violation statistics...");
            
            LocalDateTime twentyMinutesBefore = mostRecentDate.minusMinutes(20);
            
            List<RadarBaseData> lastTwentyMinutesRecords = radarBaseDataRepository
                .findByDateTimeBetween(twentyMinutesBefore, mostRecentDate);
            
            if (lastTwentyMinutesRecords.isEmpty()) {
                log.info("No records found in the last hour");
                return Collections.emptyList();
            }
            
            log.info("Processing {} records from last hour", lastTwentyMinutesRecords.size());
            
            Map<String, Road> roadCache = buildRoadCache(lastTwentyMinutesRecords);
            
            Map<Road, Region> regionCache = buildRegionCache(roadCache.values());
            
            Map<String, List<RadarBaseData>> recordsByRegion = groupRecordsByRegion(
                lastTwentyMinutesRecords, roadCache, regionCache);
            
            List<SpeedViolationStatisticsDTO> statistics = calculateRegionStatistics(recordsByRegion);
            
            processAlertsForStatistics(statistics, twentyMinutesBefore, mostRecentDate);
            
            return statistics;
        } catch (Exception e) {
            log.error("Error calculating speed violation statistics: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private Map<String, Road> buildRoadCache(List<RadarBaseData> records) {
        Set<String> addresses = records.stream()
            .filter(r -> r.getAddress() != null && !r.getAddress().trim().isEmpty())
            .map(this::buildCompleteAddress)
            .collect(Collectors.toSet());
        
        Map<String, Road> roadCache = new HashMap<>();
        for (String address : addresses) {
            roadRepository.findByAddress(address)
                .ifPresent(road -> roadCache.put(address, road));
        }
        
        return roadCache;
    }
    
    private Map<Road, Region> buildRegionCache(Collection<Road> roads) {
        Map<Road, Region> regionCache = new HashMap<>();
        
        for (Road road : roads) {
            List<Camera> cameras = cameraRepository.findByRoad(road);
            if (!cameras.isEmpty() && cameras.get(0).getRegion() != null) {
                regionCache.put(road, cameras.get(0).getRegion());
            }
        }
        return regionCache;
    }
    
    private Map<String, List<RadarBaseData>> groupRecordsByRegion(
            List<RadarBaseData> records,
            Map<String, Road> roadCache,
            Map<Road, Region> regionCache) {
        
        Map<String, List<RadarBaseData>> recordsByRegion = new HashMap<>();
        
        for (RadarBaseData record : records) {
            if (record.getAddress() == null || record.getAddress().trim().isEmpty()) {
                continue;
            }
            
            String address = buildCompleteAddress(record);
            Road road = roadCache.get(address);
            
            if (road != null) {
                Region region = regionCache.get(road);
                if (region != null) {
                    String regionName = region.getName();
                    recordsByRegion
                        .computeIfAbsent(regionName, k -> new ArrayList<>())
                        .add(record);
                }
            }
        }
        
        return recordsByRegion;
    }
    
    private List<SpeedViolationStatisticsDTO> calculateRegionStatistics(
            Map<String, List<RadarBaseData>> recordsByRegion) {
        
        List<SpeedViolationStatisticsDTO> statistics = new ArrayList<>();
        
        for (Map.Entry<String, List<RadarBaseData>> entry : recordsByRegion.entrySet()) {
            String regionName = entry.getKey();
            List<RadarBaseData> records = entry.getValue();
            
            long totalVehicles = records.size();
            long violatingVehicles = records.stream()
                .filter(r -> r.getVehicleSpeed() != null && r.getSpeedLimit() != null)
                .filter(r -> isSpeedViolation(r))
                .count();
            
            double violationRate = totalVehicles > 0 ? 
                (double) violatingVehicles / totalVehicles : 0.0;
            
            statistics.add(SpeedViolationStatisticsDTO.builder()
                .regionName(regionName)
                .totalVehicles(totalVehicles)
                .violatingVehicles(violatingVehicles)
                .violationRate(violationRate)
                .build());
        }
        
        statistics.sort(Comparator.comparing(SpeedViolationStatisticsDTO::getRegionName));
        
        return statistics;
    }
    
    private boolean isSpeedViolation(RadarBaseData record) {
        BigDecimal speedLimit = new BigDecimal(record.getSpeedLimit());
        BigDecimal threshold = speedLimit.multiply(SPEED_VIOLATION_THRESHOLD);
        return record.getVehicleSpeed().compareTo(threshold) >= 0;
    }
    
    @Transactional
    private void processAlertsForStatistics(
            List<SpeedViolationStatisticsDTO> statistics,
            LocalDateTime start,
            LocalDateTime end) {
        
        try {
            Criterion speedViolationCriterion = criterionRepository
                .findByName(SPEED_VIOLATION_CRITERION_NAME)
                .orElse(null);
            
            if (speedViolationCriterion == null) {
                log.warn("Speed violation criterion '{}' not found in database", SPEED_VIOLATION_CRITERION_NAME);
                return;
            }
            
            for (SpeedViolationStatisticsDTO stat : statistics) {
                processAlertForRegion(stat, speedViolationCriterion, end);
            }
        } catch (Exception e) {
            log.error("Error processing alerts for statistics: {}", e.getMessage(), e);
        }
    }
    
    private void processAlertForRegion(
            SpeedViolationStatisticsDTO stat,
            Criterion criterion,
            LocalDateTime timestamp) {
        
        try {
            String regionName = stat.getRegionName();
            double violationRate = stat.getViolationRate();
            short newLevel = calculateAlertLevel(violationRate);
            
            Region region = findRegionByName(regionName);
            if (region == null) {
                log.warn("  - Region '{}' not found in database", regionName);
                return;
            }
            
            Alert lastAlert = alertRepository
                .findTopByCriterionAndRegionAndClosedAtIsNullOrderByCreatedAtDesc(criterion, region)
                .orElse(null);
            
            if (lastAlert == null) {
                createNewAlert(region, criterion, newLevel, stat, timestamp);
            } else {
                lastAlert.setLevel(newLevel);
                alertRepository.save(lastAlert);
            }
        } catch (Exception e) {
            log.error("Error processing alert for region {}: {}", stat.getRegionName(), e.getMessage(), e);
        }
    }
    
    private void createNewAlert(
            Region region,
            Criterion criterion,
            short level,
            SpeedViolationStatisticsDTO stat,
            LocalDateTime timestamp) {
        
        String message = String.format(
            "Speed violation rate in region %s: %.2f%% (%d of %d vehicles exceeding limit by 10%% or more)",
            region.getName(),
            stat.getViolationRate() * 100,
            stat.getViolatingVehicles(),
            stat.getTotalVehicles()
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
    
    private short calculateAlertLevel(double violationRate) {
        double percentage = violationRate * 100;
        
        if (percentage <= 0.5) return 1;
        if (percentage <= 1.0) return 2;
        if (percentage <= 2.0) return 3;
        if (percentage <= 5.0) return 4;
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
