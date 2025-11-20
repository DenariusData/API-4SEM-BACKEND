package data.denarius.radarius.controller;

import data.denarius.radarius.dto.alert.AlertLevelPerRegionDTO;
import data.denarius.radarius.dto.alert.AlertRequestDTO;
import data.denarius.radarius.dto.alert.AlertResponseDTO;
import data.denarius.radarius.dto.alertlog.AlertLogRecentResponseDTO;
import data.denarius.radarius.security.annotations.RequireAgenteOrGestorRole;
import data.denarius.radarius.security.annotations.RequireGestorRole;
import data.denarius.radarius.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping
    @RequireGestorRole
    public ResponseEntity<AlertResponseDTO> create(@RequestBody AlertRequestDTO dto) {
        return ResponseEntity.ok(alertService.create(dto));
    }

    @PutMapping("/{id}")
    @RequireGestorRole
    public ResponseEntity<AlertResponseDTO> update(@PathVariable Integer id, @RequestBody AlertRequestDTO dto) {
        return ResponseEntity.ok(alertService.update(id, dto));
    }

    @GetMapping("/{id}")
    @RequireAgenteOrGestorRole
    public ResponseEntity<AlertResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(alertService.findById(id));
    }

    @GetMapping
    @RequireAgenteOrGestorRole
    public ResponseEntity<List<AlertResponseDTO>> findAll() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @DeleteMapping("/{id}")
    @RequireGestorRole
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/last-ten")
    @RequireAgenteOrGestorRole
    public ResponseEntity<List<AlertLogRecentResponseDTO>> getLast10AlertLogs(
            @RequestParam(required = false) Integer regionId) {
        return ResponseEntity.ok(alertService.getLast10AlertLogs(regionId));
    }

    @GetMapping("/search")
    @RequireAgenteOrGestorRole
    public ResponseEntity<Page<AlertResponseDTO>> getWithFilters(
            @RequestParam(required = false) List<Integer> regionIds,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        return ResponseEntity.ok(alertService.getAlertsWithFilters(regionIds, start, end, page, size));
    }

    @GetMapping("/top5/region")
    public ResponseEntity<List<AlertResponseDTO>> getTop5ByRegion(
            @RequestParam List<Integer> regionIds
    ) {
        return ResponseEntity.ok(alertService.getTop5WorstByRegion(regionIds));
    }

    @GetMapping("/top5/region/criterion/{criterionId}")
    public ResponseEntity<List<AlertResponseDTO>> getTop5ByRegionAndCriterion(
            @RequestParam List<Integer> regionIds,
            @PathVariable Integer criterionId
    ) {
        return ResponseEntity.ok(alertService.getTop5WorstByRegionAndCriterion(regionIds, criterionId));
    }

    @GetMapping("/per-region")
    public ResponseEntity<List<AlertLevelPerRegionDTO>> getAverageLevelPerRegion() {
        return ResponseEntity.ok(alertService.getAverageLevelPerRegion());
    }

}
