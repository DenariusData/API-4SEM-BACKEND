package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Integer> {
    
    List<Reading> findByCameraAndCreatedAtBetween(Camera camera, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM Reading r WHERE r.camera.id = :cameraId AND r.createdAt >= :since ORDER BY r.createdAt DESC")
    List<Reading> findRecentReadingsByCamera(@Param("cameraId") Integer cameraId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(r) FROM Reading r WHERE r.camera.id = :cameraId AND r.createdAt BETWEEN :start AND :end")
    Long countReadingsByCameraInTimeWindow(@Param("cameraId") Integer cameraId, 
                                           @Param("start") LocalDateTime start, 
                                           @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(r) FROM Reading r WHERE r.camera.id = :cameraId AND r.createdAt = :exactTime AND r.speed = :speed")
    Long countByCameraAndCreatedAtAndSpeed(@Param("cameraId") Integer cameraId, 
                                          @Param("exactTime") LocalDateTime exactTime, 
                                          @Param("speed") java.math.BigDecimal speed);
    
    void deleteByCreatedAtBefore(LocalDateTime before);
}
