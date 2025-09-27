package data.denarius.radarius.service;

import data.denarius.radarius.entity.DetectedIncident;

import java.util.List;
import java.util.Optional;

public interface DetectedIncidentService {

    List<DetectedIncident> findAll();

    Optional<DetectedIncident> findById(Integer id);

    DetectedIncident save(DetectedIncident detectedIncident);

    DetectedIncident update(Integer id, DetectedIncident detectedIncident);

    void delete(Integer id);
}
