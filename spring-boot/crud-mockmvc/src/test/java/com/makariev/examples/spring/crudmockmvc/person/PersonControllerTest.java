package com.makariev.examples.spring.crudmockmvc.person;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    // Test method for creating a new Person
    @Test
    void shouldCreateNewPerson() throws Exception {
        // Create a new Person instance
        final Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setBirthYear(1980);

        // Perform a POST request to create the Person
        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isCreated());

        // Verify that the Person was created in the database
        assertThat(personRepository.count()).isEqualTo(1);

        //clean the database
        personRepository.deleteAll();
    }

    // Test method for retrieving a Person by ID
    @Test
    void shouldRetrievePersonById() throws Exception {
        // Create a new Person and save it in the database
        final Person savedPerson = personRepository.save(new Person("Alice", "Smith", 1990));

        // Perform a GET request to retrieve the Person by ID
        mockMvc.perform(get("/api/persons/{id}", savedPerson.getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(savedPerson.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(savedPerson.getLastName())))
                .andExpect(jsonPath("$.birthYear", is(savedPerson.getBirthYear())));

        //clean the database
        personRepository.delete(savedPerson);
    }

    // Test method for updating a Person
    @Test
    void shouldUpdatePerson() throws Exception {
        // Create a new Person and save it in the database
        final Person savedPerson = personRepository.save(new Person("Bob", "Johnson", 1985));

        // Update the Person's information
        savedPerson.setFirstName("UpdatedFirstName");
        savedPerson.setLastName("UpdatedLastName");

        // Perform a PUT request to update the Person
        mockMvc.perform(put("/api/persons/{id}", savedPerson.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedPerson)))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify that the Person's information was updated in the database
        final Person updatedPerson = personRepository.findById(savedPerson.getId()).orElse(null);
        assertThat(updatedPerson).isNotNull();
        assertThat(updatedPerson.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(updatedPerson.getLastName()).isEqualTo("UpdatedLastName");

        //clean the database
        personRepository.delete(savedPerson);
    }

    // Test method for deleting a Person
    @Test
    void shouldDeletePerson() throws Exception {
        // Create a new Person and save it in the database
        final Person savedPerson = personRepository.save(new Person("Eve", "Williams", 2000));

        // Perform a DELETE request to delete the Person by ID
        mockMvc.perform(delete("/api/persons/{id}", savedPerson.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify that the Person was deleted from the database
        assertThat(personRepository.existsById(savedPerson.getId())).isFalse();
    }

    // Test method for retrieving a list of persons
    @Test
    void shouldRetrievePersonList() throws Exception {
        // Create a new persons and save them in the database
        List.of(
                new Person("Alice", "Smith", 1990),
                new Person("Ada", "Lovelace", 1815),
                new Person("Niklaus", "Wirth", 1934),
                new Person("Donald", "Knuth", 1938),
                new Person("Edsger", "Dijkstra", 1930),
                new Person("Grace", "Hopper", 1906),
                new Person("John", "Backus", 1924)
        ).forEach(personRepository::save);

        // Perform a GET request to retrieve list of persons
        mockMvc.perform(
                get("/api/persons")
                        .param("page", "0")
                        .param("size", "1")
        ).andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(7)))
                .andExpect(jsonPath("$.numberOfElements", is(1)));

        // Perform a GET request to retrieve list of persons, 
        // from page 3, when the page size is 1, sorted by `firstName`
        mockMvc.perform(
                get("/api/persons")
                        .param("page", "2")
                        .param("size", "1")
                        .param("sort", "firstName,asc")
        ).andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(7)))
                .andExpect(jsonPath("$.numberOfElements", is(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("Donald")));

        //clean the database
        personRepository.deleteAll();
    }
}
