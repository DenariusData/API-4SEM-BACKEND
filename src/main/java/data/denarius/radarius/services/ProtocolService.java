package data.denarius.radarius.services;

import data.denarius.radarius.dtos.protocol.ProtocolRequestDTO;
import data.denarius.radarius.dtos.protocol.ProtocolResponseDTO;

import java.util.List;

public interface ProtocolService {

    List<ProtocolResponseDTO> findAll();

    ProtocolResponseDTO findById(Integer id);

    ProtocolResponseDTO save(ProtocolRequestDTO dto);

    ProtocolResponseDTO update(Integer id, ProtocolRequestDTO dto);

    void delete(Integer id);
}
