package data.denarius.radarius.service;

import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;

import java.util.List;

public interface AlertService {
    AlertResponseDTO create(AlertRequestDTO dto);

    AlertResponseDTO update(Integer id, AlertRequestDTO dto);

    void delete(Integer id);

    AlertResponseDTO findById(Integer id);

    List<AlertResponseDTO> findAll();
}
