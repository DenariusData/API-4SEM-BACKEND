package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.alertlog.AlertLogRequestDTO;
import data.denarius.radarius.dto.alertlog.AlertLogResponseDTO;
import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.repository.AlertLogRepository;
import data.denarius.radarius.repository.AlertRepository;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.AlertLogService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertLogServiceImpl implements AlertLogService {

    @Autowired
    private AlertLogRepository alertLogRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private RegionRepository regionRepository;

    @Override
    public AlertLogResponseDTO create(AlertLogRequestDTO dto) {
        AlertLog alertLog = mapToEntity(dto);
        return mapToDTO(alertLogRepository.save(alertLog));
    }

    @Override
    public AlertLogResponseDTO update(Integer id, AlertLogRequestDTO dto) {
        AlertLog alertLog = alertLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AlertLog não encontrado"));
        updateEntity(alertLog, dto);
        return mapToDTO(alertLogRepository.save(alertLog));
    }

    @Override
    public void delete(Integer id) {
        alertLogRepository.deleteById(id);
    }

    @Override
    public AlertLogResponseDTO findById(Integer id) {
        return alertLogRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("AlertLog não encontrado"));
    }

    @Override
    public List<AlertLogResponseDTO> findAll() {
        return alertLogRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AlertLog mapToEntity(AlertLogRequestDTO dto) {
        AlertLog alertLog = new AlertLog();
        updateEntity(alertLog, dto);
        return alertLog;
    }

    private void updateEntity(AlertLog alertLog, AlertLogRequestDTO dto) {
        alertLog.setCreatedAt(dto.getCreatedAt());
        alertLog.setPreviousLevel(dto.getPreviousLevel());
        alertLog.setNewLevel(dto.getNewLevel());
        alertLog.setClosedAt(dto.getClosedAt());

        if (dto.getAlertId() != null)
            alertLog.setAlert(alertRepository.findById(dto.getAlertId()).orElse(null));

        if (dto.getRegionId() != null)
            alertLog.setRegion(regionRepository.findById(dto.getRegionId()).orElse(null));
    }

    private AlertLogResponseDTO mapToDTO(AlertLog alertLog) {
        AlertLogResponseDTO dto = new AlertLogResponseDTO();
        dto.setId(alertLog.getId());
        dto.setCreatedAt(alertLog.getCreatedAt());
        dto.setPreviousLevel(alertLog.getPreviousLevel());
        dto.setNewLevel(alertLog.getNewLevel());
        dto.setClosedAt(alertLog.getClosedAt());
        dto.setAlertMessage(alertLog.getAlert() != null ? alertLog.getAlert().getMessage() : null);
        dto.setRegionName(alertLog.getRegion() != null ? alertLog.getRegion().getName() : null);
        return dto;
    }
}
