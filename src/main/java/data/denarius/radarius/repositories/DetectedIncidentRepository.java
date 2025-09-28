package data.denarius.radarius.repositories;

import data.denarius.radarius.entity.DetectedIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetectedIncidentRepository extends JpaRepository<DetectedIncident, Integer> {
}
