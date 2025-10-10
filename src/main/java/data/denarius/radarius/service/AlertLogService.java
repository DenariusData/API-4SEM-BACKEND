package data.denarius.radarius.service;

import data.denarius.radarius.dto.alertlog.AlertLogRequestDTO;
import data.denarius.radarius.dto.alertlog.AlertLogResponseDTO;

import java.util.List;

public interface AlertLogService {

    AlertLogResponseDTO create(AlertLogRequestDTO dto);

    AlertLogResponseDTO update(Integer id, AlertLogRequestDTO dto);

    void delete(Integer id);

    AlertLogResponseDTO findById(Integer id);

    List<AlertLogResponseDTO> findAll();
}
