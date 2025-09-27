package data.denarius.radarius.service;

import data.denarius.radarius.entity.Camera;

import java.util.List;
import java.util.Optional;

public interface CameraService {

    List<Camera> findAll();

    Optional<Camera> findById(Integer id);

    Camera save(Camera camera);

    Camera update(Integer id, Camera camera);

    void delete(Integer id);
}
