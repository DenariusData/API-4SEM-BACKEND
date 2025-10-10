package data.denarius.radarius.service;

import data.denarius.radarius.dto.person.PersonRequestDTO;
import data.denarius.radarius.dto.person.PersonResponseDTO;

import java.util.List;

public interface PersonService {
    PersonResponseDTO create(PersonRequestDTO dto);
    PersonResponseDTO update(Integer id, PersonRequestDTO dto);
    void delete(Integer id);
    PersonResponseDTO findById(Integer id);
    List<PersonResponseDTO> findAll();
}
