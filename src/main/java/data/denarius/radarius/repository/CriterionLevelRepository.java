package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.CriterionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CriterionLevelRepository extends JpaRepository<CriterionLevel, Integer> {
    Optional<CriterionLevel> findByCriterionAndLevel(Criterion criterion, Integer level);
}
