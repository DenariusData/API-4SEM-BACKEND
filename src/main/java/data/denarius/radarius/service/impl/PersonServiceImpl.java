package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.person.PersonRequestDTO;
import data.denarius.radarius.dto.person.PersonResponseDTO;
import data.denarius.radarius.entity.Person;
import data.denarius.radarius.repository.PersonRepository;
import data.denarius.radarius.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public PersonResponseDTO create(PersonRequestDTO dto) {
        Person person = mapToEntity(dto);
        return mapToDTO(personRepository.save(person));
    }

    @Override
    public PersonResponseDTO update(Integer id, PersonRequestDTO dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person não encontrada"));
        updateEntity(person, dto);
        return mapToDTO(personRepository.save(person));
    }

    @Override
    public void delete(Integer id) {
        personRepository.deleteById(id);
    }

    @Override
    public PersonResponseDTO findById(Integer id) {
        return personRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Person não encontrada"));
    }

    @Override
    public List<PersonResponseDTO> findAll() {
        return personRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        return Optional.empty();
    }

    private Person mapToEntity(PersonRequestDTO dto) {
        Person person = new Person();
        updateEntity(person, dto);
        return person;
    }

    private void updateEntity(Person person, PersonRequestDTO dto) {
        person.setName(dto.getName());
        person.setWhatsapp(dto.getWhatsapp());
        person.setEmail(dto.getEmail());
        person.setPassword(dto.getPassword());
        person.setRole(dto.getRole());
        person.setCreatedAt(dto.getCreatedAt());
    }

    private PersonResponseDTO mapToDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setWhatsapp(person.getWhatsapp());
        dto.setEmail(person.getEmail());
        dto.setRole(person.getRole());
        dto.setCreatedAt(person.getCreatedAt());
        return dto;
    }
}
