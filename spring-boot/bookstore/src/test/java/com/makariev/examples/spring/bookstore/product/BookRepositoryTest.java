package com.makariev.examples.spring.bookstore.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void findAllBookSummaries_ReturnsBookSummaries() {
        // Setup
        Author author = new Author();
        author.setName("Joshua Bloch");
        entityManager.persist(author);

        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor(author);
        book.setIsbn("some-isbn-number");
        book.setPrice(new BigDecimal("45.00"));
        entityManager.persist(book);

        entityManager.flush();

        // Execute
        List<BookSummary> summaries = bookRepository.findAllBookSummaries();

        // Verify
        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).title()).isEqualTo("Effective Java");
        assertThat(summaries.get(0).authorName()).isEqualTo("Joshua Bloch");
        assertThat(summaries.get(0).price()).isEqualByComparingTo(45.00);
    }
}
