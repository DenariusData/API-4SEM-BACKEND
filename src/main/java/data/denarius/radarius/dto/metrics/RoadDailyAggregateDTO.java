package data.denarius.radarius.dto.metrics;

import java.util.List;

public record RoadDailyAggregateDTO(
        Integer roadId,
        String roadName,
        long totalCount,
        List<HourlyVehiclesDTO> hours
) {}
