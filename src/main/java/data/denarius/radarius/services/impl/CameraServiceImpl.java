package data.denarius.radarius.services.impl;

import data.denarius.radarius.dtos.camera.CameraRequestDTO;
import data.denarius.radarius.dtos.camera.CameraResponseDTO;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Road;
import data.denarius.radarius.repositories.CameraRepository;
import data.denarius.radarius.repositories.RoadRepository;
import data.denarius.radarius.services.CameraService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;
    private final RoadRepository roadRepository;

    public CameraServiceImpl(CameraRepository cameraRepository, RoadRepository roadRepository) {
        this.cameraRepository = cameraRepository;
        this.roadRepository = roadRepository;
    }

    @Override
    public List<CameraResponseDTO> findAll() {
        return cameraRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CameraResponseDTO findById(Integer id) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camera not found with id: " + id));
        return toResponseDTO(camera);
    }

    @Override
    public CameraResponseDTO save(CameraRequestDTO dto) {
        Camera camera = new Camera();
        Road road = roadRepository.findById(dto.getRoadId())
                .orElseThrow(() -> new RuntimeException("Road not found with id: " + dto.getRoadId()));

        camera.setRoad(road);
        camera.setLatitude(dto.getLatitude());
        camera.setLongitude(dto.getLongitude());
        camera.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
        camera.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : OffsetDateTime.now());
        camera.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : OffsetDateTime.now());

        Camera saved = cameraRepository.save(camera);
        return toResponseDTO(saved);
    }

    @Override
    public CameraResponseDTO update(Integer id, CameraRequestDTO dto) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camera not found with id: " + id));

        if (dto.getRoadId() != null) {
            Road road = roadRepository.findById(dto.getRoadId())
                    .orElseThrow(() -> new RuntimeException("Road not found with id: " + dto.getRoadId()));
            camera.setRoad(road);
        }

        if (dto.getLatitude() != null) camera.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) camera.setLongitude(dto.getLongitude());
        if (dto.getActive() != null) camera.setActive(dto.getActive());
        camera.setUpdatedAt(OffsetDateTime.now());

        Camera updated = cameraRepository.save(camera);
        return toResponseDTO(updated);
    }

    @Override
    public void delete(Integer id) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camera not found with id: " + id));
        cameraRepository.delete(camera);
    }

    private CameraResponseDTO toResponseDTO(Camera camera) {
        return new CameraResponseDTO(
                camera.getCameraId(),
                camera.getRoad() != null ? camera.getRoad().getRoadId() : null,
                camera.getLatitude(),
                camera.getLongitude(),
                camera.getActive(),
                camera.getCreatedAt(),
                camera.getUpdatedAt()
        );
    }
}
