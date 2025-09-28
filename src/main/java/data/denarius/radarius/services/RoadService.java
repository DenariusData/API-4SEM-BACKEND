package data.denarius.radarius.services;

import data.denarius.radarius.dto.RoadRequestDTO;
import data.denarius.radarius.dto.RoadResponseDTO;

import java.util.List;

public interface RoadService {

    List<RoadResponseDTO> findAll();

    RoadResponseDTO findById(Integer id);

    RoadResponseDTO save(RoadRequestDTO dto);

    RoadResponseDTO update(Integer id, RoadRequestDTO dto);

    void delete(Integer id);
}
