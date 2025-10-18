package data.denarius.radarius.repository;

import data.denarius.radarius.entity.DadosBaseRadares;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DadosBaseRadaresRepository extends JpaRepository<DadosBaseRadares, Long> {
    
    // Buscar por cidade
    List<DadosBaseRadares> findByCidadeIgnoreCase(String cidade);
    
    // Buscar por tipo de veículo
    List<DadosBaseRadares> findByTipoVeiculoIgnoreCase(String tipoVeiculo);
    
    // Buscar por camera ID
    List<DadosBaseRadares> findByCameraId(String cameraId);
    
    // Buscar por sentido
    List<DadosBaseRadares> findBySentidoIgnoreCase(String sentido);
    
    // Buscar por período de data/hora
    List<DadosBaseRadares> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    // Buscar registros ordenados por data/hora mais recente
    @Query("SELECT d FROM DadosBaseRadares d ORDER BY d.dataHora DESC")
    List<DadosBaseRadares> findAllOrderByDataHoraDesc();
    
    // Buscar por velocidade acima do limite regulamentado
    @Query("SELECT d FROM DadosBaseRadares d WHERE d.velocidadeVeiculo > d.velocidadeRegulamentada ORDER BY d.dataHora DESC")
    List<DadosBaseRadares> findVeiculosAcimaVelocidade();
    
    // Buscar por cidade e tipo de veículo
    List<DadosBaseRadares> findByCidadeIgnoreCaseAndTipoVeiculoIgnoreCase(String cidade, String tipoVeiculo);
    
    // Buscar os registros mais recentes (últimas 24 horas)
    @Query("SELECT d FROM DadosBaseRadares d WHERE d.dataHora >= :dataInicio ORDER BY d.dataHora DESC")
    List<DadosBaseRadares> findRecentRecords(@Param("dataInicio") LocalDateTime dataInicio);
    
    // Buscar registros agrupados por câmera
    @Query("SELECT d FROM DadosBaseRadares d WHERE d.cameraId = :cameraId ORDER BY d.dataHora DESC")
    List<DadosBaseRadares> findByCameraIdOrderByDataHora(@Param("cameraId") String cameraId);
    
    // Buscar registros não processados (mais antigos primeiro) - para processamento em lote
    @Query("SELECT d FROM DadosBaseRadares d WHERE d.processado = false ORDER BY d.dataHora ASC")
    List<DadosBaseRadares> findUnprocessedRecordsOrderByOldest(Pageable pageable);
    
    // Contar registros não processados
    @Query("SELECT COUNT(d) FROM DadosBaseRadares d WHERE d.processado = false")
    Long countUnprocessedRecords();
    
    // Marcar registro como processado
    @Modifying
    @Query("UPDATE DadosBaseRadares d SET d.processado = true WHERE d.id = :id")
    void markAsProcessed(@Param("id") Long id);
    
    // Resetar todos os registros para não processados (para reprocessamento)
    @Modifying
    @Query("UPDATE DadosBaseRadares d SET d.processado = false")
    void resetAllProcessedFlags();
}