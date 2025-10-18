package data.denarius.radarius.service;

import data.denarius.radarius.dto.dadosbaseradares.DadosBaseRadaresRequestDTO;
import data.denarius.radarius.dto.dadosbaseradares.DadosBaseRadaresResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface DadosBaseRadaresService {
    DadosBaseRadaresResponseDTO create(DadosBaseRadaresRequestDTO dto);
    DadosBaseRadaresResponseDTO update(Long id, DadosBaseRadaresRequestDTO dto);
    void delete(Long id);
    DadosBaseRadaresResponseDTO findById(Long id);
    List<DadosBaseRadaresResponseDTO> findAll();
    List<DadosBaseRadaresResponseDTO> findByCidade(String cidade);
    List<DadosBaseRadaresResponseDTO> findByTipoVeiculo(String tipoVeiculo);
    List<DadosBaseRadaresResponseDTO> findByCameraId(String cameraId);
    List<DadosBaseRadaresResponseDTO> findBySentido(String sentido);
    List<DadosBaseRadaresResponseDTO> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    List<DadosBaseRadaresResponseDTO> findAllOrderByDataHoraDesc();
    List<DadosBaseRadaresResponseDTO> findVeiculosAcimaVelocidade();
    List<DadosBaseRadaresResponseDTO> findByCidadeAndTipoVeiculo(String cidade, String tipoVeiculo);
    List<DadosBaseRadaresResponseDTO> findRecentRecords();
}