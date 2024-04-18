package com.makariev.examples.spring.bookstore.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import static org.hamcrest.Matchers.is;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 *
 * @author dmakariev
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@Transactional
public class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Book book;

    private Author author;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        bookRepository.deleteAll();

        author = new Author();
        author.setName("John Doe");
        author = authorRepository.saveAndFlush(author);  // Ensure the author is persisted

        book = new Book();
        book.setTitle("Effective Java");
        book.setIsbn("9780134685991");
        book.setPrice(new BigDecimal("40.00"));
        book.setAuthor(author);
        book = bookRepository.saveAndFlush(book);
    }

    @Test
    void getAllBookSummaries_ShouldReturnSummaries() throws Exception {
        mockMvc.perform(get("/api/books/summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Effective Java")))
                .andExpect(jsonPath("$[0].authorName", is("John Doe")))
                .andExpect(jsonPath("$[0].price", is(40.0)));
    }

    @Test
    void getBookById_ShouldReturnBook() throws Exception {
        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.isbn", is(book.getIsbn())));
    }

    @Test
    void createBook_ShouldCreateAndReturnBook() throws Exception {
        Book newBook = new Book();
        newBook.setTitle("Spring in Action");
        newBook.setIsbn("9780326204260");
        newBook.setPrice(new BigDecimal("45.00"));
        newBook.setAuthor(author); // reuse existing author

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Spring in Action")))
                .andExpect(jsonPath("$.isbn", is("9780326204260")));
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBook() throws Exception {
        book.setTitle("Effective Java 3rd Edition");

        mockMvc.perform(put("/api/books/{id}", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Effective Java 3rd Edition")));
    }

    @Test
    void deleteBook_ShouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", book.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isNotFound());
    }
}
