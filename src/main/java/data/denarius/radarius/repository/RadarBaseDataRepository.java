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
    
    List<RadarBaseData> findByCameraId(String cameraId);
    
    List<RadarBaseData> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM RadarBaseData r WHERE r.processed = false ORDER BY r.dateTime ASC")
    List<RadarBaseData> findUnprocessedRecordsOrderByOldest(Pageable pageable);
    
    @Modifying
    @Query("UPDATE RadarBaseData r SET r.processed = true WHERE r.id IN :ids")
    void markMultipleAsProcessed(@Param("ids") List<Long> ids);
}
