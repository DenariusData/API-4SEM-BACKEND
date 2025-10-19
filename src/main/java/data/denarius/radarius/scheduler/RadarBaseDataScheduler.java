package data.denarius.radarius.scheduler;

import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.VehicleTypeEnum;
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
import java.util.List;
import java.util.Optional;

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
    private ReadingRepository readingRepository;
    
    @Autowired
    private data.denarius.radarius.service.AdvancedAlertService advancedAlertService;

    private static final int BATCH_SIZE = 50;
    private static final String DEFAULT_REGION_NAME = "Centro";

    @Scheduled(fixedRate = 1 * (60 * 1000))
    @Transactional
    public void processRadarBaseDataAndGenerateAlerts() {
        try {
            log.info("Starting radar base data processing with alert generation...");
            
            Long unprocessedCount = radarBaseDataRepository.countUnprocessedRecords();
            
            if (unprocessedCount == 0) {
                log.info("No new records to process.");
                // Still process alerts for any recent data changes
                try {
                    advancedAlertService.processAllCriteriaAndGenerateAlerts();
                    advancedAlertService.deactivateOldAlerts();
                } catch (Exception e) {
                    log.error("Error in alert processing: {}", e.getMessage(), e);
                }
                return;
            }
            
            log.info("Found {} unprocessed records", unprocessedCount);
            
            // Process radar data
            processUnprocessedData();
            
            // Generate alerts based on processed data
            try {
                log.info("Processing criteria calculations and generating alerts");
                advancedAlertService.processAllCriteriaAndGenerateAlerts();
                advancedAlertService.deactivateOldAlerts();
                log.info("Completed alert processing");
            } catch (Exception e) {
                log.error("Error in alert processing: {}", e.getMessage(), e);
            }
            
            log.info("Completed radar base data processing with alert generation");
            
        } catch (Exception e) {
            log.error("Error in scheduler: {}", e.getMessage(), e);
        }
    }
    
    private void processUnprocessedData() {
        try {
            List<RadarBaseData> recordsToProcess = radarBaseDataRepository
                    .findUnprocessedRecordsOrderByOldest(PageRequest.of(0, BATCH_SIZE));
            
            if (!recordsToProcess.isEmpty()) {
                log.info("Processing batch of {} records...", recordsToProcess.size());
                
                for (RadarBaseData record : recordsToProcess) {
                    try {
                        processIndividualRecord(record);
                        radarBaseDataRepository.markAsProcessed(record.getId());
                        log.debug("Record ID {} processed successfully", record.getId());
                        
                    } catch (Exception e) {
                        log.error("Error processing record ID {}: {}", record.getId(), e.getMessage(), e);
                    }
                }
                
                log.info("Batch processed successfully");
            }
            
        } catch (Exception e) {
            log.error("General error in radar base data processing: {}", e.getMessage(), e);
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
        Camera camera = createOrGetCamera(record, road, region);
        
        // Create Reading record for traffic analysis
        createReadingRecord(record, camera);
        
        // Alert generation is now handled by CriterionCalculationScheduler
        // based on calculated criteria levels, not just speed violations
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
            // First, try to find existing camera
            Optional<Camera> existingCamera = cameraRepository
                    .findByLatitudeAndLongitude(record.getCameraLatitude(), record.getCameraLongitude());
            
            if (existingCamera.isPresent()) {
                return existingCamera.get();
            }
            
            // If not found, create new camera with synchronized block to avoid race conditions
            synchronized (this) {
                // Double-check after synchronization
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
            
            // Final attempt to find the camera (it might have been created by another thread)
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
    
    private void createReadingRecord(RadarBaseData record, Camera camera) {
        try {
            // Check for exact duplicate first (same time, camera, and speed)
            Long exactDuplicates = readingRepository.countByCameraAndCreatedAtAndSpeed(
                camera.getId(), record.getDateTime(), record.getVehicleSpeed());
            
            if (exactDuplicates > 0) {
                log.debug("Exact reading duplicate found for camera {} at time {} with speed {}, skipping", 
                    camera.getId(), record.getDateTime(), record.getVehicleSpeed());
                return;
            }
            
            // Check if a similar reading already exists in a time window to avoid near-duplicates
            LocalDateTime startWindow = record.getDateTime().minusSeconds(2);
            LocalDateTime endWindow = record.getDateTime().plusSeconds(2);
            
            List<Reading> existingReadings = readingRepository.findByCameraAndCreatedAtBetween(
                camera, startWindow, endWindow);
            
            // If there are too many readings in the same 4-second window for this camera, skip
            if (existingReadings.size() >= 3) {
                log.debug("Too many readings ({}) for camera {} in time window around {}, skipping to prevent spam", 
                    existingReadings.size(), camera.getId(), record.getDateTime());
                return;
            }
            
            VehicleTypeEnum vehicleType = VehicleTypeEnum.fromString(record.getVehicleType());
            
            Reading reading = Reading.builder()
                .createdAt(record.getDateTime())
                .vehicleType(vehicleType)
                .speed(record.getVehicleSpeed())
                .plate(null) // Plate detection not implemented yet
                .camera(camera)
                .build();
            
            readingRepository.save(reading);
            
            log.debug("Reading record created for camera {} - Vehicle: {} - Speed: {}km/h", 
                camera.getId(), vehicleType.getDisplayName(), record.getVehicleSpeed());
                
        } catch (Exception e) {
            log.error("Error creating reading record for camera {}: {}", camera.getId(), e.getMessage(), e);
        }
    }
    
    public void forceProcessing() {
        log.info("Processing forced via endpoint");
        processUnprocessedData();
    }
}
