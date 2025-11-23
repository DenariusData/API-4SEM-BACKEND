package data.denarius.radarius.service;

import data.denarius.radarius.dto.alert.AlertLevelPerRegionDTO;
import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.dto.alertlog.AlertLogRecentResponseDTO;
import data.denarius.radarius.dto.alertlog.AlertLogResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertService {
    AlertResponseDTO create(AlertRequestDTO dto);

    AlertResponseDTO update(Integer id, AlertRequestDTO dto);

    void delete(Integer id);

    AlertResponseDTO findById(Integer id);

    List<AlertResponseDTO> findAll();

    List<AlertLogRecentResponseDTO> getLast10AlertLogs(Integer regionId);

    Page<AlertResponseDTO> getAlertsWithFilters(
            List<Integer> regionIds,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    );

    List<AlertResponseDTO> getTop5WorstByRegion(List<Integer> regionIds);

    List<AlertResponseDTO> getTop5WorstByRegionAndCriterion(List<Integer> regionIds, Integer criterionId);

    List<AlertLevelPerRegionDTO> getAverageLevelPerRegion();

    List<AlertResponseDTO> getActiveAlertsByRegions(List<Integer> regionIds);

    Page<AlertResponseDTO> getAlertHistory(
            List<Integer> regionIds,
            List<Integer> criterionIds,
            List<Short> levels,
            Boolean isOpen,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    );

    List<AlertLogResponseDTO> getAlertLogs(Integer alertId);

}
