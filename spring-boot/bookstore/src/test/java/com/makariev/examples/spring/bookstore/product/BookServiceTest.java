package com.makariev.examples.spring.bookstore.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

/**
 *
 * @author dmakariev
 */
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setIsbn("0123456789");
        book.setPrice(new BigDecimal("40.00"));
        // Assuming an Author and other necessary fields are set up correctly
    }

    @Test
    void findAllBookSummaries_ReturnsListOfSummaries() {
        final BookSummary summary = new BookSummary("Effective Java", "Joshua Bloch", 40.00);
        final List<BookSummary> summaries = Arrays.asList(summary);

        given(bookRepository.findAllBookSummaries()).willReturn(summaries);

        final List<BookSummary> fetchedSummaries = bookService.findAllBookSummaries();

        assertThat(fetchedSummaries).isNotNull();
        assertThat(fetchedSummaries).hasSize(1);
        assertThat(fetchedSummaries.get(0).title()).isEqualTo("Effective Java");
    }

    @Test
    void findBookById_ReturnsBook() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        final Optional<Book> foundBook = bookService.findBookById(1L);

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void saveBook_SavesAndReturnsBook() {
        given(bookRepository.save(any(Book.class))).willReturn(book);

        final Book savedBook = bookService.saveBook(book);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void deleteBook_DeletesById() {
        willDoNothing().given(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        then(bookRepository).should().deleteById(1L);
    }

    @Test
    void updateBook_UpdatesAndReturnsUpdatedBook() {
        final Book updatedBook = new Book();
        updatedBook.setTitle("Effective Java 2nd Edition");

        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        final Book result = bookService.updateBook(1L, updatedBook);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Effective Java 2nd Edition");
    }
}
