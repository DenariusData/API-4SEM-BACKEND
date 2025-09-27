package data.denarius.radarius.service;

import data.denarius.radarius.entity.AlertLog;

import java.util.List;
import java.util.Optional;

public interface AlertLogService {

    List<AlertLog> findAll();

    Optional<AlertLog> findById(Integer id);

    AlertLog save(AlertLog alertLog);

    AlertLog update(Integer id, AlertLog alertLog);

    void delete(Integer id);
}
