package data.denarius.radarius.controllers;

import data.denarius.radarius.dto.DetectedIncidentRequestDTO;
import data.denarius.radarius.dto.DetectedIncidentResponseDTO;
import data.radarius.radarius.service.DetectedIncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detected-incidents")
public class DetectedIncidentController {

    private final DetectedIncidentService service;

    public DetectedIncidentController(DetectedIncidentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DetectedIncidentResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetectedIncidentResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<DetectedIncidentResponseDTO> create(@RequestBody DetectedIncidentRequestDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetectedIncidentResponseDTO> update(@PathVariable Integer id,
                                                              @RequestBody DetectedIncidentRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
