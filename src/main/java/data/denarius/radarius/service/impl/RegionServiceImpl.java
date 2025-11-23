package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.region.RegionRequestDTO;
import data.denarius.radarius.dto.region.RegionResponseDTO;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.RegionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public RegionResponseDTO create(RegionRequestDTO dto) {
        Region region = mapToEntity(dto);
        return mapToDTO(regionRepository.save(region));
    }

    @Override
    public RegionResponseDTO update(Integer id, RegionRequestDTO dto) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Region não encontrada"));
        updateEntity(region, dto);
        return mapToDTO(regionRepository.save(region));
    }

    @Override
    public void delete(Integer id) {
        regionRepository.deleteById(id);
    }

    @Override
    public RegionResponseDTO findById(Integer id) {
        return regionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Region não encontrada"));
    }

    @Override
    public List<RegionResponseDTO> findAll() {
        return regionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Region mapToEntity(RegionRequestDTO dto) {
        Region region = new Region();
        updateEntity(region, dto);
        return region;
    }

    private void updateEntity(Region region, RegionRequestDTO dto) {
        region.setName(dto.getName());
        region.setCreatedAt(dto.getCreatedAt());
        region.setUpdatedAt(dto.getUpdatedAt());
    }

    private RegionResponseDTO mapToDTO(Region region) {
        RegionResponseDTO dto = new RegionResponseDTO();
        dto.setId(region.getId());
        dto.setName(region.getName());
        dto.setCreatedAt(region.getCreatedAt());
        dto.setUpdatedAt(region.getUpdatedAt());
        return dto;
    }
}
