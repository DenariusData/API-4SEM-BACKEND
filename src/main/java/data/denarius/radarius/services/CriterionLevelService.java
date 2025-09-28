package data.denarius.radarius.services;

import data.denarius.radarius.dtos.criterionlevel.CriterionLevelRequestDTO;
import data.denarius.radarius.dtos.criterionlevel.CriterionLevelResponseDTO;

import java.util.List;

public interface CriterionLevelService {

    List<CriterionLevelResponseDTO> findAll();

    CriterionLevelResponseDTO findById(Integer id);

    CriterionLevelResponseDTO save(CriterionLevelRequestDTO dto);

    CriterionLevelResponseDTO update(Integer id, CriterionLevelRequestDTO dto);

    void delete(Integer id);
}
