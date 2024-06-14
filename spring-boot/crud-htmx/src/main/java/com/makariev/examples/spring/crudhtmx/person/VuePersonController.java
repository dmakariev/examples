package com.makariev.examples.spring.crudhtmx.person;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class VuePersonController {

    private final PersonRepository personRepository;

    @GetMapping
    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    @GetMapping("{id}")
    public Optional<Person> findById(@PathVariable("id") Long id) {
        return personRepository.findById(id);
    }

    @PostMapping
    public Person create(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @PutMapping("{id}")
    public Person updateById(@PathVariable("id") Long id, @RequestBody Person person) {
        var loaded = personRepository.findById(id).orElseThrow();
        loaded.setFirstName(person.getFirstName());
        loaded.setLastName(person.getLastName());
        loaded.setBirthYear(person.getBirthYear());
        return personRepository.save(loaded);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        personRepository.deleteById(id);
    }

    @Controller
    public static class PageController {

        @GetMapping("/person-crud-vue")
        public String loadPage() {
            return "person-crud-vue";
        }

    }
}
