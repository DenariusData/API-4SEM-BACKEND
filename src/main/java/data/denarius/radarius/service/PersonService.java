package data.denarius.radarius.service;

import data.denarius.radarius.entity.Person;

import java.util.Optional;

public interface PersonService {
    Optional<Person> findByEmail(String email);
}
