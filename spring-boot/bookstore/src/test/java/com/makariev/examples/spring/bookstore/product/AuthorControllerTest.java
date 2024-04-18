package com.makariev.examples.spring.bookstore.product;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;
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
        Author updatedAuthor = new Author("Jane Doe");
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
