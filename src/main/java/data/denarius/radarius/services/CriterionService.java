package data.denarius.radarius.service;

import data.denarius.radarius.entity.Criterion;

import java.util.List;
import java.util.Optional;

public interface CriterionService {

    List<Criterion> findAll();

    Optional<Criterion> findById(Integer id);

    Criterion save(Criterion criterion);

    Criterion update(Integer id, Criterion criterion);

    void delete(Integer id);
}
