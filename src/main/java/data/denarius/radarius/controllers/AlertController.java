package data.denarius.radarius.controllers;

import data.denarius.radarius.dtos.alert.AlertRequestDTO;
import data.denarius.radarius.dtos.alert.AlertResponseDTO;
import data.denarius.radarius.services.AlertService;
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
    public ResponseEntity<List<AlertResponseDTO>> findAll() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> findById(@PathVariable Integer id) {
        return alertService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AlertResponseDTO> create(@RequestBody AlertRequestDTO request) {
        return ResponseEntity.ok(alertService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> update(@PathVariable Integer id,
                                                   @RequestBody AlertRequestDTO request) {
        return ResponseEntity.ok(alertService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
