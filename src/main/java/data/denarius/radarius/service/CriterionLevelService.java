package data.denarius.radarius.service;

import data.denarius.radarius.dto.criterionlevel.CriterionLevelRequestDTO;
import data.denarius.radarius.dto.criterionlevel.CriterionLevelResponseDTO;

import java.util.List;

public interface CriterionLevelService {
    CriterionLevelResponseDTO create(CriterionLevelRequestDTO dto);
    CriterionLevelResponseDTO update(Integer id, CriterionLevelRequestDTO dto);
    void delete(Integer id);
    CriterionLevelResponseDTO findById(Integer id);
    List<CriterionLevelResponseDTO> findAll();
    List<CriterionLevelResponseDTO> findByCriterionId(Integer criterionId);
}
