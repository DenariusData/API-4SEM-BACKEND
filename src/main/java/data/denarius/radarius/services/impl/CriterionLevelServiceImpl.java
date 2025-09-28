package data.denarius.radarius.services.impl;

import data.denarius.radarius.dtos.CriterionLevelRequestDTO;
import data.denarius.radarius.dtos.CriterionLevelResponseDTO;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.CriterionLevel;
import data.denarius.radarius.entity.User;
import data.denarius.radarius.repositories.CriterionLevelRepository;
import data.denarius.radarius.repositories.CriterionRepository;
import data.denarius.radarius.repositories.UserRepository;
import data.denarius.radarius.services.CriterionLevelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriterionLevelServiceImpl implements CriterionLevelService {

    private final CriterionLevelRepository repository;
    private final CriterionRepository criterionRepository;
    private final UserRepository userRepository;

    public CriterionLevelServiceImpl(CriterionLevelRepository repository, CriterionRepository criterionRepository,
                                     UserRepository userRepository) {
        this.repository = repository;
        this.criterionRepository = criterionRepository;
        this.userRepository = userRepository;
    }

    private CriterionLevelResponseDTO toDTO(CriterionLevel level) {
        CriterionLevelResponseDTO dto = new CriterionLevelResponseDTO();
        dto.setCriterionLevelId(level.getCriterionLevelId());
        dto.setCriterionId(level.getCriterion() != null ? level.getCriterion().getCriterionId() : null);
        dto.setLevel(level.getLevel());
        dto.setCreatedById(level.getCreatedBy() != null ? level.getCreatedBy().getUserId() : null);
        dto.setCreatedAt(level.getCreatedAt());
        return dto;
    }

    private void mapDTOToEntity(CriterionLevelRequestDTO dto, CriterionLevel entity) {
        entity.setLevel(dto.getLevel());
        entity.setCreatedAt(dto.getCreatedAt());

        if (dto.getCriterionId() != null) {
            Criterion criterion = criterionRepository.findById(dto.getCriterionId())
                    .orElseThrow(() -> new RuntimeException("Criterion not found with id " + dto.getCriterionId()));
            entity.setCriterion(criterion);
        }

        if (dto.getCreatedById() != null) {
            User user = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + dto.getCreatedById()));
            entity.setCreatedBy(user);
        }
    }

    @Override
    public List<CriterionLevelResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CriterionLevelResponseDTO findById(Integer id) {
        return repository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("CriterionLevel not found with id " + id));
    }

    @Override
    public CriterionLevelResponseDTO save(CriterionLevelRequestDTO dto) {
        CriterionLevel level = new CriterionLevel();
        mapDTOToEntity(dto, level);
        return toDTO(repository.save(level));
    }

    @Override
    public CriterionLevelResponseDTO update(Integer id, CriterionLevelRequestDTO dto) {
        CriterionLevel level = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CriterionLevel not found with id " + id));
        mapDTOToEntity(dto, level);
        return toDTO(repository.save(level));
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("CriterionLevel not found with id " + id);
        }
        repository.deleteById(id);
    }
}
