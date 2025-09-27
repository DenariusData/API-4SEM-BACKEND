package data.denarius.radarius.controller;

import data.denarius.radarius.entity.Road;
import data.denarius.radarius.service.RoadService;
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
    public ResponseEntity<List<Road>> getAll() {
        return ResponseEntity.ok(roadService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Road> getById(@PathVariable Integer id) {
        return roadService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Road> create(@RequestBody Road road) {
        return ResponseEntity.ok(roadService.save(road));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Road> update(@PathVariable Integer id, @RequestBody Road road) {
        return ResponseEntity.ok(roadService.update(id, road));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
