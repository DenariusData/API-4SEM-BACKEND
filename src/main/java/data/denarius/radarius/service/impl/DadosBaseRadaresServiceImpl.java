package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.dadosbaseradares.DadosBaseRadaresRequestDTO;
import data.denarius.radarius.dto.dadosbaseradares.DadosBaseRadaresResponseDTO;
import data.denarius.radarius.entity.DadosBaseRadares;
import data.denarius.radarius.repository.DadosBaseRadaresRepository;
import data.denarius.radarius.service.DadosBaseRadaresService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DadosBaseRadaresServiceImpl implements DadosBaseRadaresService {

    @Autowired
    private DadosBaseRadaresRepository dadosBaseRadaresRepository;

    @Override
    public DadosBaseRadaresResponseDTO create(DadosBaseRadaresRequestDTO dto) {
        DadosBaseRadares dadosBaseRadares = mapToEntity(dto);
        return mapToDTO(dadosBaseRadaresRepository.save(dadosBaseRadares));
    }

    @Override
    public DadosBaseRadaresResponseDTO update(Long id, DadosBaseRadaresRequestDTO dto) {
        DadosBaseRadares dadosBaseRadares = dadosBaseRadaresRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dados base radares não encontrado"));
        updateEntity(dadosBaseRadares, dto);
        return mapToDTO(dadosBaseRadaresRepository.save(dadosBaseRadares));
    }

    @Override
    public void delete(Long id) {
        dadosBaseRadaresRepository.deleteById(id);
    }

    @Override
    public DadosBaseRadaresResponseDTO findById(Long id) {
        return dadosBaseRadaresRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Dados base radares não encontrado"));
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findAll() {
        return dadosBaseRadaresRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findByCidade(String cidade) {
        return dadosBaseRadaresRepository.findByCidadeIgnoreCase(cidade).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findByTipoVeiculo(String tipoVeiculo) {
        return dadosBaseRadaresRepository.findByTipoVeiculoIgnoreCase(tipoVeiculo).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findByCameraId(String cameraId) {
        return dadosBaseRadaresRepository.findByCameraId(cameraId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findBySentido(String sentido) {
        return dadosBaseRadaresRepository.findBySentidoIgnoreCase(sentido).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim) {
        return dadosBaseRadaresRepository.findByDataHoraBetween(inicio, fim).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findAllOrderByDataHoraDesc() {
        return dadosBaseRadaresRepository.findAllOrderByDataHoraDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findVeiculosAcimaVelocidade() {
        return dadosBaseRadaresRepository.findVeiculosAcimaVelocidade().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findByCidadeAndTipoVeiculo(String cidade, String tipoVeiculo) {
        return dadosBaseRadaresRepository.findByCidadeIgnoreCaseAndTipoVeiculoIgnoreCase(cidade, tipoVeiculo).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DadosBaseRadaresResponseDTO> findRecentRecords() {
        LocalDateTime dataInicio = LocalDateTime.now().minusHours(24);
        return dadosBaseRadaresRepository.findRecentRecords(dataInicio).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private DadosBaseRadares mapToEntity(DadosBaseRadaresRequestDTO dto) {
        DadosBaseRadares dadosBaseRadares = new DadosBaseRadares();
        updateEntity(dadosBaseRadares, dto);
        return dadosBaseRadares;
    }

    private void updateEntity(DadosBaseRadares dadosBaseRadares, DadosBaseRadaresRequestDTO dto) {
        dadosBaseRadares.setCameraLat(dto.getCameraLat());
        dadosBaseRadares.setCameraLong(dto.getCameraLong());
        dadosBaseRadares.setCameraId(dto.getCameraId());
        dadosBaseRadares.setFaixaDaCamera(dto.getFaixaDaCamera());
        dadosBaseRadares.setQuantidadeDeFaixas(dto.getQuantidadeDeFaixas());
        dadosBaseRadares.setDataHora(dto.getDataHora());
        dadosBaseRadares.setTipoVeiculo(dto.getTipoVeiculo());
        dadosBaseRadares.setVelocidadeVeiculo(dto.getVelocidadeVeiculo());
        dadosBaseRadares.setVelocidadeRegulamentada(dto.getVelocidadeRegulamentada());
        dadosBaseRadares.setEndereco(dto.getEndereco());
        dadosBaseRadares.setNumero(dto.getNumero());
        dadosBaseRadares.setCidade(dto.getCidade());
        dadosBaseRadares.setSentido(dto.getSentido());
    }

    private DadosBaseRadaresResponseDTO mapToDTO(DadosBaseRadares dadosBaseRadares) {
        DadosBaseRadaresResponseDTO dto = new DadosBaseRadaresResponseDTO();
        dto.setId(dadosBaseRadares.getId());
        dto.setCameraLat(dadosBaseRadares.getCameraLat());
        dto.setCameraLong(dadosBaseRadares.getCameraLong());
        dto.setCameraId(dadosBaseRadares.getCameraId());
        dto.setFaixaDaCamera(dadosBaseRadares.getFaixaDaCamera());
        dto.setQuantidadeDeFaixas(dadosBaseRadares.getQuantidadeDeFaixas());
        dto.setDataHora(dadosBaseRadares.getDataHora());
        dto.setTipoVeiculo(dadosBaseRadares.getTipoVeiculo());
        dto.setVelocidadeVeiculo(dadosBaseRadares.getVelocidadeVeiculo());
        dto.setVelocidadeRegulamentada(dadosBaseRadares.getVelocidadeRegulamentada());
        dto.setEndereco(dadosBaseRadares.getEndereco());
        dto.setNumero(dadosBaseRadares.getNumero());
        dto.setCidade(dadosBaseRadares.getCidade());
        dto.setSentido(dadosBaseRadares.getSentido());
        return dto;
    }
}