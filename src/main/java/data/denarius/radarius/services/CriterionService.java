package data.denarius.radarius.services;

import data.denarius.radarius.dtos.criterion.CriterionRequestDTO;
import data.denarius.radarius.dtos.criterion.CriterionResponseDTO;

import java.util.List;

public interface CriterionService {

    List<CriterionResponseDTO> findAll();

    CriterionResponseDTO findById(Integer id);

    CriterionResponseDTO save(CriterionRequestDTO dto);

    CriterionResponseDTO update(Integer id, CriterionRequestDTO dto);

    void delete(Integer id);
}
