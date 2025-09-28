package data.denarius.radarius.controllers;

import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.service.CameraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cameras")
public class CameraController {

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping
    public ResponseEntity<List<Camera>> getAll() {
        return ResponseEntity.ok(cameraService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Camera> getById(@PathVariable Integer id) {
        return cameraService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Camera> create(@RequestBody Camera camera) {
        return ResponseEntity.ok(cameraService.save(camera));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Camera> update(@PathVariable Integer id, @RequestBody Camera camera) {
        return ResponseEntity.ok(cameraService.update(id, camera));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cameraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
