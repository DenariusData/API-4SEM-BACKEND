package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.camera.CameraRequestDTO;
import data.denarius.radarius.dto.camera.CameraResponseDTO;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.repository.CameraRepository;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.repository.RoadRepository;
import data.denarius.radarius.service.CameraService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CameraServiceImpl implements CameraService {

    @Autowired
    private CameraRepository cameraRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private RoadRepository roadRepository;

    @Override
    public CameraResponseDTO create(CameraRequestDTO dto) {
        Camera camera = mapToEntity(dto);
        return mapToDTO(cameraRepository.save(camera));
    }

    @Override
    public CameraResponseDTO update(Integer id, CameraRequestDTO dto) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Camera não encontrada"));
        updateEntity(camera, dto);
        return mapToDTO(cameraRepository.save(camera));
    }

    @Override
    public void delete(Integer id) {
        cameraRepository.deleteById(id);
    }

    @Override
    public CameraResponseDTO findById(Integer id) {
        return cameraRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Camera não encontrada"));
    }

    @Override
    public List<CameraResponseDTO> findAll() {
        return cameraRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Camera mapToEntity(CameraRequestDTO dto) {
        Camera camera = new Camera();
        updateEntity(camera, dto);
        return camera;
    }

    private void updateEntity(Camera camera, CameraRequestDTO dto) {
        camera.setLatitude(dto.getLatitude());
        camera.setLongitude(dto.getLongitude());
        camera.setActive(dto.getActive());
        camera.setCreatedAt(dto.getCreatedAt());
        camera.setUpdatedAt(dto.getUpdatedAt());

        if (dto.getRegionId() != null)
            camera.setRegion(regionRepository.findById(dto.getRegionId()).orElse(null));

        if (dto.getRoadId() != null)
            camera.setRoad(roadRepository.findById(dto.getRoadId()).orElse(null));
    }

    private CameraResponseDTO mapToDTO(Camera camera) {
        CameraResponseDTO dto = new CameraResponseDTO();
        dto.setId(camera.getId());
        dto.setLatitude(camera.getLatitude());
        dto.setLongitude(camera.getLongitude());
        dto.setActive(camera.getActive());
        dto.setCreatedAt(camera.getCreatedAt());
        dto.setUpdatedAt(camera.getUpdatedAt());
        dto.setRegionName(camera.getRegion() != null ? camera.getRegion().getName() : null);
        dto.setRoadName(camera.getRoad() != null ? camera.getRoad().getName() : null);
        return dto;
    }
}
