package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.enums.SourceTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {

    @Query("""
        SELECT a
        FROM Alert a
        JOIN a.logs al
        WHERE al.region.id = :regionId
        ORDER BY al.createdAt DESC
    """)
    List<Alert> findTop10ByRegion(@Param("regionId") Integer regionId, Pageable pageable);

    @Query("""
        SELECT DISTINCT a
        FROM Alert a
        LEFT JOIN a.logs al
        WHERE (:regionIds IS NULL OR al.region.id IN :regionIds)
        AND (:cameraId IS NULL OR a.camera.id = :cameraId)
        AND (:startDate IS NULL OR a.createdAt >= :startDate)
        AND (:endDate IS NULL OR a.createdAt <= :endDate)
        ORDER BY a.createdAt DESC
    """)
    Page<Alert> findWithFilters(@Param("regionIds") List<Integer> regionIds,
            @Param("cameraId") Integer cameraId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    Optional<Alert> findFirstByCriterionIdAndRegionIdOrderByCreatedAtDesc(Integer criterionId, Integer regionId);
    
    Optional<Alert> findTopBySourceTypeAndCriterionIdAndCameraIdAndRegionIdOrderByCreatedAtDesc(
        SourceTypeEnum sourceType, Integer criterionId, Integer cameraId, Integer regionId);
    
    @Query("SELECT a FROM Alert a WHERE a.sourceType = :sourceType AND a.criterion.id = :criterionId AND a.camera.id = :cameraId AND a.region.id = :regionId AND a.closedAt IS NULL ORDER BY a.createdAt DESC")
    Optional<Alert> findActiveAlertBySourceTypeAndCriterionIdAndCameraIdAndRegionId(
        @Param("sourceType") SourceTypeEnum sourceType, 
        @Param("criterionId") Integer criterionId, 
        @Param("cameraId") Integer cameraId, 
        @Param("regionId") Integer regionId);
    
    @Query("SELECT a FROM Alert a WHERE a.closedAt IS NULL AND a.createdAt < :threshold")
    List<Alert> findActiveAlertsOlderThan(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT a FROM Alert a WHERE a.closedAt IS NULL")
    List<Alert> findActiveAlerts();
    
    @Query("SELECT a FROM Alert a WHERE a.closedAt IS NULL AND a.region.id = :regionId")
    List<Alert> findActiveAlertsByRegionId(@Param("regionId") Integer regionId);
    
    @Query("SELECT a FROM Alert a WHERE a.closedAt IS NULL AND a.level = :level")
    List<Alert> findActiveAlertsByLevel(@Param("level") Integer level);
    
    Optional<Alert> findTopByCriterionAndRegionAndClosedAtIsNullOrderByCreatedAtDesc(
        Criterion criterion, Region region);
}
