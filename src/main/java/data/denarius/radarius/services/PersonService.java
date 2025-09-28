package data.denarius.radarius.services;

import data.denarius.radarius.entity.Person;

import java.util.Optional;

public interface PersonService {
    Optional<Person> findByEmail(String email);
}
