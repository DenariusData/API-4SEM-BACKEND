package data.denarius.radarius.controllers;

import data.denarius.radarius.dto.CriterionLevelRequestDTO;
import data.denarius.radarius.dto.CriterionLevelResponseDTO;
import data.denarius.radarius.service.CriterionLevelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criterion-levels")
public class CriterionLevelController {

    private final CriterionLevelService service;

    public CriterionLevelController(CriterionLevelService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CriterionLevelResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionLevelResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<CriterionLevelResponseDTO> create(@RequestBody CriterionLevelRequestDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionLevelResponseDTO> update(@PathVariable Integer id,
                                                            @RequestBody CriterionLevelRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
