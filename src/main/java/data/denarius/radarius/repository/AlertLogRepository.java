package data.denarius.radarius.repository;

import data.denarius.radarius.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Integer> {
    @Query("""
        SELECT al
        FROM AlertLog al
        WHERE al.region.id = :regionId
        ORDER BY al.createdAt DESC
        LIMIT 1
    """)
    Optional<AlertLog> findLatestByRegion(@Param("regionId") Integer regionId);
}
