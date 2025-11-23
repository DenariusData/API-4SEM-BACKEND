package data.denarius.radarius.controller;

import data.denarius.radarius.dto.protocol.ProtocolRequestDTO;
import data.denarius.radarius.dto.protocol.ProtocolResponseDTO;
import data.denarius.radarius.security.annotations.RequireAgenteOrGestorRole;
import data.denarius.radarius.security.annotations.RequireGestorRole;
import data.denarius.radarius.service.ProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protocols")
@RequireGestorRole
public class ProtocolController {

    @Autowired
    private ProtocolService protocolService;

    @PostMapping
    public ResponseEntity<ProtocolResponseDTO> create(@RequestBody ProtocolRequestDTO dto) {
        return ResponseEntity.ok(protocolService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProtocolResponseDTO> update(@PathVariable Integer id, @RequestBody ProtocolRequestDTO dto) {
        return ResponseEntity.ok(protocolService.update(id, dto));
    }

    @GetMapping("/root-cause/{rootCauseId}")
    @RequireAgenteOrGestorRole
    public ResponseEntity<ProtocolResponseDTO> findByRootCause(@PathVariable Integer rootCauseId) {
        return ResponseEntity.ok(protocolService.findByRootCause(rootCauseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocolResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(protocolService.findById(id));
    }

    @GetMapping
    @RequireAgenteOrGestorRole
    public ResponseEntity<List<ProtocolResponseDTO>> search(@RequestParam (required = false) String name) {
        if (name != null && !name.isEmpty())
            return ResponseEntity.ok(protocolService.search(name));

        return ResponseEntity.ok(protocolService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        protocolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
