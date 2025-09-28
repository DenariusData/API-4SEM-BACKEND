package data.denarius.radarius.services.impl;

import data.denarius.radarius.dto.RoadRequestDTO;
import data.denarius.radarius.dto.RoadResponseDTO;
import data.denarius.radarius.entity.Road;
import data.denarius.radarius.repository.RoadRepository;
import data.denarius.radarius.service.RoadService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadServiceImpl implements RoadService {

    private final RoadRepository roadRepository;

    public RoadServiceImpl(RoadRepository roadRepository) {
        this.roadRepository = roadRepository;
    }

    private RoadResponseDTO toDTO(Road road) {
        RoadResponseDTO dto = new RoadResponseDTO();
        dto.setRoadId(road.getRoadId());
        dto.setAddress(road.getAddress());
        dto.setSpeedLimit(road.getSpeedLimit());
        dto.setCreatedAt(road.getCreatedAt());
        dto.setUpdatedAt(road.getUpdatedAt());
        return dto;
    }

    private void mapDTOToEntity(RoadRequestDTO dto, Road road) {
        road.setAddress(dto.getAddress());
        road.setSpeedLimit(dto.getSpeedLimit());
        road.setCreatedAt(dto.getCreatedAt());
        road.setUpdatedAt(dto.getUpdatedAt());
    }

    @Override
    public List<RoadResponseDTO> findAll() {
        return roadRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public RoadResponseDTO findById(Integer id) {
        return roadRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Road not found with id " + id));
    }

    @Override
    public RoadResponseDTO save(RoadRequestDTO dto) {
        Road road = new Road();
        mapDTOToEntity(dto, road);
        return toDTO(roadRepository.save(road));
    }

    @Override
    public RoadResponseDTO update(Integer id, RoadRequestDTO dto) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Road not found with id " + id));
        mapDTOToEntity(dto, road);
        return toDTO(roadRepository.save(road));
    }

    @Override
    public void delete(Integer id) {
        if (!roadRepository.existsById(id)) {
            throw new RuntimeException("Road not found with id " + id);
        }
        roadRepository.deleteById(id);
    }
}
