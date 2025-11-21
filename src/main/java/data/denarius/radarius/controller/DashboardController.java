// src/main/java/data/denarius/radarius/controller/DashboardController.java
package data.denarius.radarius.controller;

import data.denarius.radarius.dto.metrics.AroundTimeVehiclesDTO;
import data.denarius.radarius.dto.metrics.HourlyVehiclesDTO;
import data.denarius.radarius.dto.metrics.RoadDailyAggregateDTO;
import data.denarius.radarius.service.TrafficMetricsService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/v1/metrics")
public class DashboardController {

    private final TrafficMetricsService service;

    public DashboardController(TrafficMetricsService service) {
        this.service = service;
    }

    // 1) Veículos por HORA para UMA via no intervalo
    @GetMapping("/region/{regionId}/road/{roadId}/hourly")
    public ResponseEntity<List<HourlyVehiclesDTO>> vehiclesPerHourForRoad(
            @PathVariable @NotNull Integer regionId,
            @PathVariable @NotNull Integer roadId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (!end.isAfter(start)) return ResponseEntity.badRequest().build();
        var result = service.vehiclesPerHourForRoad(regionId, roadId, start, end);
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    // 2) Por vias da região em um dia ou intervalo (compatível com ?date=yyyy-MM-dd ou ?start=...&end=...)
    // exemplo compatível antiga: /v1/metrics/region/5/roads/daily?date=2025-07-29
    // novo: /v1/metrics/region/5/roads/daily?start=2025-07-01&end=2025-07-29
    @GetMapping("/region/{regionId}/roads/daily")
    public ResponseEntity<List<RoadDailyAggregateDTO>> vehiclesPerHourByRoadForRange(
            @PathVariable @NotNull Integer regionId,
            // alias antigo
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            // nova maneira
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        // Prioridade: se start fornecido, use start/end. Caso contrário, se date fornecido, trate como single-day.
        LocalDate s;
        LocalDate e;

        if (start != null) {
            s = start;
            e = (end != null) ? end : start;
        } else if (date != null) {
            s = date;
            e = date;
        } else {
            // nenhum parâmetro -> bad request (exige pelo menos date ou start)
            return ResponseEntity.badRequest().build();
        }

        if (e.isBefore(s)) {
            return ResponseEntity.badRequest().build();
        }

        var result = service.vehiclesPerHourByRoadForRange(regionId, s, e);
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    // 3) Ao redor de uma hora (± janela em minutos)
    @GetMapping("/region/{regionId}/road/{roadId}/around")
    public ResponseEntity<AroundTimeVehiclesDTO> vehiclesAroundTime(
            @PathVariable @NotNull Integer regionId,
            @PathVariable @NotNull Integer roadId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time,
            @RequestParam(defaultValue = "5") @Min(1) int windowMinutes) {
        var dto = service.vehiclesAroundTime(regionId, roadId, time, windowMinutes);
        return ResponseEntity.ok(dto);
    }
}
