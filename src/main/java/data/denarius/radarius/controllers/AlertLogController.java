package data.denarius.radarius.controller;

import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.service.AlertLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alert-logs")
public class AlertLogController {

    private final AlertLogService alertLogService;

    public AlertLogController(AlertLogService alertLogService) {
        this.alertLogService = alertLogService;
    }

    @GetMapping
    public ResponseEntity<List<AlertLog>> getAll() {
        return ResponseEntity.ok(alertLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertLog> getById(@PathVariable Integer id) {
        return alertLogService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AlertLog> create(@RequestBody AlertLog alertLog) {
        return ResponseEntity.ok(alertLogService.save(alertLog));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertLog> update(@PathVariable Integer id, @RequestBody AlertLog alertLog) {
        return ResponseEntity.ok(alertLogService.update(id, alertLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
