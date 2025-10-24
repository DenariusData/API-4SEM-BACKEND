package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    Optional<Camera> findByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);
    List<Camera> findByRoad(Road road);
}
