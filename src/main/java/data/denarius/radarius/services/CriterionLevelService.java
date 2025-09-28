package data.denarius.radarius.service;

import data.denarius.radarius.dto.CriterionLevelRequestDTO;
import data.denarius.radarius.dto.CriterionLevelResponseDTO;

import java.util.List;

public interface CriterionLevelService {

    List<CriterionLevelResponseDTO> findAll();

    CriterionLevelResponseDTO findById(Integer id);

    CriterionLevelResponseDTO save(CriterionLevelRequestDTO dto);

    CriterionLevelResponseDTO update(Integer id, CriterionLevelRequestDTO dto);

    void delete(Integer id);
}
