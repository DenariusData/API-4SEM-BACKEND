package data.denarius.radarius.controller;

import data.denarius.radarius.dto.camera.CameraRequestDTO;
import data.denarius.radarius.dto.camera.CameraResponseDTO;
import data.denarius.radarius.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cameras")
public class CameraController {

    @Autowired
    private CameraService cameraService;

    @PostMapping
    public ResponseEntity<CameraResponseDTO> create(@RequestBody CameraRequestDTO dto) {
        return ResponseEntity.ok(cameraService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CameraResponseDTO> update(@PathVariable Integer id, @RequestBody CameraRequestDTO dto) {
        return ResponseEntity.ok(cameraService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CameraResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cameraService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CameraResponseDTO>> findAll() {
        return ResponseEntity.ok(cameraService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cameraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
