package data.denarius.radarius.controller;

import data.denarius.radarius.entity.Protocol;
import data.denarius.radarius.service.ProtocolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protocols")
public class ProtocolController {

    private final ProtocolService protocolService;

    public ProtocolController(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @GetMapping
    public ResponseEntity<List<Protocol>> getAll() {
        return ResponseEntity.ok(protocolService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Protocol> getById(@PathVariable Integer id) {
        return protocolService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Protocol> create(@RequestBody Protocol protocol) {
        return ResponseEntity.ok(protocolService.save(protocol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Protocol> update(@PathVariable Integer id, @RequestBody Protocol protocol) {
        return ResponseEntity.ok(protocolService.update(id, protocol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        protocolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
