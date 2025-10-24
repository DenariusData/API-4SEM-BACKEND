package data.denarius.radarius.scheduler;

import data.denarius.radarius.entity.*;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.GeolocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RadarBaseDataScheduler {

    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;
    
    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private RoadRepository roadRepository;
    
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private GeolocationService geolocationService;
    
    @Autowired
    private data.denarius.radarius.service.AdvancedAlertService advancedAlertService;

    private static final int UNPROCESSED_RECORDS_BATCH_SIZE = 1000;
    private static final int PROCESS_MEMORY_RECORDS_BATCH_SIZE = 100;
    private static final String DEFAULT_REGION_NAME = "Centro";
    private static final BigDecimal SPEED_VIOLATION_THRESHOLD = new BigDecimal("1.10");

    @Scheduled(fixedRate = 1 * 60 * (60 * 1000))
    @Transactional
    public void processRadarBaseDataAndGenerateAlerts() {
        try {
            log.info("Starting radar base data processing...");
            
            List<RadarBaseData> unprocessedRecords = radarBaseDataRepository
                    .findUnprocessedRecordsOrderByOldest(PageRequest.of(0, UNPROCESSED_RECORDS_BATCH_SIZE));
            
            if (unprocessedRecords.isEmpty()) {
                log.info("No new records to process.");
                try {
                    advancedAlertService.deactivateOldAlerts();
                } catch (Exception e) {
                    log.error("Error deactivating old alerts: {}", e.getMessage(), e);
                }
                return;
            }
            
            log.info("Found {} unprocessed records", unprocessedRecords.size());
            
            processUnprocessedData(unprocessedRecords);
            
            try {
                advancedAlertService.deactivateOldAlerts();
                log.info("Old alerts deactivated successfully");
            } catch (Exception e) {
                log.error("Error deactivating old alerts: {}", e.getMessage(), e);
            }
            
            log.info("Completed radar base data processing");
            
        } catch (Exception e) {
            log.error("Error in scheduler: {}", e.getMessage(), e);
        }
    }
    
    private void processUnprocessedData(List<RadarBaseData> unprocessedRecords) {
        try {
            int totalProcessed = 0;
            for (int i = 0; i < unprocessedRecords.size(); i += PROCESS_MEMORY_RECORDS_BATCH_SIZE) {
                int endIndex = Math.min(i + PROCESS_MEMORY_RECORDS_BATCH_SIZE, unprocessedRecords.size());
                List<RadarBaseData> batch = unprocessedRecords.subList(i, endIndex);
                
                log.info("Processing batch {}/{}: {} records", 
                    (i / PROCESS_MEMORY_RECORDS_BATCH_SIZE) + 1, 
                    (unprocessedRecords.size() + PROCESS_MEMORY_RECORDS_BATCH_SIZE - 1) / PROCESS_MEMORY_RECORDS_BATCH_SIZE, 
                    batch.size());
                
                for (RadarBaseData record : batch) {
                    try {
                        processIndividualRecord(record);
                        radarBaseDataRepository.markAsProcessed(record.getId());
                        totalProcessed++;
                        log.debug("Record ID {} processed successfully", record.getId());
                    } catch (Exception e) {
                        log.error("Error processing record ID {}: {}", record.getId(), e.getMessage(), e);
                    }
                }
            }
            
            log.info("Completed processing {} records", totalProcessed);
            
            if (!unprocessedRecords.isEmpty()) {
                RadarBaseData mostRecentRecord = unprocessedRecords.stream()
                    .filter(r -> r.getDateTime() != null)
                    .max(Comparator.comparing(RadarBaseData::getDateTime))
                    .orElse(null);
                    
                if (mostRecentRecord != null) {
                    calculateSpeedViolationStatistics(mostRecentRecord.getDateTime());
                }
            }
            
        } catch (Exception e) {
            log.error("General error in radar base data processing: {}", e.getMessage(), e);
        }
    }
    
    private void calculateSpeedViolationStatistics(LocalDateTime mostRecentDate) {
        try {
            log.info("Calculating speed violation statistics...");
            
            LocalDateTime oneHourBefore = mostRecentDate.minusHours(1);
            
            log.info("Analyzing records from {} to {}", oneHourBefore, mostRecentDate);
            
            List<RadarBaseData> lastHourRecords = radarBaseDataRepository
                .findByDateTimeBetween(oneHourBefore, mostRecentDate);
            
            if (lastHourRecords.isEmpty()) {
                log.info("No records found in the last hour");
                return;
            }
            
            Map<String, List<RadarBaseData>> recordsByRoad = lastHourRecords.stream()
                .filter(r -> r.getAddress() != null && !r.getAddress().trim().isEmpty())
                .collect(Collectors.groupingBy(r -> buildCompleteAddress(r)));
            
            Map<String, Double> violationRateByRoad = new HashMap<>();
            Map<String, Road> roadByAddress = new HashMap<>();
            
            for (Map.Entry<String, List<RadarBaseData>> entry : recordsByRoad.entrySet()) {
                String address = entry.getKey();
                List<RadarBaseData> records = entry.getValue();
                
                long totalVehicles = records.size();
                long violatingVehicles = records.stream()
                    .filter(r -> r.getVehicleSpeed() != null && r.getSpeedLimit() != null)
                    .filter(r -> {
                        BigDecimal speedLimit = new BigDecimal(r.getSpeedLimit());
                        BigDecimal threshold = speedLimit.multiply(SPEED_VIOLATION_THRESHOLD);
                        return r.getVehicleSpeed().compareTo(threshold) >= 0;
                    })
                    .count();
                
                double violationRate = totalVehicles > 0 ? 
                    (double) violatingVehicles / totalVehicles : 0.0;
                    
                violationRateByRoad.put(address, violationRate);
                
                Optional<Road> roadOpt = roadRepository.findByAddress(address);
                roadOpt.ifPresent(road -> roadByAddress.put(address, road));
            }
            
            Map<String, List<Map.Entry<String, Double>>> violationsByRegion = new HashMap<>();
            
            for (Map.Entry<String, Double> entry : violationRateByRoad.entrySet()) {
                String address = entry.getKey();
                Road road = roadByAddress.get(address);
                
                if (road != null) {
                    List<Camera> cameras = cameraRepository.findByRoad(road);
                    if (!cameras.isEmpty()) {
                        Region region = cameras.get(0).getRegion();
                        String regionName = region != null ? region.getName() : "Unknown";
                        
                        violationsByRegion
                            .computeIfAbsent(regionName, k -> new ArrayList<>())
                            .add(entry);
                    }
                }
            }
            
            log.info("===== SPEED VIOLATION STATISTICS BY REGION =====");
            log.info("Analysis period: {} to {}", oneHourBefore, mostRecentDate);
            log.info("Total records analyzed: {}", lastHourRecords.size());
            log.info("Total roads analyzed: {}", recordsByRoad.size());
            log.info("");
            
            for (Map.Entry<String, List<Map.Entry<String, Double>>> regionEntry : 
                    violationsByRegion.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.toList())) {
                
                String regionName = regionEntry.getKey();
                List<Map.Entry<String, Double>> roads = regionEntry.getValue();
                
                double averageViolationRate = roads.stream()
                    .mapToDouble(Map.Entry::getValue)
                    .average()
                    .orElse(0.0);
                
                log.info("Region: {}", regionName);
                log.info("  - Number of roads: {}", roads.size());
                log.info("  - Average violation rate: {}%", String.format("%.2f", averageViolationRate * 100));
                
                // Show top 3 roads with highest violations
                roads.stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(3)
                    .forEach(road -> 
                        log.info("    * {}: {}%", 
                            road.getKey().length() > 60 ? 
                                road.getKey().substring(0, 57) + "..." : road.getKey(), 
                            String.format("%.2f", road.getValue() * 100))
                    );
                log.info("");
            }
            
            log.info("================================================");
            
        } catch (Exception e) {
            log.error("Error calculating speed violation statistics: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private void processIndividualRecord(RadarBaseData record) {
        log.debug("Processing radar data record ID: {}", record.getId());
        
        if (record.getCameraLatitude() == null || record.getCameraLongitude() == null) {
            log.warn("Record ID {} has invalid coordinates, skipping processing", record.getId());
            return;
        }
        
        if (record.getAddress() == null || record.getAddress().trim().isEmpty()) {
            log.warn("Record ID {} has invalid address, skipping processing", record.getId());
            return;
        }
        
        Road road = createOrGetRoad(record);
        Region region = determineRegionFromCoordinates(record.getCameraLatitude(), record.getCameraLongitude());
        createOrGetCamera(record, road, region);
    }
    
    private Road createOrGetRoad(RadarBaseData record) {
        String completeAddress = buildCompleteAddress(record);
        
        try {
            Optional<Road> existingRoad = roadRepository.findByAddress(completeAddress);
            
            if (existingRoad.isPresent()) {
                return existingRoad.get();
            }
            
            Road newRoad = Road.builder()
                    .address(completeAddress)
                    .speedLimit(new BigDecimal(record.getSpeedLimit()))
                    .createdAt(LocalDateTime.now())
                    .build();
            
            return roadRepository.save(newRoad);
            
        } catch (Exception e) {
            log.warn("Error creating Road for address '{}', trying to search again: {}", completeAddress, e.getMessage());
            
            Optional<Road> existingRoad = roadRepository.findByAddress(completeAddress);
            if (existingRoad.isPresent()) {
                log.info("Road found after error: {}", completeAddress);
                return existingRoad.get();
            }
            
            throw new RuntimeException("Could not create or find Road for address: " + completeAddress, e);
        }
    }
    
    private Camera createOrGetCamera(RadarBaseData record, Road road, Region region) {
        String coordinates = record.getCameraLatitude() + "," + record.getCameraLongitude();
        
        try {
            Optional<Camera> existingCamera = cameraRepository
                    .findByLatitudeAndLongitude(record.getCameraLatitude(), record.getCameraLongitude());
            
            if (existingCamera.isPresent()) {
                return existingCamera.get();
            }
            
            synchronized (this) {
                existingCamera = cameraRepository
                        .findByLatitudeAndLongitude(record.getCameraLatitude(), record.getCameraLongitude());
                
                if (existingCamera.isPresent()) {
                    return existingCamera.get();
                }
                
                Camera newCamera = Camera.builder()
                        .latitude(record.getCameraLatitude())
                        .longitude(record.getCameraLongitude())
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .road(road)
                        .region(region)
                        .build();
                
                return cameraRepository.save(newCamera);
            }
            
        } catch (Exception e) {
            log.warn("Error with Camera for coordinates '{}', trying final search: {}", coordinates, e.getMessage());
            
            Optional<Camera> existingCamera = cameraRepository
                    .findByLatitudeAndLongitude(record.getCameraLatitude(), record.getCameraLongitude());
            
            if (existingCamera.isPresent()) {
                log.info("Camera found in final search: {}", coordinates);
                return existingCamera.get();
            }
            
            log.error("Could not create or find Camera for coordinates: {}", coordinates, e);
            throw new RuntimeException("Could not create or find Camera for coordinates: " + coordinates, e);
        }
    }
    
    private Region determineRegionFromCoordinates(BigDecimal latitude, BigDecimal longitude) {
        Optional<Region> regionOpt = geolocationService.determineRegionFromCoordinates(latitude, longitude);
        
        if (regionOpt.isPresent()) {
            return regionOpt.get();
        } else {
            log.warn("No region found for coordinates ({}, {}), using default region", latitude, longitude);
            return createOrGetDefaultRegion();
        }
    }
    
    private Region createOrGetDefaultRegion() {
        try {
            return regionRepository.findByName(DEFAULT_REGION_NAME)
                    .orElseGet(() -> {
                        Region newRegion = Region.builder()
                                .name(DEFAULT_REGION_NAME)
                                .createdAt(LocalDateTime.now())
                                .build();
                        return regionRepository.save(newRegion);
                    });
        } catch (Exception e) {
            log.warn("Error creating default Region, trying to search again: {}", e.getMessage());
            return regionRepository.findByName(DEFAULT_REGION_NAME)
                    .orElseThrow(() -> new RuntimeException("Could not create or find default Region", e));
        }
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
    
    public void forceProcessing() {
        log.info("Processing forced via endpoint");
        List<RadarBaseData> unprocessedRecords = radarBaseDataRepository.findUnprocessedRecords();
        if (!unprocessedRecords.isEmpty()) {
            processUnprocessedData(unprocessedRecords);
        } else {
            log.info("No unprocessed records to force process");
        }
    }
}
