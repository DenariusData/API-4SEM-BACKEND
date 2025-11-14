package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.dto.alertlog.AlertLogRecentResponseDTO;
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
    public List<AlertResponseDTO> getTop5ByRegion(Integer regionId) {
        return alertRepository.findTop5ByRegion_IdOrderByLevelDesc(regionId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponseDTO> getTop5ByRegionAndCriterion(Integer regionId, Integer criterionId) {
        return alertRepository.findTop5ByRegion_IdAndCriterion_IdOrderByLevelDesc(regionId, criterionId)
                .stream()
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
        dto.setCriterionName(alert.getCriterion() != null ? alert.getCriterion().getName() : null);
        dto.setRootCauseName(alert.getRootCause() != null ? alert.getRootCause().getName() : null);
        dto.setProtocolName(alert.getProtocol() != null ? alert.getProtocol().getName() : null);
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
