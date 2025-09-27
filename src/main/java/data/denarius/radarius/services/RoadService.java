package data.denarius.radarius.service;

import data.denarius.radarius.entity.Road;

import java.util.List;
import java.util.Optional;

public interface RoadService {

    List<Road> findAll();

    Optional<Road> findById(Integer id);

    Road save(Road road);

    Road update(Integer id, Road road);

    void delete(Integer id);
}
