package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoadRepository extends JpaRepository<Road, Integer> {
    Optional<Road> findByAddress(String address);
}
