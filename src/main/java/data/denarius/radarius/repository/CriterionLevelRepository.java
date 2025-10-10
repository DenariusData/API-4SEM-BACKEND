package data.denarius.radarius.repository;

import data.denarius.radarius.entity.CriterionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CriterionLevelRepository extends JpaRepository<CriterionLevel, Integer> {
}
