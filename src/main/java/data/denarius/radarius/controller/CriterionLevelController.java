package data.denarius.radarius.controller;

import data.denarius.radarius.dto.criterionlevel.CriterionLevelRequestDTO;
import data.denarius.radarius.dto.criterionlevel.CriterionLevelResponseDTO;
import data.denarius.radarius.service.CriterionLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criterion-levels")
public class CriterionLevelController {

    @Autowired
    private CriterionLevelService criterionLevelService;

    @PostMapping
    public ResponseEntity<CriterionLevelResponseDTO> create(@RequestBody CriterionLevelRequestDTO dto) {
        return ResponseEntity.ok(criterionLevelService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionLevelResponseDTO> update(@PathVariable Integer id, @RequestBody CriterionLevelRequestDTO dto) {
        return ResponseEntity.ok(criterionLevelService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionLevelResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(criterionLevelService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CriterionLevelResponseDTO>> findAll() {
        return ResponseEntity.ok(criterionLevelService.findAll());
    }

    @GetMapping("/by-criterion/{criterionId}")
    public ResponseEntity<List<CriterionLevelResponseDTO>> findByCriterionId(@PathVariable Integer criterionId) {
        return ResponseEntity.ok(criterionLevelService.findByCriterionId(criterionId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        criterionLevelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
