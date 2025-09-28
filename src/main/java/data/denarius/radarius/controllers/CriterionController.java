package data.denarius.radarius.controllers;

import data.denarius.radarius.dtos.criterion.CriterionRequestDTO;
import data.denarius.radarius.dtos.criterion.CriterionResponseDTO;
import data.denarius.radarius.services.CriterionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criteria")
public class CriterionController {

    private final CriterionService criterionService;

    public CriterionController(CriterionService criterionService) {
        this.criterionService = criterionService;
    }

    @GetMapping
    public ResponseEntity<List<CriterionResponseDTO>> getAll() {
        return ResponseEntity.ok(criterionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(criterionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CriterionResponseDTO> create(@RequestBody CriterionRequestDTO dto) {
        return ResponseEntity.ok(criterionService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionResponseDTO> update(@PathVariable Integer id,
                                                       @RequestBody CriterionRequestDTO dto) {
        return ResponseEntity.ok(criterionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        criterionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
