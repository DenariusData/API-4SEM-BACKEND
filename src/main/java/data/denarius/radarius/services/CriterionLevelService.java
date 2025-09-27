package data.denarius.radarius.service;

import data.denarius.radarius.entity.CriterionLevel;

import java.util.List;
import java.util.Optional;

public interface CriterionLevelService {

    List<CriterionLevel> findAll();

    Optional<CriterionLevel> findById(Integer id);

    CriterionLevel save(CriterionLevel criterionLevel);

    CriterionLevel update(Integer id, CriterionLevel criterionLevel);

    void delete(Integer id);
}
