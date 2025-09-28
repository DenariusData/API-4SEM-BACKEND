package data.denarius.radarius.services.impl;

import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.repository.CriterionRepository;
import data.denarius.radarius.service.CriterionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CriterionServiceImpl implements CriterionService {

    private final CriterionRepository criterionRepository;

    public CriterionServiceImpl(CriterionRepository criterionRepository) {
        this.criterionRepository = criterionRepository;
    }

    @Override
    public List<Criterion> findAll() {
        return criterionRepository.findAll();
    }

    @Override
    public Optional<Criterion> findById(Integer id) {
        return criterionRepository.findById(id);
    }

    @Override
    public Criterion save(Criterion criterion) {
        return criterionRepository.save(criterion);
    }

    @Override
    public Criterion update(Integer id, Criterion criterion) {
        return criterionRepository.findById(id)
                .map(existing -> {
                    existing.setName(criterion.getName());
                    existing.setCreatedAt(criterion.getCreatedAt());
                    existing.setCreatedBy(criterion.getCreatedBy());
                    existing.setLevels(criterion.getLevels());
                    existing.setAlerts(criterion.getAlerts());
                    return criterionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Criterion not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!criterionRepository.existsById(id)) {
            throw new RuntimeException("Criterion not found with id " + id);
        }
        criterionRepository.deleteById(id);
    }
}
