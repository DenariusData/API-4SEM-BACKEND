package data.denarius.radarius.controllers;

import data.denarius.radarius.dto.camera.CameraRequestDTO;
import data.denarius.radarius.dto.camera.CameraResponseDTO;
import data.denarius.radarius.services.CameraService;
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
    public ResponseEntity<List<CameraResponseDTO>> getAll() {
        return ResponseEntity.ok(cameraService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CameraResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(cameraService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CameraResponseDTO> create(@RequestBody CameraRequestDTO dto) {
        return ResponseEntity.ok(cameraService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CameraResponseDTO> update(@PathVariable Integer id, @RequestBody CameraRequestDTO dto) {
        return ResponseEntity.ok(cameraService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cameraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
