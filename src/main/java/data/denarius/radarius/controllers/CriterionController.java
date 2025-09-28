package data.denarius.radarius.controllers;

import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.service.CriterionService;
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
    public ResponseEntity<List<Criterion>> getAll() {
        return ResponseEntity.ok(criterionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Criterion> getById(@PathVariable Integer id) {
        return criterionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Criterion> create(@RequestBody Criterion criterion) {
        return ResponseEntity.ok(criterionService.save(criterion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Criterion> update(@PathVariable Integer id, @RequestBody Criterion criterion) {
        return ResponseEntity.ok(criterionService.update(id, criterion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        criterionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
