// src/main/java/data/denarius/radarius/service/TrafficMetricsService.java
package data.denarius.radarius.service;

import data.denarius.radarius.dto.metrics.AroundTimeVehiclesDTO;
import data.denarius.radarius.dto.metrics.HourlyVehiclesDTO;
import data.denarius.radarius.dto.metrics.RoadDailyAggregateDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TrafficMetricsService {
    List<HourlyVehiclesDTO> vehiclesPerHourForRoad(Integer regionId, Integer roadId, LocalDateTime start, LocalDateTime end);

    List<RoadDailyAggregateDTO> vehiclesPerHourByRoadForDay(Integer regionId, LocalDate date);

    // novo: intervalo (inclusivo em dias: start..end)
    List<RoadDailyAggregateDTO> vehiclesPerHourByRoadForRange(Integer regionId, LocalDate startDate, LocalDate endDate);

    AroundTimeVehiclesDTO vehiclesAroundTime(Integer regionId, Integer roadId, LocalDateTime targetTime, int windowMinutes);
}
