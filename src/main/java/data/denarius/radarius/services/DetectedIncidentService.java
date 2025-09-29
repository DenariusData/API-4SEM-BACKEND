package data.denarius.radarius.services;

import data.denarius.radarius.dtos.detectedincident.DetectedIncidentRequestDTO;
import data.denarius.radarius.dtos.detectedincident.DetectedIncidentResponseDTO;

import java.util.List;

public interface DetectedIncidentService {

    List<DetectedIncidentResponseDTO> findAll();

    DetectedIncidentResponseDTO findById(Integer id);

    DetectedIncidentResponseDTO save(DetectedIncidentRequestDTO dto);

    DetectedIncidentResponseDTO update(Integer id, DetectedIncidentRequestDTO dto);

    void delete(Integer id);
}
