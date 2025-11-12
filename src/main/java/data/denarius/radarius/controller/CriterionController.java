package data.denarius.radarius.controller;

import data.denarius.radarius.dto.criterion.CriterionRequestDTO;
import data.denarius.radarius.dto.criterion.CriterionResponseDTO;
import data.denarius.radarius.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criterion")
public class CriterionController {

    @Autowired
    private CriterionService criterionService;

    @PostMapping
    public ResponseEntity<CriterionResponseDTO> create(@RequestBody CriterionRequestDTO dto) {
        return ResponseEntity.ok(criterionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionResponseDTO> update(@PathVariable Integer id, @RequestBody CriterionRequestDTO dto) {
        return ResponseEntity.ok(criterionService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(criterionService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CriterionResponseDTO>> findAll() {
        return ResponseEntity.ok(criterionService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        criterionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<List<CriterionResponseDTO>> getCriteriaSummary() {
        return ResponseEntity.ok(criterionService.getCriteriaSummary());
    }
}
