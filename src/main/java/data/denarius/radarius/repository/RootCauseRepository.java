package data.denarius.radarius.repository;

import data.denarius.radarius.entity.RootCause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RootCauseRepository extends JpaRepository<RootCause, Integer> {
    List<RootCause> findByNameContainingIgnoreCase(String query);
}
