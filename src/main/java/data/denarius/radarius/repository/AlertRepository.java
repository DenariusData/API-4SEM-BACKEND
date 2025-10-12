package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

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
    WHERE (:regionId IS NULL OR al.region.id = :regionId)
    AND (:cameraId IS NULL OR a.camera.id = :cameraId)
    AND (:startDate IS NULL OR a.createdAt >= :startDate)
    AND (:endDate IS NULL OR a.createdAt <= :endDate)
    ORDER BY a.createdAt DESC
    """)
    Page<Alert> findWithFilters(
            @Param("regionId") Integer regionId,
            @Param("cameraId") Integer cameraId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}
