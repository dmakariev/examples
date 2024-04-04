//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 22
//DEPS org.springframework.boot:spring-boot-dependencies:3.2.4@pom
//DEPS org.springframework.boot:spring-boot-starter-web
//DEPS org.springframework.boot:spring-boot-starter-data-jpa
//DEPS org.springframework.boot:spring-boot-starter-actuator
//DEPS com.h2database:h2
//DEPS org.postgresql:postgresql
//DEPS org.projectlombok:lombok
//DEPS org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0
//DEPS org.slf4j:slf4j-simple

//JAVA_OPTIONS -Dserver.port=8080
//JAVA_OPTIONS -Dspring.datasource.url=jdbc:h2:mem:person-db;MODE=PostgreSQL;
//JAVA_OPTIONS -Dspring.h2.console.enabled=true -Dspring.h2.console.settings.web-allow-others=true
//JAVA_OPTIONS -Dmanagement.endpoints.web.exposure.include=health,env,loggers
//FILES META-INF/resources/index.html=index-fetch.html

//REPOS mavencentral,sb_snapshot=https://repo.spring.io/snapshot,sb_milestone=https://repo.spring.io/milestone
package com.makariev.examples.jbang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class springbootJpaVue {

    public static void main(String[] args) {
        SpringApplication.run(springbootJpaVue.class, args);
    }

}

@Component
@RequiredArgsConstructor
class InitialRecords {

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

@Slf4j
@RestController
class HiController {

    @GetMapping("/hi")
    public String sayHi(@RequestParam(required = false, defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
class PersonController {

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

@Data
@Entity
@Table(name = "person")
@NoArgsConstructor
@AllArgsConstructor
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private int birthYear;
}

interface PersonRepository extends JpaRepository<Person, Long> {
}
