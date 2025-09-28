package data.denarius.radarius.services.impl;

import data.denarius.radarius.dto.camera.CameraRequestDTO;
import data.denarius.radarius.dto.camera.CameraResponseDTO;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Road;
import data.denarius.radarius.repository.CameraRepository;
import data.denarius.radarius.repository.RoadRepository;
import data.denarius.radarius.services.CameraService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;
    private final RoadRepository roadRepository;

    public CameraServiceImpl(CameraRepository cameraRepository, RoadRepository roadRepository) {
        this.cameraRepository = cameraRepository;
        this.roadRepository = roadRepository;
    }

    private CameraResponseDTO toDTO(Camera camera) {
        CameraResponseDTO dto = new CameraResponseDTO();
        dto.setCameraId(camera.getCameraId());
        dto.setRoadId(camera.getRoad() != null ? camera.getRoad().getRoadId() : null);
        dto.setLatitude(camera.getLatitude());
        dto.setLongitude(camera.getLongitude());
        dto.setActive(camera.getActive());
        dto.setCreatedAt(camera.getCreatedAt());
        dto.setUpdatedAt(camera.getUpdatedAt());
        return dto;
    }

    private Camera toEntity(CameraRequestDTO dto) {
        Camera camera = new Camera();
        if (dto.getRoadId() != null) {
            Road road = roadRepository.findById(dto.getRoadId())
                    .orElseThrow(() -> new RuntimeException("Road not found with id " + dto.getRoadId()));
            camera.setRoad(road);
        }
        camera.setLatitude(dto.getLatitude());
        camera.setLongitude(dto.getLongitude());
        camera.setActive(dto.getActive());
        camera.setCreatedAt(dto.getCreatedAt());
        camera.setUpdatedAt(dto.getUpdatedAt());
        return camera;
    }

    @Override
    public List<CameraResponseDTO> findAll() {
        return cameraRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CameraResponseDTO findById(Integer id) {
        return cameraRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Camera not found with id " + id));
    }

    @Override
    public CameraResponseDTO save(CameraRequestDTO dto) {
        Camera camera = toEntity(dto);
        return toDTO(cameraRepository.save(camera));
    }

    @Override
    public CameraResponseDTO update(Integer id, CameraRequestDTO dto) {
        return cameraRepository.findById(id)
                .map(existing -> {
                    if (dto.getRoadId() != null) {
                        Road road = roadRepository.findById(dto.getRoadId())
                                .orElseThrow(() -> new RuntimeException("Road not found with id " + dto.getRoadId()));
                        existing.setRoad(road);
                    }
                    existing.setLatitude(dto.getLatitude());
                    existing.setLongitude(dto.getLongitude());
                    existing.setActive(dto.getActive());
                    existing.setCreatedAt(dto.getCreatedAt());
                    existing.setUpdatedAt(dto.getUpdatedAt());
                    return toDTO(cameraRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Camera not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!cameraRepository.existsById(id)) {
            throw new RuntimeException("Camera not found with id " + id);
        }
        cameraRepository.deleteById(id);
    }
}
