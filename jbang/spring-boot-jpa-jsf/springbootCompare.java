//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 22
//DEPS org.springframework.boot:spring-boot-dependencies:3.3.0@pom
//DEPS org.springframework.boot:spring-boot-starter-web
//DEPS org.springframework.boot:spring-boot-starter-data-jpa
//DEPS org.springframework.boot:spring-boot-starter-actuator
//DEPS com.h2database:h2
//DEPS org.postgresql:postgresql
//DEPS org.projectlombok:lombok
//DEPS org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0
//DEPS org.slf4j:slf4j-simple

//DEPS org.mvnpm:simpledotcss:2.3.1
//DEPS org.webjars.npm:picocss__pico:2.0.6

//DEPS org.springframework.boot:spring-boot-starter-thymeleaf

//JAVA_OPTIONS -Dserver.port=8080
//JAVA_OPTIONS -Dspring.datasource.url=jdbc:h2:mem:person-db;MODE=PostgreSQL;
//JAVA_OPTIONS -Dspring.h2.console.enabled=true -Dspring.h2.console.settings.web-allow-others=true
//JAVA_OPTIONS -Dmanagement.endpoints.web.exposure.include=health,env,loggers

//FILES META-INF/resources/index.html=index-fetch.html


//FILES templates/persons.html=persons.html

//REPOS mavencentral,sb_snapshot=https://repo.spring.io/snapshot,sb_milestone=https://repo.spring.io/milestone


//SOURCES PersonBean.java
//SOURCES Person.java
//SOURCES PersonRepository.java
package com.makariev.examples.jbang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.application.ViewHandler;
import java.io.Serializable;


@SpringBootApplication
@ComponentScan(basePackages = "com.makariev.examples.jbang")
public class springbootCompare {

    public static void main(String[] args) {
        SpringApplication.run(springbootCompare.class, args);
    }
    

@Component
@RequiredArgsConstructor
public static class InitialRecords {

    private final PersonRepository personRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void exercise() {

        if (personRepository.count() > 0) {
            return;
        }
        List.of(
                new Person(1L, "Ada", "Lovelace", 1815),
                new Person(2L, "Niklaus", "Wirth", 1934),
                new Person(3L, "Donald", "Knuth", 1938),
                new Person(4L, "Edsger", "Dijkstra", 1930),
                new Person(5L, "Grace", "Hopper", 1906),
                new Person(6L, "John", "Backus", 1924)
        ).forEach(personRepository::save);
    }
}

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public static class PersonController {

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
}



}


////////

@Controller
@RequestMapping("/persons")
@RequiredArgsConstructor
class PersonApiController {

    private final PersonRepository personRepository;

    @GetMapping
    public String getPersonsPage(Model model) {
        // Initialize variables and load initial data
        Pageable pageable = PageRequest.of(0, 5);
        Page<Person> personPage = personRepository.findAll(pageable);

        model.addAttribute("persons", personPage.getContent());
        model.addAttribute("totalPages", personPage.getTotalPages());
        model.addAttribute("currentPage", 0);
        model.addAttribute("size", 5);

        return "persons";
    }

    @GetMapping("/htmx/list")
    public String findAll(@RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "size", defaultValue = "5") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> personPage = personRepository.findAll(pageable);

        model.addAttribute("persons", personPage.getContent());
        model.addAttribute("totalPages", personPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        return "persons :: personRows";
    }

    @GetMapping("/htmx/pagination")
    public String getPagination(@RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "5") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> personPage = personRepository.findAll(pageable);

        model.addAttribute("totalPages", personPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        return "persons :: pagination";
    }

    @GetMapping("/htmx/form")
    public String showPersonForm(@RequestParam(name = "id", required = false) Long id, @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Person person = id != null
                ? personRepository.findById(id).orElse(new Person())
                : new Person();

        model.addAttribute("person", person);
        model.addAttribute("editMode", id != null);
        model.addAttribute("currentPage", page);

        return "persons :: personForm";
    }

    @PostMapping("/htmx/create")
    public String createPerson(@ModelAttribute Person person,
                               @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        personRepository.save(person);
        return findAll(page, 5, model); // Return the list of persons for the current page
    }

    @PostMapping("/htmx/update")
    public String updatePerson(@ModelAttribute Person person,
                               @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Person existingPerson = personRepository.findById(person.getId())
                .orElseThrow();
        existingPerson.setFirstName(person.getFirstName());
        existingPerson.setLastName(person.getLastName());
        existingPerson.setBirthYear(person.getBirthYear());
        personRepository.save(existingPerson);
        return findAll(page, 5, model); // Return the list of persons for the current page
    }

    @DeleteMapping("/htmx/{id}")
    public String deletePerson(@PathVariable("id") Long id, @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        personRepository.deleteById(id);
        return findAll(page, 5, model); // Return the list of persons for the current page
    }
}


