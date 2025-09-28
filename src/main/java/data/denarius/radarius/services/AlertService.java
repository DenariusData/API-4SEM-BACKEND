package data.denarius.radarius.services;

import data.denarius.radarius.entity.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertService {

    List<Alert> findAll();

    Optional<Alert> findById(Integer id);

    Alert save(Alert alert);

    Alert update(Integer id, Alert alert);

    void delete(Integer id);
}
