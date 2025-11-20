package data.denarius.radarius.repository;

import data.denarius.radarius.entity.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProtocolRepository extends JpaRepository<Protocol, Integer> {
    List<Protocol> findByNameContainingIgnoreCase(String query);
    
    @Query("SELECT DISTINCT p FROM Protocol p JOIN Alert a ON a.protocol = p WHERE a.rootCause.id = :rootCauseId")
    List<Protocol> findByRootCauseId(@Param("rootCauseId") Integer rootCauseId);
}
