package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.AlertLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Integer> {
    Optional<AlertLog> findFirstByCriterionIdAndRegionIdOrderByCreatedAtDesc(Integer criterionId, Integer regionId);

    List<AlertLog> findByAlert(Alert alert);
    
    Optional<AlertLog> findTopByAlertOrderByCreatedAtDesc(Alert alert);
    
    List<AlertLog> findTop10ByOrderByCreatedAtDesc();
    
    List<AlertLog> findByRegionIdOrderByCreatedAtDesc(Integer regionId, Pageable pageable);

    List<AlertLog> findByAlertIdOrderByCreatedAtAsc(Integer alertId);
}
