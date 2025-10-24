package data.denarius.radarius.service;

import data.denarius.radarius.entity.Region;

import java.math.BigDecimal;
import java.util.Optional;

public interface GeolocationService {
    Optional<Region> determineRegionFromCoordinates(BigDecimal latitude, BigDecimal longitude);
    double calculateHaversineDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2);
}
