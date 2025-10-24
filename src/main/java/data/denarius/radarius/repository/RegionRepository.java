package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findByName(String name);
    
    @Query("SELECT r FROM Region r WHERE r.centerLatitude IS NOT NULL AND r.centerLongitude IS NOT NULL AND r.radiusKm IS NOT NULL")
    List<Region> findAllWithGeolocationData();
}
