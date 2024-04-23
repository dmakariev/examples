package com.makariev.examples.spring.bookstore.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author dmakariev
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@Transactional
public class AuthorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        final Author author = new Author();
        author.setName("John Doe");

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void shouldRetrieveAuthor() throws Exception {
        final Author author = new Author();
        author.setName("John Doe");
        final Author savedAuthor = authorRepository.save(author);

        mockMvc.perform(get("/api/authors/{id}", savedAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void shouldRetrieveBooksByAuthorId() throws Exception {
        // Create and save an author
        final Author author = new Author();
        author.setName("George R.R. Martin");
        final Author savedAuthor = authorRepository.saveAndFlush(author);

        // Create and save books for the author
        final Book book1 = new Book();
        book1.setTitle("A Game of Thrones");
        book1.setIsbn("1234567890");
        book1.setPrice(new BigDecimal("29.99"));
        book1.setAuthor(savedAuthor);
        bookRepository.saveAndFlush(book1);

        final Book book2 = new Book();
        book2.setTitle("A Clash of Kings");
        book2.setIsbn("0987654321");
        book2.setPrice(new BigDecimal("25.99"));
        book2.setAuthor(savedAuthor);
        bookRepository.saveAndFlush(book2);

        // Perform the request
        mockMvc.perform(get("/api/authors/{id}/books", savedAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Checks if two books are returned
                .andExpect(jsonPath("$[0].title").value("A Game of Thrones"))
                .andExpect(jsonPath("$[1].title").value("A Clash of Kings"));
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        final Author author = new Author();
        author.setName("Initial Name");
        assertThat(author.getId()).isNull();
        authorRepository.save(author);
        assertThat(author.getId()).isNotNull();

        final Author updatedInfo = new Author();
        updatedInfo.setName("Updated Name");

        mockMvc.perform(put("/api/authors/{id}", author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        final Author author = new Author();
        author.setName("John Doe");
        assertThat(author.getId()).isNull();
        authorRepository.save(author);
        assertThat(author.getId()).isNotNull();

        mockMvc.perform(delete("/api/authors/{id}", author.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/authors/{id}", author.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
