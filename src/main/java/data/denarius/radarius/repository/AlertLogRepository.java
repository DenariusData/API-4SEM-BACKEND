package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Integer> {
    @Query("""
        SELECT al
        FROM AlertLog al
        WHERE al.region.id = :regionId
        AND al.criterion.id = :criterionId
        ORDER BY al.createdAt DESC
        LIMIT 1
    """)
    Optional<AlertLog> findLatestByCriterionAndRegion(@Param("criterionId") Integer criterionId,
                                                      @Param("regionId") Integer regionId);

    List<AlertLog> findByAlert(Alert alert);
}
