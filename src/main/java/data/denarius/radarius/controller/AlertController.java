package data.denarius.radarius.controller;

import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping
    public ResponseEntity<AlertResponseDTO> create(@RequestBody AlertRequestDTO dto) {
        return ResponseEntity.ok(alertService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> update(@PathVariable Integer id, @RequestBody AlertRequestDTO dto) {
        return ResponseEntity.ok(alertService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(alertService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> findAll() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
