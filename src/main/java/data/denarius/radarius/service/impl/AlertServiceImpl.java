package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.alert.AlertLevelPerRegionDTO;
import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.dto.alertlog.AlertLogRecentResponseDTO;
import data.denarius.radarius.dto.alertlog.AlertLogResponseDTO;
import data.denarius.radarius.entity.*;
import data.denarius.radarius.repository.*;
import data.denarius.radarius.service.AlertService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private AlertLogRepository alertLogRepository;
    @Autowired
    private PersonRepository personRepository;
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
    public List<AlertLogRecentResponseDTO> getLast10AlertLogs(Integer regionId) {
        List<AlertLog> alertLogs;
        
        if (regionId == null) {
            alertLogs = alertLogRepository.findTop10ByOrderByCreatedAtDesc();
        } else {
            Pageable pageable = PageRequest.of(0, 10);
            alertLogs = alertLogRepository.findByRegionIdOrderByCreatedAtDesc(regionId, pageable);
        }
        
        return alertLogs.stream()
                .map(this::mapAlertLogToRecentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AlertResponseDTO> getAlertsWithFilters(
            List<Integer> regionIds,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Integer> regionIdsParam = (regionIds == null) ? null : regionIds;
        Page<Alert> alerts = alertRepository.findWithFilters(regionIdsParam, startDate, endDate, pageable);
        return alerts.map(this::mapToDTO);
    }

    @Override
    public List<AlertResponseDTO> getTop5WorstByRegion(List<Integer> regionIds) {
        Pageable pageable = PageRequest.of(0, 5,
                Sort.by("level").descending().and(Sort.by("createdAt").descending()));
        return alertRepository.findTop5WorstByRegionIds(regionIds,pageable)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDTO> getTop5WorstByRegionAndCriterion(List <Integer> regionIds, Integer criterionId) {
        Pageable pageable = PageRequest.of(0, 5,
                Sort.by("level").descending().and(Sort.by("createdAt").descending()));
        return alertRepository.findTop5WorstByRegionIdsAndCriterion(regionIds, criterionId,pageable)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertLevelPerRegionDTO> getAverageLevelPerRegion() {
        return alertRepository.findAverageLevelPerRegion().stream()
                .map(this::mapToAlertLevelPerRegionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDTO> getActiveAlertsByRegions(List<Integer> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            throw new IllegalArgumentException("Lista de IDs de regiões não pode ser nula ou vazia");
        }
        return alertRepository.findActiveAlertsByRegionIds(regionIds)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AlertResponseDTO> getAlertHistory(
            List<Integer> regionIds,
            List<Integer> criterionIds,
            List<Short> levels,
            Boolean isOpen,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        List<Integer> regionIdsParam = (regionIds != null && !regionIds.isEmpty()) ? regionIds : null;
        List<Integer> criterionIdsParam = (criterionIds != null && !criterionIds.isEmpty()) ? criterionIds : null;
        List<Short> levelsParam = (levels != null && !levels.isEmpty()) ? levels : null;

        Page<Alert> alerts = alertRepository.findHistoryWithFilters(
                regionIdsParam,
                criterionIdsParam,
                levelsParam,
                isOpen,
                startDate,
                endDate,
                pageable
        );

        return alerts.map(this::mapToDTO);
    }

    @Override
    public List<AlertLogResponseDTO> getAlertLogs(Integer alertId) {
        alertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alerta não encontrado com ID: " + alertId));

        List<AlertLog> logs = alertLogRepository.findByAlertIdOrderByCreatedAtAsc(alertId);

        return logs.stream()
                .map(this::mapAlertLogToDTO)
                .collect(Collectors.toList());
    }

    private AlertLogResponseDTO mapAlertLogToDTO(AlertLog log) {
        AlertLogResponseDTO dto = new AlertLogResponseDTO();
        dto.setId(log.getId());
        dto.setCreatedAt(log.getCreatedAt());
        dto.setPreviousLevel(log.getPreviousLevel());
        dto.setNewLevel(log.getNewLevel());
        dto.setClosedAt(log.getClosedAt());
        dto.setAlertMessage(log.getAlert() != null ? log.getAlert().getMessage() : null);
        dto.setRegionName(log.getRegion() != null ? log.getRegion().getName() : null);
        dto.setAlertId(log.getAlert() != null ? log.getAlert().getId() : null);
        dto.setCriterionName(log.getCriterion() != null ? log.getCriterion().getName() : null);
        return dto;
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
        dto.setLevel(alert.getLevel().shortValue());
        dto.setMessage(alert.getMessage());
        dto.setConclusion(alert.getConclusion());
        dto.setSourceType(alert.getSourceType());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setClosedAt(alert.getClosedAt());

        dto.setCreatedByName(alert.getCreatedBy() != null ? alert.getCreatedBy().getName() : null);
        dto.setAssignedToName(alert.getAssignedTo() != null ? alert.getAssignedTo().getName() : null);

        dto.setRegionId(alert.getRegion() != null ? alert.getRegion().getId() : null);
        dto.setRegionName(alert.getRegion() != null ? alert.getRegion().getName() : null);

        dto.setCriterionId(alert.getCriterion() != null ? alert.getCriterion().getId() : null);
        dto.setCriterionName(alert.getCriterion() != null ? alert.getCriterion().getName() : null);
        dto.setRegionName(alert.getRegion() != null ? alert.getRegion().getName() : null);
        dto.setRegionId(alert.getRegion() != null ? alert.getRegion().getId() : null);
        dto.setRootCauseName(alert.getRootCause() != null ? alert.getRootCause().getName() : null);
        dto.setProtocolName(alert.getProtocol() != null ? alert.getProtocol().getName() : null);

        boolean isOpen = alert.getClosedAt() == null;
        dto.setIsOpen(isOpen);
        dto.setStatus(isOpen ? "ABERTO" : "FECHADO");

        return dto;
    }
    
    private AlertLogRecentResponseDTO mapAlertLogToRecentDTO(AlertLog alertLog) {
        return AlertLogRecentResponseDTO.builder()
                .id(alertLog.getId())
                .alertId(alertLog.getAlert() != null ? alertLog.getAlert().getId() : null)
                .indicator(alertLog.getCriterion() != null ? alertLog.getCriterion().getName() : null)
                .previousLevel(alertLog.getPreviousLevel())
                .newLevel(alertLog.getNewLevel())
                .location(alertLog.getRegion() != null ? alertLog.getRegion().getName() : null)
                .timestamp(calculateTimeAgo(alertLog.getCreatedAt()))
                .finalized(alertLog.getClosedAt() != null)
                .build();
    }

    private AlertLevelPerRegionDTO mapToAlertLevelPerRegionDTO(java.util.Map<String, Object> map) {
        return AlertLevelPerRegionDTO.builder()
                .regionId(((Number) map.get("regionId")).intValue())
                .level(((Number) map.get("level")).intValue())
                .build();
    }
    
    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "N/A";
        }
        
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        
        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        
        if (days > 0) {
            return days == 1 ? "1 dia" : days + " dias";
        } else if (hours > 0) {
            return hours == 1 ? "1 hora" : hours + " horas";
        } else if (minutes > 0) {
            return minutes == 1 ? "1 minuto" : minutes + " minutos";
        } else {
            return seconds <= 1 ? "agora" : seconds + " segundos";
        }
    }
}
