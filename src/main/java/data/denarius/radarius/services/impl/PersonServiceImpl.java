package data.denarius.radarius.services.impl;

import data.denarius.radarius.dtos.person.PersonRequestDTO;
import data.denarius.radarius.dtos.person.PersonResponseDTO;
import data.denarius.radarius.entity.Person;
import data.denarius.radarius.enums.RoleEnum;
import data.denarius.radarius.repositories.PersonRepository;
import data.denarius.radarius.services.PersonService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonServiceImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private PersonResponseDTO toDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setPersonId(person.getPersonId());
        dto.setName(person.getName());
        dto.setWhatsapp(person.getWhatsapp());
        dto.setEmail(person.getEmail());
        dto.setRole(person.getRole().name());
        dto.setCreatedAt(person.getCreatedAt());
        return dto;
    }

    private void mapDTOToEntity(PersonRequestDTO dto, Person person, boolean isUpdate) {
        person.setName(dto.getName());
        person.setWhatsapp(dto.getWhatsapp());
        person.setEmail(dto.getEmail());

        // ✅ Converter String para Enum com validação
        if (dto.getRole() != null) {
            try {
                person.setRole(RoleEnum.valueOf(dto.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + dto.getRole());
            }
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            person.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else if (!isUpdate) {
            throw new RuntimeException("Password is required");
        }
    }

    @Override
    public List<PersonResponseDTO> findAll() {
        return personRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonResponseDTO findById(Integer id) {
        return personRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Person not found with id " + id));
    }

    @Override
    public PersonResponseDTO save(PersonRequestDTO dto) {
        Person person = new Person();
        mapDTOToEntity(dto, person, false);  // false = não é update
        return toDTO(personRepository.save(person));
    }

    @Override
    public PersonResponseDTO update(Integer id, PersonRequestDTO dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id " + id));
        mapDTOToEntity(dto, person, true);  // true = é update
        return toDTO(personRepository.save(person));
    }

    @Override
    public void delete(Integer id) {
        if (!personRepository.existsById(id)) {
            throw new RuntimeException("Person not found with id " + id);
        }
        personRepository.deleteById(id);
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        return Optional.empty();
    }
}