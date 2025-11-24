package data.denarius.radarius.dto.metrics;

import java.time.LocalDateTime;

public record HourlyVehiclesDTO(
        LocalDateTime hour,
        Integer roadId,
        long vehicleCount,
        Double avgSpeedKmh // pode ser null se não existir coluna de velocidade
) {}
