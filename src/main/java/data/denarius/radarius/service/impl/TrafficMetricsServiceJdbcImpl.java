// src/main/java/data/denarius/radarius/service/impl/TrafficMetricsServiceJdbcImpl.java
package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.metrics.AroundTimeVehiclesDTO;
import data.denarius.radarius.dto.metrics.HourlyVehiclesDTO;
import data.denarius.radarius.dto.metrics.RoadDailyAggregateDTO;
import data.denarius.radarius.service.TrafficMetricsService;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Comparator;

@Service
@Transactional(readOnly = true)
public class TrafficMetricsServiceJdbcImpl implements TrafficMetricsService {

    private final NamedParameterJdbcTemplate jdbc;

    // Se precisar prefixar com schema, coloque aqui: "OWNER.CAMERA" / "OWNER.READING"
    private static final String T_CAMERA = "CAMERA";
    private static final String T_READING = "READING";

    public TrafficMetricsServiceJdbcImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<HourlyVehiclesDTO> vehiclesPerHourForRoad(Integer regionId, Integer roadId, LocalDateTime start, LocalDateTime end) {
        final String sql = "SELECT TRUNC(CAST(r.REA_CREATED_AT AS DATE), 'HH') AS bucket,\n"
                + "       COUNT(*) AS cnt,\n"
                + "       AVG(r.REA_SPEED) AS avg_speed\n"
                + "FROM " + T_CAMERA + " c\n"
                + "JOIN " + T_READING + " r ON r.CAM_ID = c.CAM_ID\n"
                + "WHERE c.REG_ID = :regionId\n"
                + "  AND c.ROA_ID = :roadId\n"
                + "  AND c.CAM_ACTIVE = 1\n"
                + "  AND r.REA_CREATED_AT >= :start\n"
                + "  AND r.REA_CREATED_AT < :end\n"
                + "GROUP BY TRUNC(CAST(r.REA_CREATED_AT AS DATE), 'HH')\n"
                + "ORDER BY bucket";

        var params = new HashMap<String, Object>();
        params.put("regionId", regionId);
        params.put("roadId", roadId);
        params.put("start", Timestamp.valueOf(start));
        params.put("end", Timestamp.valueOf(end));

        return jdbc.query(sql, params, (rs, n) -> new HourlyVehiclesDTO(
                rs.getTimestamp("bucket").toLocalDateTime(),
                roadId,
                rs.getLong("cnt"),
                Optional.ofNullable((Number) rs.getObject("avg_speed"))
                        .map(Number::doubleValue).orElse(null)
        ));
    }

    @Override
    public List<RoadDailyAggregateDTO> vehiclesPerHourByRoadForDay(Integer regionId, LocalDate date) {
        // mantém compatibilidade: delega para range com same start/end
        return vehiclesPerHourByRoadForRange(regionId, date, date);
    }

