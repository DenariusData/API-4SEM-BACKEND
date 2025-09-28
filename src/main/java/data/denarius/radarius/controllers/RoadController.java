package data.denarius.radarius.controllers;

import data.denarius.radarius.dtos.road.RoadRequestDTO;
import data.denarius.radarius.dtos.road.RoadResponseDTO;
import data.radarius.radarius.services.RoadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roads")
public class RoadController {

    private final RoadService roadService;

    public RoadController(RoadService roadService) {
        this.roadService = roadService;
    }

    @GetMapping
    public ResponseEntity<List<RoadResponseDTO>> getAll() {
        return ResponseEntity.ok(roadService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoadResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(roadService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RoadResponseDTO> create(@RequestBody RoadRequestDTO dto) {
        return ResponseEntity.ok(roadService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoadResponseDTO> update(@PathVariable Integer id, @RequestBody RoadRequestDTO dto) {
        return ResponseEntity.ok(roadService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
