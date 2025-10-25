package data.denarius.radarius.service;

import data.denarius.radarius.dto.protocol.ProtocolRequestDTO;
import data.denarius.radarius.dto.protocol.ProtocolResponseDTO;

import java.util.List;

public interface ProtocolService {
    ProtocolResponseDTO create(ProtocolRequestDTO dto);
    ProtocolResponseDTO update(Integer id, ProtocolRequestDTO dto);
    void delete(Integer id);
    ProtocolResponseDTO findById(Integer id);
    List<ProtocolResponseDTO> findAll();
    List<ProtocolResponseDTO> search(String query);
}
