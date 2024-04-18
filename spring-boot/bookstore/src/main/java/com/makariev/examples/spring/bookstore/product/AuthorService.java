package com.makariev.examples.spring.bookstore.product;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author dmakariev
 */
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> findAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    public Author updateAuthor(Long id, Author updatedAuthor) {
        return authorRepository.findById(id)
                .map(author -> {
                    author.setName(updatedAuthor.getName());
                    return authorRepository.save(author);
                })
                .orElseGet(() -> {
                    updatedAuthor.setId(id);
                    return authorRepository.save(updatedAuthor);
                });
    }
}
