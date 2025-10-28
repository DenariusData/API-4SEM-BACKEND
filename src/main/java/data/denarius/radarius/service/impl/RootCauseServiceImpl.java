package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.rootcause.RootCauseRequestDTO;
import data.denarius.radarius.dto.rootcause.RootCauseResponseDTO;
import data.denarius.radarius.entity.RootCause;
import data.denarius.radarius.repository.PersonRepository;
import data.denarius.radarius.repository.RootCauseRepository;
import data.denarius.radarius.service.RootCauseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RootCauseServiceImpl implements RootCauseService {

    @Autowired
    private RootCauseRepository rootCauseRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public RootCauseResponseDTO create(RootCauseRequestDTO dto) {
        RootCause rootCause = mapToEntity(dto);
        return mapToDTO(rootCauseRepository.save(rootCause));
    }

    @Override
    public RootCauseResponseDTO update(Integer id, RootCauseRequestDTO dto) {
        RootCause rootCause = rootCauseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RootCause não encontrada"));
        updateEntity(rootCause, dto);
        return mapToDTO(rootCauseRepository.save(rootCause));
    }

    @Override
    public void delete(Integer id) {
        rootCauseRepository.deleteById(id);
    }

    @Override
    public RootCauseResponseDTO findById(Integer id) {
        return rootCauseRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("RootCause não encontrada"));
    }

    @Override
    public List<RootCauseResponseDTO> findAll() {
        return rootCauseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RootCauseResponseDTO> search(String query) {
        return rootCauseRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RootCause mapToEntity(RootCauseRequestDTO dto) {
        RootCause rootCause = new RootCause();
        updateEntity(rootCause, dto);
        return rootCause;
    }

    private void updateEntity(RootCause rootCause, RootCauseRequestDTO dto) {
        rootCause.setName(dto.getName());
        rootCause.setDescription(dto.getDescription());
        rootCause.setCreatedAt(LocalDateTime.now());

        if (dto.getCreatedBy() != null)
            rootCause.setPerson(personRepository.findById(dto.getCreatedBy()).orElse(null));
    }

    private RootCauseResponseDTO mapToDTO(RootCause rootCause) {
        RootCauseResponseDTO dto = new RootCauseResponseDTO();
        dto.setId(rootCause.getId());
        dto.setName(rootCause.getName());
        dto.setDescription(rootCause.getDescription());
        dto.setCreatedAt(rootCause.getCreatedAt());
        dto.setPersonName(rootCause.getPerson() != null ? rootCause.getPerson().getName() : null);
        return dto;
    }
}
