package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.GeolocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GeolocationServiceImpl implements GeolocationService {

    @Autowired
    private RegionRepository regionRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public Optional<Region> determineRegionFromCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            log.warn("Invalid coordinates provided: lat={}, lon={}", latitude, longitude);
            return Optional.empty();
        }

        try {
            List<Region> allRegions = regionRepository.findAllWithGeolocationData();
            
            Region closestRegion = null;
            double minDistance = Double.MAX_VALUE;

            for (Region region : allRegions) {
                if (region.getCenterLatitude() == null || region.getCenterLongitude() == null || region.getRadiusKm() == null) {
                    log.debug("Region {} has incomplete geolocation data, skipping", region.getName());
                    continue;
                }

                double distance = calculateHaversineDistance(
                    latitude, longitude,
                    region.getCenterLatitude(), region.getCenterLongitude()
                );

                log.debug("Distance from coordinates ({}, {}) to region '{}': {:.2f}km (radius: {}km)", 
                    latitude, longitude, region.getName(), distance, region.getRadiusKm());

                if (distance <= region.getRadiusKm().doubleValue() && distance < minDistance) {
                    minDistance = distance;
                    closestRegion = region;
                }
            }

            if (closestRegion != null) {
                return Optional.of(closestRegion);
            } else {
                log.warn("No region found for coordinates ({}, {})", latitude, longitude);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Error determining region from coordinates ({}, {}): {}", latitude, longitude, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public double calculateHaversineDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new IllegalArgumentException("All coordinates must be non-null");
        }

        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lon1Rad = Math.toRadians(lon1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
