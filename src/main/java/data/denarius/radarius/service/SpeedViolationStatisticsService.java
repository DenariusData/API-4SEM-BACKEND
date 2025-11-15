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

@Slf4j
@Service
public class SpeedViolationStatisticsService {

    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private data.denarius.radarius.repository.AlertRepository alertRepository;
    
    @Autowired
    private data.denarius.radarius.repository.CriterionRepository criterionRepository;
    
    private static final BigDecimal SPEED_VIOLATION_THRESHOLD = new BigDecimal("1.10");
    private static final String SPEED_VIOLATION_CRITERION_NAME = "Infrações por excesso de velocidade";
    private static final int TIME_WINDOW_MINUTES = 20;
    
    @Transactional(readOnly = true)
    public List<SpeedViolationStatisticsDTO> calculateStatistics(
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
            
            log.info("Processing {} records from last {} minutes for speed violation", recentRecords.size(), TIME_WINDOW_MINUTES);
            
            Map<String, List<RadarBaseData>> recordsByRegion = groupRecordsByRegion(
                recentRecords, roadCache, regionCache);
            
            List<SpeedViolationStatisticsDTO> statistics = calculateRegionStatistics(recordsByRegion);
            
            processAlertsForStatistics(statistics, timeWindowStart, mostRecentDate);
            
            return statistics;
        } catch (Exception e) {
            log.error("Error calculating speed violation statistics: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private Map<String, List<RadarBaseData>> groupRecordsByRegion(
            List<RadarBaseData> records,
            Map<String, Road> roadCache,
            Map<String, Region> regionCache) {
        
        Map<String, List<RadarBaseData>> recordsByRegion = new HashMap<>();
        
        for (RadarBaseData record : records) {
            if (record.getAddress() == null || record.getAddress().trim().isEmpty()) {
                continue;
            }
            
            if (record.getCameraLatitude() == null || record.getCameraLongitude() == null) {
                continue;
            }
            
            String coordinates = record.getCameraLatitude() + "," + record.getCameraLongitude();
            Region region = regionCache.get(coordinates);
            
            if (region != null) {
                String regionName = region.getName();
                recordsByRegion
                    .computeIfAbsent(regionName, k -> new ArrayList<>())
                    .add(record);
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

    private String buildAlertMessage(Region region, SpeedViolationStatisticsDTO stat) {
        return String.format(
            "Infração de velocidade em região %s: %.2f%% (%d de %d veículos excedendo o limite em 10%% ou mais)",
            region.getName(),
            stat.getViolationRate() * 100,
            stat.getViolatingVehicles(),
            stat.getTotalVehicles()
        );
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
    
    private short calculateAlertLevel(double violationRate) {
        double percentage = violationRate * 100;
        
        if (percentage <= 0.1) return 1;
        if (percentage <= 0.5) return 2;
        if (percentage <= 1.0) return 3;
        if (percentage <= 5.0) return 4;
        return 5;
    }
    
    private Region findRegionByName(String regionName) {
        return regionRepository.findByName(regionName).orElse(null);
    }
}
