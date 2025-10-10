package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.criterionlevel.CriterionLevelRequestDTO;
import data.denarius.radarius.dto.criterionlevel.CriterionLevelResponseDTO;
import data.denarius.radarius.entity.CriterionLevel;
import data.denarius.radarius.repository.CriterionLevelRepository;
import data.denarius.radarius.repository.CriterionRepository;
import data.denarius.radarius.repository.PersonRepository;
import data.denarius.radarius.service.CriterionLevelService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriterionLevelServiceImpl implements CriterionLevelService {

    @Autowired
    private CriterionLevelRepository criterionLevelRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CriterionRepository criterionRepository;

    @Override
    public CriterionLevelResponseDTO create(CriterionLevelRequestDTO dto) {
        CriterionLevel cl = mapToEntity(dto);
        return mapToDTO(criterionLevelRepository.save(cl));
    }

    @Override
    public CriterionLevelResponseDTO update(Integer id, CriterionLevelRequestDTO dto) {
        CriterionLevel cl = criterionLevelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CriterionLevel não encontrado"));
        updateEntity(cl, dto);
        return mapToDTO(criterionLevelRepository.save(cl));
    }

    @Override
    public void delete(Integer id) {
        criterionLevelRepository.deleteById(id);
    }

    @Override
    public CriterionLevelResponseDTO findById(Integer id) {
        return criterionLevelRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("CriterionLevel não encontrado"));
    }

    @Override
    public List<CriterionLevelResponseDTO> findAll() {
        return criterionLevelRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CriterionLevel mapToEntity(CriterionLevelRequestDTO dto) {
        CriterionLevel cl = new CriterionLevel();
        updateEntity(cl, dto);
        return cl;
    }

    private void updateEntity(CriterionLevel cl, CriterionLevelRequestDTO dto) {
        cl.setLevel(dto.getLevel());
        cl.setCreatedAt(dto.getCreatedAt());

        if (dto.getCreatedById() != null)
            cl.setCreatedBy(personRepository.findById(dto.getCreatedById()).orElse(null));

        if (dto.getCriterionId() != null)
            cl.setCriterion(criterionRepository.findById(dto.getCriterionId()).orElse(null));
    }

    private CriterionLevelResponseDTO mapToDTO(CriterionLevel cl) {
        CriterionLevelResponseDTO dto = new CriterionLevelResponseDTO();
        dto.setId(cl.getId());
        dto.setLevel(cl.getLevel());
        dto.setCreatedAt(cl.getCreatedAt());
        dto.setCreatedByName(cl.getCreatedBy() != null ? cl.getCreatedBy().getName() : null);
        dto.setCriterionName(cl.getCriterion() != null ? cl.getCriterion().getName() : null);
        return dto;
    }
}
