package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.region.RegionRequestDTO;
import data.denarius.radarius.dto.region.RegionResponseDTO;
import data.denarius.radarius.entity.Person;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.PersonRepository;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.RegionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private PersonRepository personRepository;

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
        List<Integer> userRegionIds = getUserRegionIds();
        List<Region> regions;
        if (userRegionIds.isEmpty()) {
            regions = regionRepository.findAll();
        } else {
            regions = regionRepository.findAllById(userRegionIds);
        }

        return regions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private List<Integer> getUserRegionIds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return List.of();
        }

        Object principal = authentication.getPrincipal();
        Integer userId = null;

        try {
            data.denarius.radarius.security.UserPrincipal up =
                    (data.denarius.radarius.security.UserPrincipal) principal;
            userId = up.getUserId();
        } catch (ClassCastException ignored) {
        }

        Person person = null;
        if (userId != null) {
            person = personRepository.findById(userId).orElse(null);
        } else if (authentication.getName() != null) {
            person = personRepository.findByEmail(authentication.getName()).orElse(null);
        }

        if (person != null && person.getRegions() != null && !person.getRegions().isEmpty()) {
            return person.getRegions().stream()
                    .map(Region::getId)
                    .collect(Collectors.toList());
        }

        return List.of();
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
