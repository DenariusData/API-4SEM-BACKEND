package data.denarius.radarius.controller;

import data.denarius.radarius.dto.rootcause.RootCauseRequestDTO;
import data.denarius.radarius.dto.rootcause.RootCauseResponseDTO;
import data.denarius.radarius.service.RootCauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/root-causes")
public class RootCauseController {

    @Autowired
    private RootCauseService rootCauseService;

    @PostMapping
    public ResponseEntity<RootCauseResponseDTO> create(@RequestBody RootCauseRequestDTO dto) {
        return ResponseEntity.ok(rootCauseService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RootCauseResponseDTO> update(@PathVariable Integer id, @RequestBody RootCauseRequestDTO dto) {
        return ResponseEntity.ok(rootCauseService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RootCauseResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(rootCauseService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RootCauseResponseDTO>> findAll() {
        return ResponseEntity.ok(rootCauseService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rootCauseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
