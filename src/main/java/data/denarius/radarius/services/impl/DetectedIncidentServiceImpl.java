package data.denarius.radarius.services.impl;

import data.denarius.radarius.entity.DetectedIncident;
import data.denarius.radarius.repository.DetectedIncidentRepository;
import data.denarius.radarius.service.DetectedIncidentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetectedIncidentServiceImpl implements DetectedIncidentService {

    private final DetectedIncidentRepository detectedIncidentRepository;

    public DetectedIncidentServiceImpl(DetectedIncidentRepository detectedIncidentRepository) {
        this.detectedIncidentRepository = detectedIncidentRepository;
    }

    @Override
    public List<DetectedIncident> findAll() {
        return detectedIncidentRepository.findAll();
    }

    @Override
    public Optional<DetectedIncident> findById(Integer id) {
        return detectedIncidentRepository.findById(id);
    }

    @Override
    public DetectedIncident save(DetectedIncident detectedIncident) {
        return detectedIncidentRepository.save(detectedIncident);
    }

    @Override
    public DetectedIncident update(Integer id, DetectedIncident detectedIncident) {
        return detectedIncidentRepository.findById(id)
                .map(existing -> {
                    existing.setAlert(detectedIncident.getAlert());
                    existing.setUser(detectedIncident.getUser());
                    existing.setIncidentType(detectedIncident.getIncidentType());
                    existing.setCreatedAt(detectedIncident.getCreatedAt());
                    return detectedIncidentRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DetectedIncident not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!detectedIncidentRepository.existsById(id)) {
            throw new RuntimeException("DetectedIncident not found with id " + id);
        }
        detectedIncidentRepository.deleteById(id);
    }
}
