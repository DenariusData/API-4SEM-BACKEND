package data.denarius.radarius.services.impl;

import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.repository.AlertRepository;
import data.denarius.radarius.service.AlertService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public List<Alert> findAll() {
        return alertRepository.findAll();
    }

    @Override
    public Optional<Alert> findById(Integer id) {
        return alertRepository.findById(id);
    }

    @Override
    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    @Override
    public Alert update(Integer id, Alert alert) {
        return alertRepository.findById(id)
                .map(existing -> {
                    existing.setCriterion(alert.getCriterion());
                    existing.setProtocol(alert.getProtocol());
                    existing.setLevel(alert.getLevel());
                    existing.setStatus(alert.getStatus());
                    existing.setAssignedTo(alert.getAssignedTo());
                    existing.setMessage(alert.getMessage());
                    existing.setConclusion(alert.getConclusion());
                    existing.setCamera(alert.getCamera());
                    existing.setCreatedAt(alert.getCreatedAt());
                    existing.setSourceType(alert.getSourceType());
                    existing.setIncidents(alert.getIncidents());
                    existing.setLogs(alert.getLogs());
                    return alertRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Alert not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!alertRepository.existsById(id)) {
            throw new RuntimeException("Alert not found with id " + id);
        }
        alertRepository.deleteById(id);
    }
}