    @Override
    public List<RoadDailyAggregateDTO> vehiclesPerHourByRoadForRange(Integer regionId, LocalDate startDate, LocalDate endDate) {
        // queremos intervalo [startDate, endDate inclusive] -> convert para [start, endExclusive)
        var start = startDate.atStartOfDay();
        var end = endDate.plusDays(1).atStartOfDay();

        final String sql = "SELECT c.ROA_ID AS road_id,\n"
                + "       TRUNC(CAST(r.REA_CREATED_AT AS DATE), 'HH') AS bucket,\n"
                + "       COUNT(*) AS cnt,\n"
                + "       AVG(r.REA_SPEED) AS avg_speed\n"
                + "FROM " + T_CAMERA + " c\n"
                + "JOIN " + T_READING + " r ON r.CAM_ID = c.CAM_ID\n"
                + "WHERE c.REG_ID = :regionId\n"
                + "  AND c.CAM_ACTIVE = 1\n"
                + "  AND r.REA_CREATED_AT >= :start\n"
                + "  AND r.REA_CREATED_AT < :end\n"
                + "GROUP BY c.ROA_ID, TRUNC(CAST(r.REA_CREATED_AT AS DATE), 'HH')\n"
                + "ORDER BY c.ROA_ID, bucket";

        var params = new HashMap<String, Object>();
        params.put("regionId", regionId);
        params.put("start", Timestamp.valueOf(start));
        params.put("end", Timestamp.valueOf(end));

        var rows = jdbc.query(sql, params, (rs, n) -> {
            Map<String, Object> m = new HashMap<>();
            m.put("road_id", rs.getInt("road_id"));
            m.put("bucket", rs.getTimestamp("bucket").toLocalDateTime());
            m.put("cnt", rs.getLong("cnt"));
            m.put("avg_speed", Optional.ofNullable((Number) rs.getObject("avg_speed"))
                    .map(Number::doubleValue).orElse(null));
            return m;
        });

        if (rows.isEmpty()) return List.of();

        Map<Integer, List<HourlyVehiclesDTO>> byRoad = new LinkedHashMap<>();
        for (var r : rows) {
            Integer roaId = (Integer) r.get("road_id");
            LocalDateTime hour = (LocalDateTime) r.get("bucket");
            long cnt = (Long) r.get("cnt");
            Double avg = (Double) r.get("avg_speed");
            byRoad.computeIfAbsent(roaId, k -> new ArrayList<>())
                    .add(new HourlyVehiclesDTO(hour, roaId, cnt, avg));
        }

        List<RoadDailyAggregateDTO> out = new ArrayList<>();
        for (var e : byRoad.entrySet()) {
            var hrs = e.getValue().stream()
                    .sorted(Comparator.comparing(HourlyVehiclesDTO::hour))
                    .toList();
            long total = hrs.stream().mapToLong(HourlyVehiclesDTO::vehicleCount).sum();
            // não temos nome da via aqui → null
            out.add(new RoadDailyAggregateDTO(e.getKey(), null, total, hrs));
        }

        out.sort(Comparator.comparing(RoadDailyAggregateDTO::roadId));
        return out;
    }

    @Override
    public AroundTimeVehiclesDTO vehiclesAroundTime(Integer regionId, Integer roadId, LocalDateTime targetTime, int windowMinutes) {
        var from = targetTime.minusMinutes(windowMinutes);
        var to = targetTime.plusMinutes(windowMinutes);

        final String sql = "SELECT TRUNC(CAST(r.REA_CREATED_AT AS DATE), 'MI') AS bucket,\n"
                + "       COUNT(*) AS cnt\n"
                + "FROM " + T_CAMERA + " c\n"
                + "JOIN " + T_READING + " r ON r.CAM_ID = c.CAM_ID\n"
                + "WHERE c.REG_ID = :regionId\n"
                + "  AND c.ROA_ID = :roadId\n"
                + "  AND c.CAM_ACTIVE = 1\n"
                + "  AND r.REA_CREATED_AT >= :fromTs\n"
                + "  AND r.REA_CREATED_AT <= :toTs\n"
                + "GROUP BY TRUNC(CAST(r.REA_CREATED_AT AS DATE), 'MI')\n"
                + "ORDER BY bucket";

        var params = new HashMap<String, Object>();
        params.put("regionId", regionId);
        params.put("roadId", roadId);
        params.put("fromTs", Timestamp.valueOf(from));
        params.put("toTs", Timestamp.valueOf(to));

        Map<LocalDateTime, Long> perMinute = new LinkedHashMap<>();
        jdbc.query(sql, params, rs -> {
            perMinute.put(rs.getTimestamp("bucket").toLocalDateTime(), rs.getLong("cnt"));
        });

        long total = perMinute.values().stream().mapToLong(Long::longValue).sum();

        return new AroundTimeVehiclesDTO(regionId, roadId, targetTime, windowMinutes, from, to, total, perMinute);
    }
}
