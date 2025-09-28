package data.denarius.radarius.controllers;

import data.denarius.radarius.dto.ProtocolRequestDTO;
import data.denarius.radarius.dto.ProtocolResponseDTO;
import data.denarius.radarius.services.ProtocolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protocols")
public class ProtocolController {

    private final ProtocolService service;

    public ProtocolController(ProtocolService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProtocolResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocolResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProtocolResponseDTO> create(@RequestBody ProtocolRequestDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProtocolResponseDTO> update(@PathVariable Integer id, @RequestBody ProtocolRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
