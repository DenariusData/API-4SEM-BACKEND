package data.denarius.radarius.controller;

import data.denarius.radarius.entity.Reading;
import data.denarius.radarius.service.ReadingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/readings")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @GetMapping
    public ResponseEntity<List<Reading>> getAll() {
        return ResponseEntity.ok(readingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reading> getById(@PathVariable Integer id) {
        return readingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reading> create(@RequestBody Reading reading) {
        return ResponseEntity.ok(readingService.save(reading));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reading> update(@PathVariable Integer id, @RequestBody Reading reading) {
        return ResponseEntity.ok(readingService.update(id, reading));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        readingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
