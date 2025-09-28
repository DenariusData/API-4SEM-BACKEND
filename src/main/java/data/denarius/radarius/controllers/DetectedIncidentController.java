package data.denarius.radarius.controllers;

import data.denarius.radarius.entity.DetectedIncident;
import data.denarius.radarius.service.DetectedIncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detected-incidents")
public class DetectedIncidentController {

    private final DetectedIncidentService detectedIncidentService;

    public DetectedIncidentController(DetectedIncidentService detectedIncidentService) {
        this.detectedIncidentService = detectedIncidentService;
    }

    @GetMapping
    public ResponseEntity<List<DetectedIncident>> getAll() {
        return ResponseEntity.ok(detectedIncidentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetectedIncident> getById(@PathVariable Integer id) {
        return detectedIncidentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetectedIncident> create(@RequestBody DetectedIncident detectedIncident) {
        return ResponseEntity.ok(detectedIncidentService.save(detectedIncident));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetectedIncident> update(@PathVariable Integer id, @RequestBody DetectedIncident detectedIncident) {
        return ResponseEntity.ok(detectedIncidentService.update(id, detectedIncident));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        detectedIncidentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
