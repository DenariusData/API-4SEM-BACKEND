package data.denarius.radarius.service;

import data.denarius.radarius.dto.rootcause.RootCauseRequestDTO;
import data.denarius.radarius.dto.rootcause.RootCauseResponseDTO;

import java.util.List;

public interface RootCauseService {
    RootCauseResponseDTO create(RootCauseRequestDTO dto);
    RootCauseResponseDTO update(Integer id, RootCauseRequestDTO dto);
    void delete(Integer id);
    RootCauseResponseDTO findById(Integer id);
    List<RootCauseResponseDTO> findAll();
}
