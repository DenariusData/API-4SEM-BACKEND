package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.Person;
import data.denarius.radarius.services.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import data.denarius.radarius.repository.PersonRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {
    private PersonRepository personRepository;

    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }
}
