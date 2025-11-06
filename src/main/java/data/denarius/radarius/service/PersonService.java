package data.denarius.radarius.service;

import data.denarius.radarius.dto.person.PersonRequestDTO;
import data.denarius.radarius.dto.person.PersonResponseDTO;
import data.denarius.radarius.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    PersonResponseDTO create(PersonRequestDTO dto);
    PersonResponseDTO update(Integer id, PersonRequestDTO dto);
    void delete(Integer id);
    PersonResponseDTO findById(Integer id);
    Page<PersonResponseDTO> findAll(Pageable pageable);
    Optional<Person> findByEmail(String email);
}
