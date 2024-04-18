package com.makariev.examples.spring.bookstore.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author dmakariev
 */
@DataJpaTest
@ActiveProfiles("test")
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void testSaveAuthor() {
        Author author = new Author("John Doe");
        author = authorRepository.save(author);

        assertThat(author.getId()).isNotNull();
        assertThat(author.getName()).isEqualTo("John Doe");
    }

    @Test
    public void testFindById() {
        Author author = new Author("Jane Doe");
        author = authorRepository.save(author);

        Optional<Author> found = authorRepository.findById(author.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Jane Doe");
    }

    @Test
    public void testFindAllAuthors() {
        Author author1 = new Author("Alice Smith");
        Author author2 = new Author("Bob Johnson");
        authorRepository.save(author1);
        authorRepository.save(author2);

        List<Author> authors = authorRepository.findAll();

        assertThat(authors).hasSize(2);
        assertThat(authors).extracting(Author::getName).containsExactlyInAnyOrder("Alice Smith", "Bob Johnson");
    }

    @Test
    public void testDeleteAuthor() {
        Author author = new Author("Charlie Brown");
        author = authorRepository.save(author);

        assertThat(authorRepository.findById(author.getId())).isPresent();

        authorRepository.deleteById(author.getId());

        assertThat(authorRepository.findById(author.getId())).isNotPresent();
    }

    @Test
    public void testSaveAuthorWithDuplicateNameShouldFail() {
        Author author1 = new Author("David Goliath");
        authorRepository.save(author1);

        Author author2 = new Author("David Goliath");

        // Expect a failure due to a unique constraint violation, if name is set to be unique in the entity
        assertThatThrownBy(() -> authorRepository.saveAndFlush(author2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
