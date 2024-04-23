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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author dmakariev
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setIsbn("1234567890123");
        book.setPrice(new BigDecimal(45.00));

        // Setup other necessary object states here
    }

    @Test
    void getAllBookSummaries_ShouldReturnBookSummaries() throws Exception {
        final BookSummary summary = new BookSummary("Effective Java", "Joshua Bloch", 45.00);
        final List<BookSummary> summaries = Arrays.asList(summary);

        given(bookService.findAllBookSummaries()).willReturn(summaries);

        mockMvc.perform(get("/api/books/summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Effective Java")))
                .andExpect(jsonPath("$[0].authorName", is("Joshua Bloch")))
                .andExpect(jsonPath("$[0].price", is(45.00)));
    }

    @Test
    void getBookById_ShouldReturnBook() throws Exception {
        given(bookService.findBookById(1L)).willReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Effective Java")))
                .andExpect(jsonPath("$.isbn", is("1234567890123")));
    }

    @Test
    void createBook_ShouldCreateAndReturnBook() throws Exception {
        given(bookService.saveBook(any(Book.class))).willReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Effective Java")))
                .andExpect(jsonPath("$.isbn", is("1234567890123")));
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBook() throws Exception {
        given(bookService.updateBook(eq(1L), any(Book.class))).willReturn(book);

        mockMvc.perform(put("/api/books/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Effective Java")))
                .andExpect(jsonPath("$.isbn", is("1234567890123")));
    }

    @Test
    void deleteBook_ShouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/{id}", 1))
                .andExpect(status().isOk());
    }
}
