package data.denarius.radarius.services;

import data.denarius.radarius.dto.camera.CameraRequestDTO;
import data.denarius.radarius.dto.camera.CameraResponseDTO;

import java.util.List;

public interface CameraService {

    List<CameraResponseDTO> findAll();

    CameraResponseDTO findById(Integer id);

    CameraResponseDTO save(CameraRequestDTO dto);

    CameraResponseDTO update(Integer id, CameraRequestDTO dto);

    void delete(Integer id);
}
