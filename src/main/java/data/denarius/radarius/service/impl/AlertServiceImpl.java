package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.AlertService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CameraRepository cameraRepository;
    @Autowired
    private CriterionRepository criterionRepository;
    @Autowired
    private RootCauseRepository rootCauseRepository;

    @Override
    public AlertResponseDTO create(AlertRequestDTO dto) {
        Alert alert = mapToEntity(dto);
        return mapToDTO(alertRepository.save(alert));
    }

    @Override
    public AlertResponseDTO update(Integer id, AlertRequestDTO dto) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerta não encontrado"));
        updateEntity(alert, dto);
        return mapToDTO(alertRepository.save(alert));
    }

    @Override
    public void delete(Integer id) {
        alertRepository.deleteById(id);
    }

    @Override
    public AlertResponseDTO findById(Integer id) {
        return alertRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Alerta não encontrado"));
    }

    @Override
    public List<AlertResponseDTO> findAll() {
        return alertRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Alert mapToEntity(AlertRequestDTO dto) {
        Alert alert = new Alert();
        updateEntity(alert, dto);
        return alert;
    }

    private void updateEntity(Alert alert, AlertRequestDTO dto) {
        alert.setLevel(dto.getLevel());
        alert.setMessage(dto.getMessage());
        alert.setConclusion(dto.getConclusion());
        alert.setSourceType(dto.getSourceType());
        alert.setCreatedAt(dto.getCreatedAt());
        alert.setClosedAt(dto.getClosedAt());

        if (dto.getCreatedById() != null)
            alert.setCreatedBy(personRepository.findById(dto.getCreatedById()).orElse(null));

        if (dto.getAssignedToId() != null)
            alert.setAssignedTo(personRepository.findById(dto.getAssignedToId()).orElse(null));

        if (dto.getCameraId() != null)
            alert.setCamera(cameraRepository.findById(dto.getCameraId()).orElse(null));

        if (dto.getCriterionId() != null)
            alert.setCriterion(criterionRepository.findById(dto.getCriterionId()).orElse(null));

        if (dto.getRootCauseId() != null)
            alert.setRootCause(rootCauseRepository.findById(dto.getRootCauseId()).orElse(null));
    }

    private AlertResponseDTO mapToDTO(Alert alert) {
        AlertResponseDTO dto = new AlertResponseDTO();
        dto.setId(alert.getId());
        dto.setLevel(alert.getLevel());
        dto.setMessage(alert.getMessage());
        dto.setConclusion(alert.getConclusion());
        dto.setSourceType(alert.getSourceType());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setClosedAt(alert.getClosedAt());
        dto.setCreatedByName(alert.getCreatedBy() != null ? alert.getCreatedBy().getName() : null);
        dto.setAssignedToName(alert.getAssignedTo() != null ? alert.getAssignedTo().getName() : null);
        dto.setCriterionName(alert.getCriterion() != null ? alert.getCriterion().getName() : null);
        dto.setRootCauseName(alert.getRootCause() != null ? alert.getRootCause().getName() : null);
        return dto;
    }
}
