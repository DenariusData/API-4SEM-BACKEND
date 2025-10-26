package data.denarius.radarius.service;

import data.denarius.radarius.dto.camera.CameraRequestDTO;
import data.denarius.radarius.dto.camera.CameraResponseDTO;

import java.util.List;

public interface CameraService {
    CameraResponseDTO create(CameraRequestDTO dto);
    CameraResponseDTO update(Integer id, CameraRequestDTO dto);
    void delete(Integer id);
    CameraResponseDTO findById(Integer id);
    List<CameraResponseDTO> findAll();
}
