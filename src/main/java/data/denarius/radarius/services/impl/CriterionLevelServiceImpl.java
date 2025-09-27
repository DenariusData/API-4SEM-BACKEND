package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.CriterionLevel;
import data.denarius.radarius.repository.CriterionLevelRepository;
import data.denarius.radarius.service.CriterionLevelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CriterionLevelServiceImpl implements CriterionLevelService {

    private final CriterionLevelRepository criterionLevelRepository;

    public CriterionLevelServiceImpl(CriterionLevelRepository criterionLevelRepository) {
        this.criterionLevelRepository = criterionLevelRepository;
    }

    @Override
    public List<CriterionLevel> findAll() {
        return criterionLevelRepository.findAll();
    }

    @Override
    public Optional<CriterionLevel> findById(Integer id) {
        return criterionLevelRepository.findById(id);
    }

    @Override
    public CriterionLevel save(CriterionLevel criterionLevel) {
        return criterionLevelRepository.save(criterionLevel);
    }

    @Override
    public CriterionLevel update(Integer id, CriterionLevel criterionLevel) {
        return criterionLevelRepository.findById(id)
                .map(existing -> {
                    existing.setCriterion(criterionLevel.getCriterion());
                    existing.setLevel(criterionLevel.getLevel());
                    existing.setCreatedAt(criterionLevel.getCreatedAt());
                    existing.setCreatedBy(criterionLevel.getCreatedBy());
                    return criterionLevelRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("CriterionLevel not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!criterionLevelRepository.existsById(id)) {
            throw new RuntimeException("CriterionLevel not found with id " + id);
        }
        criterionLevelRepository.deleteById(id);
    }
}
