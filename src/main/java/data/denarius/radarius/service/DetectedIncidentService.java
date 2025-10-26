package data.denarius.radarius.service;

import data.denarius.radarius.dto.detectedincident.DetectedIncidentRequestDTO;
import data.denarius.radarius.dto.detectedincident.DetectedIncidentResponseDTO;

import java.util.List;

public interface DetectedIncidentService {
    DetectedIncidentResponseDTO create(DetectedIncidentRequestDTO dto);
    DetectedIncidentResponseDTO update(Integer id, DetectedIncidentRequestDTO dto);
    void delete(Integer id);
    DetectedIncidentResponseDTO findById(Integer id);
    List<DetectedIncidentResponseDTO> findAll();
}
