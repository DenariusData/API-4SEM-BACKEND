package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.road.RoadRequestDTO;
import data.denarius.radarius.dto.road.RoadResponseDTO;
import data.denarius.radarius.entity.Road;
import data.denarius.radarius.repository.RoadRepository;
import data.denarius.radarius.service.RoadService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadServiceImpl implements RoadService {

    @Autowired
    private RoadRepository roadRepository;

    @Override
    public RoadResponseDTO create(RoadRequestDTO dto) {
        Road road = mapToEntity(dto);
        return mapToDTO(roadRepository.save(road));
    }

    @Override
    public RoadResponseDTO update(Integer id, RoadRequestDTO dto) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Road não encontrada"));
        updateEntity(road, dto);
        return mapToDTO(roadRepository.save(road));
    }

    @Override
    public void delete(Integer id) {
        roadRepository.deleteById(id);
    }

    @Override
    public RoadResponseDTO findById(Integer id) {
        return roadRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Road não encontrada"));
    }

    @Override
    public List<RoadResponseDTO> findAll() {
        return roadRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Road mapToEntity(RoadRequestDTO dto) {
        Road road = new Road();
        updateEntity(road, dto);
        return road;
    }

    private void updateEntity(Road road, RoadRequestDTO dto) {
        road.setAddress(dto.getAddress());
        road.setSpeedLimit(dto.getSpeedLimit());
        road.setCreatedAt(dto.getCreatedAt());
        road.setUpdatedAt(dto.getUpdatedAt());
    }

    private RoadResponseDTO mapToDTO(Road road) {
        RoadResponseDTO dto = new RoadResponseDTO();
        dto.setId(road.getId());
        dto.setAddress(road.getAddress());
        dto.setSpeedLimit(road.getSpeedLimit());
        dto.setCreatedAt(road.getCreatedAt());
        dto.setUpdatedAt(road.getUpdatedAt());
        return dto;
    }
}
