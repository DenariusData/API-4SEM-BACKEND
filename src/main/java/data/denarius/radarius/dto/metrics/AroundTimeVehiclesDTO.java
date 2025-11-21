package data.denarius.radarius.dto.metrics;

import java.time.LocalDateTime;
import java.util.Map;

public record AroundTimeVehiclesDTO(
        Integer regionId,
        Integer roadId,
        LocalDateTime targetTime, // hora solicitada
        int windowMinutes,        // janela +/- em minutos
        LocalDateTime from,       // targetTime - window
        LocalDateTime to,         // targetTime + window
        long totalCount,          // total no intervalo
        Map<LocalDateTime, Long> perMinute // contagem por minuto
) {}
