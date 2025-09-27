package data.denarius.radarius.controller;

import data.denarius.radarius.entity.CriterionLevel;
import data.denarius.radarius.service.CriterionLevelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criterion-levels")
public class CriterionLevelController {

    private final CriterionLevelService criterionLevelService;

    public CriterionLevelController(CriterionLevelService criterionLevelService) {
        this.criterionLevelService = criterionLevelService;
    }

    @GetMapping
    public ResponseEntity<List<CriterionLevel>> getAll() {
        return ResponseEntity.ok(criterionLevelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterionLevel> getById(@PathVariable Integer id) {
        return criterionLevelService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CriterionLevel> create(@RequestBody CriterionLevel criterionLevel) {
        return ResponseEntity.ok(criterionLevelService.save(criterionLevel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterionLevel> update(@PathVariable Integer id, @RequestBody CriterionLevel criterionLevel) {
        return ResponseEntity.ok(criterionLevelService.update(id, criterionLevel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        criterionLevelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
