package data.denarius.radarius.service;

import data.denarius.radarius.dto.speedviolation.SpeedViolationStatisticsDTO;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.RadarBaseData;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.entity.Road;
import data.denarius.radarius.repository.CameraRepository;
import data.denarius.radarius.repository.RadarBaseDataRepository;
import data.denarius.radarius.repository.RoadRepository;
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
    
    private static final BigDecimal SPEED_VIOLATION_THRESHOLD = new BigDecimal("1.10");
    
    @Transactional(readOnly = true)
    public List<SpeedViolationStatisticsDTO> calculateStatistics(LocalDateTime mostRecentDate) {
        try {
            log.info("Calculating speed violation statistics...");
            
            LocalDateTime oneHourBefore = mostRecentDate.minusHours(1);
            log.info("Analyzing records from {} to {}", oneHourBefore, mostRecentDate);
            
            // Fetch all records from last hour in one query
            List<RadarBaseData> lastHourRecords = radarBaseDataRepository
                .findByDateTimeBetween(oneHourBefore, mostRecentDate);
            
            if (lastHourRecords.isEmpty()) {
                log.info("No records found in the last hour");
                return Collections.emptyList();
            }
            
            log.info("Processing {} records from last hour", lastHourRecords.size());
            
            // Build address cache
            Map<String, Road> roadCache = buildRoadCache(lastHourRecords);
            
            // Build region cache
            Map<Road, Region> regionCache = buildRegionCache(roadCache.values());
            
            // Group records by region and calculate statistics
            Map<String, List<RadarBaseData>> recordsByRegion = groupRecordsByRegion(
                lastHourRecords, roadCache, regionCache);
            
            // Calculate statistics per region
            List<SpeedViolationStatisticsDTO> statistics = calculateRegionStatistics(recordsByRegion);
            
            // Log results
            logStatistics(statistics, lastHourRecords.size(), oneHourBefore, mostRecentDate);
            
            return statistics;
            
        } catch (Exception e) {
            log.error("Error calculating speed violation statistics: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private Map<String, Road> buildRoadCache(List<RadarBaseData> records) {
        // Get unique addresses from records
        Set<String> addresses = records.stream()
            .filter(r -> r.getAddress() != null && !r.getAddress().trim().isEmpty())
            .map(this::buildCompleteAddress)
            .collect(Collectors.toSet());
        
        // Fetch all roads in one batch query
        Map<String, Road> roadCache = new HashMap<>();
        for (String address : addresses) {
            roadRepository.findByAddress(address)
                .ifPresent(road -> roadCache.put(address, road));
        }
        
        log.debug("Built road cache with {} entries", roadCache.size());
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
        
        log.debug("Built region cache with {} entries", regionCache.size());
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
        
        // Sort by region name
        statistics.sort(Comparator.comparing(SpeedViolationStatisticsDTO::getRegionName));
        
        return statistics;
    }
    
    private boolean isSpeedViolation(RadarBaseData record) {
        BigDecimal speedLimit = new BigDecimal(record.getSpeedLimit());
        BigDecimal threshold = speedLimit.multiply(SPEED_VIOLATION_THRESHOLD);
        return record.getVehicleSpeed().compareTo(threshold) >= 0;
    }
    
    private void logStatistics(
            List<SpeedViolationStatisticsDTO> statistics,
            int totalRecords,
            LocalDateTime start,
            LocalDateTime end) {
        
        log.info("===== SPEED VIOLATION STATISTICS BY REGION =====");
        log.info("Analysis period: {} to {}", start, end);
        log.info("Total records analyzed: {}", totalRecords);
        log.info("");
        
        for (SpeedViolationStatisticsDTO stat : statistics) {
            log.info("Region: {}", stat.getRegionName());
            log.info("  - Total vehicles: {}", stat.getTotalVehicles());
            log.info("  - Violating vehicles: {}", stat.getViolatingVehicles());
            log.info("  - Violation rate: {}%", 
                String.format("%.2f", stat.getViolationRate() * 100));
            log.info("");
        }
        
        log.info("================================================");
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
