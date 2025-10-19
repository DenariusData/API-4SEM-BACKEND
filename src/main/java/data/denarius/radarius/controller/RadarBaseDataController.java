package data.denarius.radarius.controller;

import data.denarius.radarius.dto.radarbasedata.RadarBaseDataRequestDTO;
import data.denarius.radarius.dto.radarbasedata.RadarBaseDataResponseDTO;
import data.denarius.radarius.scheduler.RadarBaseDataScheduler;
import data.denarius.radarius.service.RadarBaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/radar-base-data")
@CrossOrigin(origins = "*")
public class RadarBaseDataController {

    @Autowired
    private RadarBaseDataService radarBaseDataService;

    @Autowired
    private RadarBaseDataScheduler radarBaseDataScheduler;

    @PostMapping("/force-processing")
    public ResponseEntity<Map<String, String>> forceProcessing() {
        radarBaseDataScheduler.forceProcessing();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Processing forced successfully");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RadarBaseDataResponseDTO> create(@RequestBody RadarBaseDataRequestDTO dto) {
        return ResponseEntity.ok(radarBaseDataService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RadarBaseDataResponseDTO> update(@PathVariable Long id, @RequestBody RadarBaseDataRequestDTO dto) {
        return ResponseEntity.ok(radarBaseDataService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RadarBaseDataResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(radarBaseDataService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findAll() {
        return ResponseEntity.ok(radarBaseDataService.findAll());
    }

    @GetMapping("/ordered-by-date")
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findAllOrderByDateTimeDesc() {
        return ResponseEntity.ok(radarBaseDataService.findAllOrderByDateTimeDesc());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findByCity(@PathVariable String city) {
        return ResponseEntity.ok(radarBaseDataService.findByCity(city));
    }

    @GetMapping("/vehicle-type/{vehicleType}")
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findByVehicleType(@PathVariable String vehicleType) {
        return ResponseEntity.ok(radarBaseDataService.findByVehicleType(vehicleType));
    }

    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findByCameraId(@PathVariable String cameraId) {
        return ResponseEntity.ok(radarBaseDataService.findByCameraId(cameraId));
    }

    @GetMapping("/direction/{direction}")
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findByDirection(@PathVariable String direction) {
        return ResponseEntity.ok(radarBaseDataService.findByDirection(direction));
    }

    @GetMapping("/above-speed-limit")
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findVehiclesAboveSpeedLimit() {
        return ResponseEntity.ok(radarBaseDataService.findVehiclesAboveSpeedLimit());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RadarBaseDataResponseDTO>> findRecentRecords() {
        return ResponseEntity.ok(radarBaseDataService.findRecentRecords());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        radarBaseDataService.delete(id);
        return ResponseEntity.noContent().build();
    }
}