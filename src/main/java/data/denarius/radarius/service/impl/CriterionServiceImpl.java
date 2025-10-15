package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.criterion.CriterionRequestDTO;
import data.denarius.radarius.dto.criterion.CriterionResponseDTO;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.repository.CriterionRepository;
import data.denarius.radarius.repository.PersonRepository;
import data.denarius.radarius.service.CriterionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriterionServiceImpl implements CriterionService {

    @Autowired
    private CriterionRepository criterionRepository;
    @Autowired
    private PersonRepository personRepository;

    @Override
    public CriterionResponseDTO create(CriterionRequestDTO dto) {
        Criterion criterion = mapToEntity(dto);
        return mapToDTO(criterionRepository.save(criterion));
    }

    @Override
    public CriterionResponseDTO update(Integer id, CriterionRequestDTO dto) {
        Criterion criterion = criterionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Criterion não encontrado"));
        updateEntity(criterion, dto);
        return mapToDTO(criterionRepository.save(criterion));
    }

    @Override
    public void delete(Integer id) {
        criterionRepository.deleteById(id);
    }

    @Override
    public CriterionResponseDTO findById(Integer id) {
        return criterionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Criterion não encontrado"));
    }

    @Override
    public List<CriterionResponseDTO> findAll() {
        return criterionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CriterionResponseDTO> getCriteriaSummary() {
        return criterionRepository.findAll()
                .stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    private CriterionResponseDTO mapToSummaryDTO(Criterion criterion) {
        CriterionResponseDTO dto = new CriterionResponseDTO();
        dto.setId(criterion.getId());
        dto.setName(criterion.getName());
        dto.setDescription(criterion.getDescription());
        dto.setExample(criterion.getExample());
        dto.setMathExpression(criterion.getMathExpression());
        return dto;
    }

    private Criterion mapToEntity(CriterionRequestDTO dto) {
        Criterion criterion = new Criterion();
        updateEntity(criterion, dto);
        return criterion;
    }

    private void updateEntity(Criterion criterion, CriterionRequestDTO dto) {
        criterion.setName(dto.getName());
        criterion.setDescription(dto.getDescription());
        criterion.setExample(dto.getExample());
        criterion.setMathExpression(dto.getMathExpression());
        criterion.setCreatedAt(dto.getCreatedAt());
        if (dto.getCreatedById() != null)
            criterion.setCreatedBy(personRepository.findById(dto.getCreatedById()).orElse(null));
    }

    private CriterionResponseDTO mapToDTO(Criterion criterion) {
        CriterionResponseDTO dto = new CriterionResponseDTO();
        dto.setId(criterion.getId());
        dto.setName(criterion.getName());
        dto.setDescription(criterion.getDescription());
        dto.setExample(criterion.getExample());
        dto.setMathExpression(criterion.getMathExpression());
        dto.setCreatedAt(criterion.getCreatedAt());
        dto.setCreatedByName(criterion.getCreatedBy() != null ? criterion.getCreatedBy().getName() : null);
        return dto;
    }
}
