package data.denarius.radarius.scheduler;

import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
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
    private AlertRepository alertRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private CriterionRepository criterionRepository;
    
    @Autowired
    private RootCauseRepository rootCauseRepository;

    @Autowired
    private GeolocationService geolocationService;

    private static final int BATCH_SIZE = 10;
    private static final String DEFAULT_REGION_NAME = "Centro";
    private static final String DEFAULT_CRITERION_NAME = "Speed Above Limit";
    private static final String DEFAULT_ROOT_CAUSE_NAME = "Speeding";

    @Scheduled(fixedRate = 30 * 1000)
    @Transactional
    public void processRadarBaseData() {
        try {
            log.info("Starting radar base data processing...");
            
            Long unprocessedCount = radarBaseDataRepository.countUnprocessedRecords();
            
            if (unprocessedCount == 0) {
                log.info("No new records to process.");
                return;
            }
            
            log.info("Found {} unprocessed records", unprocessedCount);
            
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
                
                log.info("Batch processed. Approximately {} records remaining", 
                    unprocessedCount - recordsToProcess.size());
            }
            
        } catch (Exception e) {
            log.error("General error in radar base data processing: {}", e.getMessage(), e);
        }
    }

    private void processIndividualRecord(RadarBaseData record) {
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
        
        if (isSpeedAboveLimit(record)) {
            createAlert(record, camera, region);
        }
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
        try {
            Optional<Camera> existingCamera = cameraRepository
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
            
        } catch (Exception e) {
            String coordinates = record.getCameraLatitude() + "," + record.getCameraLongitude();
            log.warn("Error creating Camera for coordinates '{}', trying to search again: {}", coordinates, e.getMessage());
            
            Optional<Camera> existingCamera = cameraRepository
                    .findByLatitudeAndLongitude(record.getCameraLatitude(), record.getCameraLongitude());
            
            if (existingCamera.isPresent()) {
                log.info("Camera found after error: {}", coordinates);
                return existingCamera.get();
            }
            
            throw new RuntimeException("Could not create or find Camera for coordinates: " + coordinates, e);
        }
    }
    
    private void createAlert(RadarBaseData record, Camera camera, Region region) {
        Criterion criterion = createOrGetDefaultCriterion();
        RootCause rootCause = createOrGetDefaultRootCause();
        
        Short alertLevel = calculateAlertLevel(record);
        
        String message = String.format(
                "Speed detected: %.1f km/h - Limit: %d km/h - Camera: %s",
                record.getVehicleSpeed(),
                record.getSpeedLimit(),
                record.getCameraId()
        );
        
        Alert newAlert = Alert.builder()
                .level(alertLevel)
                .message(message)
                .sourceType(SourceTypeEnum.AUTOMATICO)
                .createdAt(record.getDateTime())
                .camera(camera)
                .criterion(criterion)
                .rootCause(rootCause)
                .region(region)
                .build();
        
        alertRepository.save(newAlert);
        
        log.debug("Alert created for speeding: Camera {} - Speed {}km/h", 
                record.getCameraId(), record.getVehicleSpeed());
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
    
    private Criterion createOrGetDefaultCriterion() {
        try {
            return criterionRepository.findByName(DEFAULT_CRITERION_NAME)
                    .orElseGet(() -> {
                        Criterion newCriterion = Criterion.builder()
                                .name(DEFAULT_CRITERION_NAME)
                                .description("Criterion to detect speed above regulated limit")
                                .example("Speed > Regulated Limit")
                                .mathExpression("vehicle_speed > speed_limit")
                                .createdAt(LocalDateTime.now())
                                .build();
                        return criterionRepository.save(newCriterion);
                    });
        } catch (Exception e) {
            log.warn("Error creating default Criterion, trying to search again: {}", e.getMessage());
            return criterionRepository.findByName(DEFAULT_CRITERION_NAME)
                    .orElseThrow(() -> new RuntimeException("Could not create or find default Criterion", e));
        }
    }
    
    private RootCause createOrGetDefaultRootCause() {
        try {
            return rootCauseRepository.findByName(DEFAULT_ROOT_CAUSE_NAME)
                    .orElseGet(() -> {
                        RootCause newRootCause = RootCause.builder()
                                .name(DEFAULT_ROOT_CAUSE_NAME)
                                .description("Root cause for speed violations detected by radar")
                                .createdAt(LocalDateTime.now())
                                .build();
                        return rootCauseRepository.save(newRootCause);
                    });
        } catch (Exception e) {
            log.warn("Error creating default RootCause, trying to search again: {}", e.getMessage());
            return rootCauseRepository.findByName(DEFAULT_ROOT_CAUSE_NAME)
                    .orElseThrow(() -> new RuntimeException("Could not create or find default RootCause", e));
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
    
    private boolean isSpeedAboveLimit(RadarBaseData record) {
        return record.getVehicleSpeed() != null && 
               record.getSpeedLimit() != null &&
               record.getVehicleSpeed().compareTo(new BigDecimal(record.getSpeedLimit())) > 0;
    }
    
    private Short calculateAlertLevel(RadarBaseData record) {
        BigDecimal speed = record.getVehicleSpeed();
        Integer limit = record.getSpeedLimit();
        
        BigDecimal difference = speed.subtract(new BigDecimal(limit));
        BigDecimal excessPercentage = difference.divide(new BigDecimal(limit), 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        
        if (excessPercentage.compareTo(new BigDecimal(50)) > 0) return 5;
        if (excessPercentage.compareTo(new BigDecimal(30)) > 0) return 4;
        if (excessPercentage.compareTo(new BigDecimal(20)) > 0) return 3;
        if (excessPercentage.compareTo(new BigDecimal(10)) > 0) return 2;
        return 1;
    }
    
    public void forceProcessing() {
        log.info("Processing forced via endpoint");
        processRadarBaseData();
    }
}
