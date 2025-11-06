package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.person.PersonRequestDTO;
import data.denarius.radarius.dto.person.PersonResponseDTO;
import data.denarius.radarius.dto.region.RegionResponseDTO;
import data.denarius.radarius.entity.Person;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.PersonRepository;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public PersonResponseDTO create(PersonRequestDTO dto) {
        Person person = mapToEntity(dto);
        return mapToDTO(personRepository.save(person));
    }

    @Override
    @Transactional
    public PersonResponseDTO update(Integer id, PersonRequestDTO dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person não encontrada"));
        updateEntity(person, dto);
        return mapToDTO(personRepository.save(person));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("Person não encontrada");
        }
        personRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PersonResponseDTO findById(Integer id) {
        return personRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Person não encontrada"));
    }

    @Override
    @Transactional
    public Page<PersonResponseDTO> findAll(Pageable pageable) {
        return personRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    private Person mapToEntity(PersonRequestDTO dto) {
        Person person = new Person();
        updateEntity(person, dto);
        person.setCreatedAt(java.time.LocalDateTime.now());
        return person;
    }

    private void updateEntity(Person person, PersonRequestDTO dto) {
        person.setName(dto.getName());
        person.setWhatsapp(dto.getWhatsapp());
        person.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            person.setPassword(dto.getPassword());
        }
        person.setRole(dto.getRole());
        person.setCreatedAt(dto.getCreatedAt());if (dto.getRegions() != null && !dto.getRegions().isEmpty()) {
            person.setRegions(
                    dto.getRegions().stream()
                            .map(regionDTO -> regionRepository.findById(regionDTO.getId())
                                    .orElseThrow(() -> new EntityNotFoundException("Region não encontrada: " + regionDTO.getId())))
                            .collect(Collectors.toList())
            );
        }
        
    }

    private PersonResponseDTO mapToDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setWhatsapp(person.getWhatsapp());
        dto.setEmail(person.getEmail());
        dto.setRole(person.getRole());
        dto.setCreatedAt(person.getCreatedAt());
        if (person.getRegions() != null) {
            dto.setRegions(person.getRegions().stream()
                    .map(this::mapRegionToDTO)
                           .collect(Collectors.toList()));
        }
        return dto;
    }

    private RegionResponseDTO mapRegionToDTO(Region region) {
        RegionResponseDTO dto = new RegionResponseDTO();
        dto.setId(region.getId());
        dto.setName(region.getName());
        dto.setCreatedAt(region.getCreatedAt());
        dto.setUpdatedAt(region.getUpdatedAt());
        return dto;
    }
}
