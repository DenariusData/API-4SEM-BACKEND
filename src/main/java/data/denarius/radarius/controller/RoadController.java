package data.denarius.radarius.controller;

import data.denarius.radarius.dto.road.RoadRequestDTO;
import data.denarius.radarius.dto.road.RoadResponseDTO;
import data.denarius.radarius.service.RoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roads")
public class RoadController {

    @Autowired
    private RoadService roadService;

    @PostMapping
    public ResponseEntity<RoadResponseDTO> create(@RequestBody RoadRequestDTO dto) {
        return ResponseEntity.ok(roadService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoadResponseDTO> update(@PathVariable Integer id, @RequestBody RoadRequestDTO dto) {
        return ResponseEntity.ok(roadService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoadResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(roadService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoadResponseDTO>> findAll() {
        return ResponseEntity.ok(roadService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
