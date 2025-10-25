package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.AlertService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    @Autowired
    private ProtocolRepository protocolRepository;

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

    @Override
    public List<AlertResponseDTO> getLast10AlertsByRegion(Integer regionId) {
        Pageable pageable = PageRequest.of(0, 10);
        return alertRepository.findTop10ByRegion(regionId, pageable)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AlertResponseDTO> getAlertsWithFilters(
            List<Integer> regionIds,
            Integer cameraId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Integer> regionIdsParam = (regionIds == null) ? null : regionIds;
        Page<Alert> alerts = alertRepository.findWithFilters(regionIdsParam, cameraId, startDate, endDate, pageable);
        return alerts.map(this::mapToDTO);
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

        if (dto.getProtocolId() != null)
            alert.setProtocol(protocolRepository.findById(dto.getProtocolId()).orElse(null));
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
        dto.setProtocolName(alert.getProtocol() != null ? alert.getProtocol().getName() : null);
        return dto;
    }
}
