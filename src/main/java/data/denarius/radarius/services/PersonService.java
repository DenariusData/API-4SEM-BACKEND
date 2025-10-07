package data.denarius.radarius.services;

import data.denarius.radarius.dtos.person.PersonRequestDTO;
import data.denarius.radarius.dtos.person.PersonResponseDTO;
import data.denarius.radarius.entity.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {

    List<PersonResponseDTO> findAll();

    PersonResponseDTO findById(Integer id);

    PersonResponseDTO save(PersonRequestDTO dto);

    PersonResponseDTO update(Integer id, PersonRequestDTO dto);

    void delete(Integer id);

    Optional<Person> findByEmail(String email);
}
