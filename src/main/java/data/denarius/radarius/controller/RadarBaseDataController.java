package data.denarius.radarius.controller;

import data.denarius.radarius.scheduler.RadarBaseDataScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/radar-base-data")
@CrossOrigin(origins = "*")
public class RadarBaseDataController {

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
}
