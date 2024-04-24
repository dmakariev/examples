package com.makariev.examples.spring.bookstore.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService userService;

    private Author sampleAuthor;

    @BeforeEach
    void setUp() {
        sampleAuthor = new Author("John Doe");
        sampleAuthor.setId(1L);
    }

    @Test
    void getAllAuthors() throws Exception {
        given(userService.findAllAuthors()).willReturn(Arrays.asList(sampleAuthor));

        mockMvc.perform(get("/api/authors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(sampleAuthor.getName()));
    }

    @Test
    void getAuthorById() throws Exception {
        given(userService.findAuthorById(1L)).willReturn(Optional.of(sampleAuthor));

        mockMvc.perform(get("/api/authors/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sampleAuthor.getName()));
    }

    @Test
    void getBooksByAuthorId() throws Exception {
        // Setup sample books for the author
        final Book book1 = new Book("A Game of Thrones", "1234567890", new BigDecimal("19.99"));
        final Book book2 = new Book("A Clash of Kings", "0987654321", new BigDecimal("15.99"));
        book1.setId(1L);
        book2.setId(2L);

        final List<Book> books = Arrays.asList(book1, book2);

        final Author authorWithBooks = new Author("John Doe");
        authorWithBooks.getBooks().addAll(books);

        // Mock the userService to return these books for the given author ID
        given(userService.findAuthorById(1L)).willReturn(Optional.of(authorWithBooks));

        // Perform the GET request to fetch books by author ID
        mockMvc.perform(get("/api/authors/{id}/books", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Checks that two books are returned
                .andExpect(jsonPath("$[0].title").value("A Game of Thrones"))
                .andExpect(jsonPath("$[1].title").value("A Clash of Kings"));
    }
    
    @Test
    void getBooksByAuthorId_ShouldReturnNotFound() throws Exception {

        final Author authorWithNoBooks = new Author("John Doe");

        // Mock the userService to return empty books for the given author ID
        given(userService.findAuthorById(1L)).willReturn(Optional.of(authorWithNoBooks));

        // Perform the GET request to fetch books by author ID
        mockMvc.perform(get("/api/authors/{id}/books", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooksByNonExistingAuthorId_ShouldReturnNotFound() throws Exception {
        // Attempt to get books for a non-existing author ID
        mockMvc.perform(get("/api/books/by-author/{authorId}", Long.MAX_VALUE) // Use a very high value for authorId
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expecting HTTP 404 Not Found
    }

    @Test
    void createAuthor() throws Exception {
        given(userService.saveAuthor(any(Author.class))).willReturn(sampleAuthor);

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sampleAuthor.getName()));
    }

    @Test
    void updateAuthor() throws Exception {
        final Author updatedAuthor = new Author("Jane Doe");
        updatedAuthor.setId(1L);

        given(userService.updateAuthor(eq(1L), any(Author.class))).willReturn(updatedAuthor);

        mockMvc.perform(put("/api/authors/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void deleteAuthor() throws Exception {
        doNothing().when(userService).deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
