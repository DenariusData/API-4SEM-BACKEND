package data.denarius.radarius.controllers;

import data.denarius.radarius.dtos.request.AlertRequestDTO;
import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getAll() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getById(@PathVariable Integer id) {
        return alertService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Alert> create(@RequestBody AlertRequestDTO request) {
        Alert created = alertService.save(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alert> update(@PathVariable Integer id, @RequestBody AlertRequestDTO request) {
        Alert updated = alertService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
