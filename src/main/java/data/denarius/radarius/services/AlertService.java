package data.denarius.radarius.services;

import data.denarius.radarius.dtos.request.AlertRequestDTO;
import data.denarius.radarius.dto.AlertResponseDTO;

import java.util.List;
import java.util.Optional;

public interface AlertService {

    List<AlertResponseDTO> findAll();

    Optional<AlertResponseDTO> findById(Integer id);

    AlertResponseDTO save(AlertRequestDTO alertRequest);

    AlertResponseDTO update(Integer id, AlertRequestDTO alertRequest);

    void delete(Integer id);
}
