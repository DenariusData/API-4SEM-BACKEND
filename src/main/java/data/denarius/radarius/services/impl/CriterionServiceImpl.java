package data.denarius.radarius.services.impl;

import data.denarius.radarius.dto.CriterionRequestDTO;
import data.denarius.radarius.dto.CriterionResponseDTO;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.User;
import data.denarius.radarius.repository.CriterionRepository;
import data.denarius.radarius.repository.UserRepository;
import data.denarius.radarius.services.CriterionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriterionServiceImpl implements CriterionService {

    private final CriterionRepository criterionRepository;
    private final UserRepository userRepository;

    public CriterionServiceImpl(CriterionRepository criterionRepository, UserRepository userRepository) {
        this.criterionRepository = criterionRepository;
        this.userRepository = userRepository;
    }

    private CriterionResponseDTO toDTO(Criterion criterion) {
        CriterionResponseDTO dto = new CriterionResponseDTO();
        dto.setCriterionId(criterion.getCriterionId());
        dto.setName(criterion.getName());
        dto.setCreatedAt(criterion.getCreatedAt());
        dto.setCreatedById(criterion.getCreatedBy() != null ? criterion.getCreatedBy().getUserId() : null);
        if (criterion.getLevels() != null)
            dto.setLevelIds(criterion.getLevels().stream().map(l -> l.getLevelId()).toList());
        if (criterion.getAlerts() != null)
            dto.setAlertIds(criterion.getAlerts().stream().map(a -> a.getAlertId()).toList());
        return dto;
    }

    private void mapRequestToEntity(CriterionRequestDTO dto, Criterion criterion) {
        criterion.setName(dto.getName());
        if (dto.getCreatedById() != null) {
            User user = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + dto.getCreatedById()));
            criterion.setCreatedBy(user);
        }
    }

    @Override
    public List<CriterionResponseDTO> findAll() {
        return criterionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CriterionResponseDTO findById(Integer id) {
        return criterionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Criterion not found with id " + id));
    }

    @Override
    public CriterionResponseDTO save(CriterionRequestDTO dto) {
        Criterion criterion = new Criterion();
        mapRequestToEntity(dto, criterion);
        return toDTO(criterionRepository.save(criterion));
    }

    @Override
    public CriterionResponseDTO update(Integer id, CriterionRequestDTO dto) {
        Criterion criterion = criterionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterion not found with id " + id));
        mapRequestToEntity(dto, criterion);
        return toDTO(criterionRepository.save(criterion));
    }

    @Override
    public void delete(Integer id) {
        if (!criterionRepository.existsById(id)) {
            throw new RuntimeException("Criterion not found with id " + id);
        }
        criterionRepository.deleteById(id);
    }
}
