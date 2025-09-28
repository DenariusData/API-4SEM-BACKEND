package data.denarius.radarius.services.impl;

import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.repository.CameraRepository;
import data.denarius.radarius.service.CameraService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;

    public CameraServiceImpl(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    @Override
    public List<Camera> findAll() {
        return cameraRepository.findAll();
    }

    @Override
    public Optional<Camera> findById(Integer id) {
        return cameraRepository.findById(id);
    }

    @Override
    public Camera save(Camera camera) {
        return cameraRepository.save(camera);
    }

    @Override
    public Camera update(Integer id, Camera camera) {
        return cameraRepository.findById(id)
                .map(existing -> {
                    existing.setRoad(camera.getRoad());
                    existing.setLatitude(camera.getLatitude());
                    existing.setLongitude(camera.getLongitude());
                    existing.setActive(camera.getActive());
                    existing.setCreatedAt(camera.getCreatedAt());
                    existing.setUpdatedAt(camera.getUpdatedAt());
                    existing.setReadings(camera.getReadings());
                    existing.setAlerts(camera.getAlerts());
                    return cameraRepository.save(existing);
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
