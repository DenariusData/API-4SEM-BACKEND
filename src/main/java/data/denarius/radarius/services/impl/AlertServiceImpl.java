package data.denarius.radarius.services.impl;

import data.denarius.radarius.dtos.alert.AlertRequestDTO;
import data.denarius.radarius.dtos.alert.AlertResponseDTO;
import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.repositories.*;
import data.denarius.radarius.services.AlertService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final CriterionRepository criterionRepository;
    private final ProtocolRepository protocolRepository;
    private final PersonRepository personRepository;
    private final CameraRepository cameraRepository;

    public AlertServiceImpl(AlertRepository alertRepository,
                            CriterionRepository criterionRepository,
                            ProtocolRepository protocolRepository,
                            PersonRepository personRepository,
                            CameraRepository cameraRepository) {
        this.alertRepository = alertRepository;
        this.criterionRepository = criterionRepository;
        this.protocolRepository = protocolRepository;
        this.personRepository = personRepository;
        this.cameraRepository = cameraRepository;
    }

    @Override
    public List<AlertResponseDTO> findAll() {
        return alertRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<AlertResponseDTO> findById(Integer id) {
        return alertRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    public AlertResponseDTO save(AlertRequestDTO request) {
        Alert alert = new Alert();
        mapRequestToEntity(request, alert);
        alert.setCreatedAt(OffsetDateTime.now());
        return toDTO(alertRepository.save(alert));
    }

    @Override
    public AlertResponseDTO update(Integer id, AlertRequestDTO request) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found with id " + id));
        mapRequestToEntity(request, alert);
        return toDTO(alertRepository.save(alert));
    }

    @Override
    public void delete(Integer id) {
        if (!alertRepository.existsById(id)) {
            throw new RuntimeException("Alert not found with id " + id);
        }
        alertRepository.deleteById(id);
    }

    private void mapRequestToEntity(AlertRequestDTO request, Alert alert) {
        if (request.getCriterionId() != null) {
            criterionRepository.findById(request.getCriterionId())
                    .ifPresent(alert::setCriterion);
        }

        if (request.getProtocolId() != null) {
            protocolRepository.findById(request.getProtocolId())
                    .ifPresent(alert::setProtocol);
        }

        if (request.getAssignedToId() != null) {
            personRepository.findById(request.getAssignedToId())
                    .ifPresent(alert::setAssignedTo);
        }

        if (request.getCameraId() != null) {
            cameraRepository.findById(request.getCameraId())
                    .ifPresent(alert::setCamera);
        }

        alert.setLevel(request.getLevel());
        alert.setStatus(request.getStatus());
        alert.setMessage(request.getMessage());
        alert.setConclusion(request.getConclusion());
        alert.setSourceType(request.getSourceType());
    }

    // Método toDTO para converter Alert em AlertResponseDTO
    private AlertResponseDTO toDTO(Alert alert) {
        AlertResponseDTO dto = new AlertResponseDTO();
        dto.setAlertId(alert.getAlertId());
        dto.setCriterionId(alert.getCriterion() != null ? alert.getCriterion().getCriterionId() : null);
        dto.setProtocolId(alert.getProtocol() != null ? alert.getProtocol().getProtocolId() : null);
        dto.setAssignedToId(alert.getAssignedTo() != null ? alert.getAssignedTo().getPersonId() : null);
        dto.setCameraId(alert.getCamera() != null ? alert.getCamera().getCameraId() : null);
        dto.setLevel(alert.getLevel());
        dto.setStatus(alert.getStatus());
        dto.setMessage(alert.getMessage());
        dto.setConclusion(alert.getConclusion());
        dto.setSourceType(alert.getSourceType());
        dto.setCreatedAt(alert.getCreatedAt());
        return dto;
    }
}
