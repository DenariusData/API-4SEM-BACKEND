package data.denarius.radarius.service;

import data.denarius.radarius.dto.road.RoadRequestDTO;
import data.denarius.radarius.dto.road.RoadResponseDTO;

import java.util.List;

public interface RoadService {
    RoadResponseDTO create(RoadRequestDTO dto);
    RoadResponseDTO update(Integer id, RoadRequestDTO dto);
    void delete(Integer id);
    RoadResponseDTO findById(Integer id);
    List<RoadResponseDTO> findAll();
}
