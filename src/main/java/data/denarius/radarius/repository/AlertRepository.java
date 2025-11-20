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
        AND (:startDate IS NULL OR a.createdAt >= :startDate)
        AND (:endDate IS NULL OR a.createdAt <= :endDate)
        ORDER BY a.createdAt DESC
    """)
    Page<Alert> findWithFilters(@Param("regionIds") List<Integer> regionIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    Optional<Alert> findFirstByCriterionIdAndRegionIdOrderByCreatedAtDesc(Integer criterionId, Integer regionId);
    
    Optional<Alert> findTopBySourceTypeAndCriterionIdAndRegionIdOrderByCreatedAtDesc(
        SourceTypeEnum sourceType, Integer criterionId, Integer regionId);
    
    @Query("SELECT a FROM Alert a WHERE a.sourceType = :sourceType AND a.criterion.id = :criterionId AND a.region.id = :regionId AND a.closedAt IS NULL ORDER BY a.createdAt DESC")
    Optional<Alert> findActiveAlertBySourceTypeAndCriterionIdAndRegionId(
        @Param("sourceType") SourceTypeEnum sourceType, 
        @Param("criterionId") Integer criterionId, 
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
    
    @Query("SELECT a FROM Alert a WHERE a.criterion.id = :criterionId AND a.region.id = :regionId AND a.id != :excludeId ORDER BY a.createdAt DESC")
    List<Alert> findByCriterionIdAndRegionIdExcludingIdOrderByCreatedAtDesc(
        @Param("criterionId") Integer criterionId, 
        @Param("regionId") Integer regionId, 
        @Param("excludeId") Integer excludeId,
        Pageable pageable);

    List<Alert> findTop5ByRegionIdAndClosedAtIsNullOrderByLevelDescCreatedAtDesc(Integer regionId);

    List<Alert> findTop5ByRegionIdAndCriterionIdAndClosedAtIsNullOrderByLevelDescCreatedAtDesc(
            Integer regionId, Integer criterionId);

    @Query("""
        SELECT new map(a.region.id as regionId, CAST(CEILING(AVG(a.level)) as java.lang.Integer) as level)
        FROM Alert a
        WHERE a.closedAt IS NULL
        GROUP BY a.region.id
    """)
    List<java.util.Map<String, Object>> findAverageLevelPerRegion();

}



