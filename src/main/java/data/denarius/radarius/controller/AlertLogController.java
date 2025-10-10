package data.denarius.radarius.controller;

import data.denarius.radarius.dto.alertlog.AlertLogRequestDTO;
import data.denarius.radarius.dto.alertlog.AlertLogResponseDTO;
import data.denarius.radarius.service.AlertLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alert-logs")
public class AlertLogController {

    @Autowired
    private AlertLogService alertLogService;

    @PostMapping
    public ResponseEntity<AlertLogResponseDTO> create(@RequestBody AlertLogRequestDTO dto) {
        return ResponseEntity.ok(alertLogService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertLogResponseDTO> update(@PathVariable Integer id, @RequestBody AlertLogRequestDTO dto) {
        return ResponseEntity.ok(alertLogService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertLogResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(alertLogService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<AlertLogResponseDTO>> findAll() {
        return ResponseEntity.ok(alertLogService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
