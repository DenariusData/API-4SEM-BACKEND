package data.denarius.radarius.repository;

import data.denarius.radarius.entity.RadarBaseData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RadarBaseDataRepository extends JpaRepository<RadarBaseData, Long> {
    
    List<RadarBaseData> findByCityIgnoreCase(String city);
    
    List<RadarBaseData> findByVehicleTypeIgnoreCase(String vehicleType);
    
    List<RadarBaseData> findByCameraId(String cameraId);
    
    List<RadarBaseData> findByDirectionIgnoreCase(String direction);
    
    List<RadarBaseData> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM RadarBaseData r ORDER BY r.dateTime DESC")
    List<RadarBaseData> findAllOrderByDateTimeDesc();
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.vehicleSpeed > r.speedLimit ORDER BY r.dateTime DESC")
    List<RadarBaseData> findVehiclesAboveSpeedLimit();
    
    List<RadarBaseData> findByCityIgnoreCaseAndVehicleTypeIgnoreCase(String city, String vehicleType);
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.dateTime >= :startDate ORDER BY r.dateTime DESC")
    List<RadarBaseData> findRecentRecords(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.cameraId = :cameraId ORDER BY r.dateTime DESC")
    List<RadarBaseData> findByCameraIdOrderByDateTime(@Param("cameraId") String cameraId);
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.processed = false ORDER BY r.dateTime ASC")
    List<RadarBaseData> findUnprocessedRecordsOrderByOldest(Pageable pageable);
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.processed = false ORDER BY r.dateTime ASC")
    List<RadarBaseData> findUnprocessedRecords();
    
    @Query("SELECT COUNT(r) FROM RadarBaseData r WHERE r.processed = false")
    Long countUnprocessedRecords();
    
    @Modifying
    @Query("UPDATE RadarBaseData r SET r.processed = true WHERE r.id = :id")
    void markAsProcessed(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE RadarBaseData r SET r.processed = false")
    void resetAllProcessedFlags();
    
    // Optimized queries for performance
    @Query("SELECT r FROM RadarBaseData r WHERE r.cameraLatitude = :latitude AND r.cameraLongitude = :longitude AND r.dateTime BETWEEN :start AND :end ORDER BY r.dateTime DESC")
    List<RadarBaseData> findByCameraCoordinatesAndDateTimeBetween(
        @Param("latitude") java.math.BigDecimal latitude, 
        @Param("longitude") java.math.BigDecimal longitude, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(r) FROM RadarBaseData r WHERE r.cameraLatitude = :latitude AND r.cameraLongitude = :longitude AND r.dateTime BETWEEN :start AND :end")
    Long countByCameraCoordinatesAndDateTimeBetween(
        @Param("latitude") java.math.BigDecimal latitude, 
        @Param("longitude") java.math.BigDecimal longitude, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end);
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.cameraLatitude = :latitude AND r.cameraLongitude = :longitude AND r.dateTime BETWEEN :start AND :end AND r.vehicleSpeed > (r.speedLimit * :thresholdPercent / 100) ORDER BY r.dateTime DESC")
    List<RadarBaseData> findSpeedViolationsByCameraAndDateTimeBetween(
        @Param("latitude") java.math.BigDecimal latitude, 
        @Param("longitude") java.math.BigDecimal longitude, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end,
        @Param("thresholdPercent") Integer thresholdPercent);
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.cameraLatitude = :latitude AND r.cameraLongitude = :longitude AND r.dateTime BETWEEN :start AND :end AND (r.vehicleType LIKE '%Caminhão%' OR r.vehicleType LIKE '%Van%' OR r.vehicleType LIKE '%Camionete%') ORDER BY r.dateTime DESC")
    List<RadarBaseData> findLargeVehiclesByCameraAndDateTimeBetween(
        @Param("latitude") java.math.BigDecimal latitude, 
        @Param("longitude") java.math.BigDecimal longitude, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end);
    
    @Query("SELECT DISTINCT r.totalLanes FROM RadarBaseData r WHERE r.cameraLatitude = :latitude AND r.cameraLongitude = :longitude AND r.totalLanes IS NOT NULL AND r.dateTime >= :since ORDER BY r.totalLanes DESC")
    List<Integer> findTotalLanesByCameraCoordinatesSince(
        @Param("latitude") java.math.BigDecimal latitude, 
        @Param("longitude") java.math.BigDecimal longitude, 
        @Param("since") LocalDateTime since);
    
    @Query("SELECT r.address, r.number, r.city FROM RadarBaseData r WHERE r.dateTime BETWEEN :start AND :end AND r.processed = true AND r.address IS NOT NULL GROUP BY r.address, r.number, r.city")
    List<Object[]> findDistinctAddressesByDateTimeBetween(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end);
}
