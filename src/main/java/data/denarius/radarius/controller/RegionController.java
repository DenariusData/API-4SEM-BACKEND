package data.denarius.radarius.controller;

import data.denarius.radarius.dto.region.RegionRequestDTO;
import data.denarius.radarius.dto.region.RegionResponseDTO;
import data.denarius.radarius.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @PostMapping
    public ResponseEntity<RegionResponseDTO> create(@RequestBody RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionResponseDTO> update(@PathVariable Integer id, @RequestBody RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(regionService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RegionResponseDTO>> findAll() {
        return ResponseEntity.ok(regionService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        regionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
