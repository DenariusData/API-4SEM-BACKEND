package data.denarius.radarius.service;

import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.dto.alertlog.AlertLogRecentResponseDTO;
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

    List<AlertResponseDTO> getTop5WorstByRegion(Integer regionId);

    List<AlertResponseDTO> getTop5WorstByRegionAndCriterion(Integer regionId, Integer criterionId);

}
