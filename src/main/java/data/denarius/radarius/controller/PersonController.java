package data.denarius.radarius.controller;

import data.denarius.radarius.dto.person.PersonRequestDTO;
import data.denarius.radarius.dto.person.PersonResponseDTO;
import data.denarius.radarius.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    public ResponseEntity<PersonResponseDTO> create(@RequestBody PersonRequestDTO dto) {
        return ResponseEntity.ok(personService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> update(@PathVariable Integer id, @RequestBody PersonRequestDTO dto) {
        return ResponseEntity.ok(personService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(personService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PersonResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(personService.findAll(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
