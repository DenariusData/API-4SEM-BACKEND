package data.denarius.radarius.services.impl;

import data.denarius.radarius.dtos.request.AlertRequestDTO;
import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.AlertService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final CriterionRepository criterionRepository;
    private final ProtocolRepository protocolRepository;
    private final UserRepository userRepository;
    private final CameraRepository cameraRepository;

    public AlertServiceImpl(AlertRepository alertRepository,
                            CriterionRepository criterionRepository,
                            ProtocolRepository protocolRepository,
                            UserRepository userRepository,
                            CameraRepository cameraRepository) {
        this.alertRepository = alertRepository;
        this.criterionRepository = criterionRepository;
        this.protocolRepository = protocolRepository;
        this.userRepository = userRepository;
        this.cameraRepository = cameraRepository;
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
    public Alert save(AlertRequestDTO request) {
        Alert alert = new Alert();
        mapRequestToEntity(request, alert);
        alert.setCreatedAt(OffsetDateTime.now());
        return alertRepository.save(alert);
    }

    @Override
    public Alert update(Integer id, AlertRequestDTO request) {
        return alertRepository.findById(id)
                .map(existing -> {
                    mapRequestToEntity(request, existing);
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

    private void mapRequestToEntity(AlertRequestDTO request, Alert alert) {
        if (request.getCriterionId() != null)
            criterionRepository.findById(request.getCriterionId()).ifPresent(alert::setCriterion);

        if (request.getProtocolId() != null)
            protocolRepository.findById(request.getProtocolId()).ifPresent(alert::setProtocol);

        if (request.getAssignedToId() != null)
            userRepository.findById(request.getAssignedToId()).ifPresent(alert::setAssignedTo);

        if (request.getCameraId() != null)
            cameraRepository.findById(request.getCameraId()).ifPresent(alert::setCamera);

        alert.setLevel(request.getLevel());
        alert.setStatus(request.getStatus());
        alert.setMessage(request.getMessage());
        alert.setConclusion(request.getConclusion());
        alert.setSourceType(request.getSourceType());
    }
}
