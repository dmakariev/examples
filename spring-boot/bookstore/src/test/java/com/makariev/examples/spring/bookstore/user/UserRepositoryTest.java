package com.makariev.examples.spring.bookstore.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author dmakariev
 */
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenSaveUser_thenRetrieveUser() {
        // Given
        final User user = new User("johnDoe", "securePassword", "john.doe@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        final Optional<User> found = userRepository.findById(user.getId());

        // Then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getUsername()).isEqualTo("johnDoe");
    }

    @Test
    public void whenSaveCustomer_thenRetrieveCustomer() {
        // Given
        final Customer customer = new Customer("aliceSmith", "securePassword123", "alice.smith@example.com", new Date());
        entityManager.persist(customer);
        entityManager.flush();

        // When
        final Optional<User> found = userRepository.findById(customer.getId());

        // Then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isInstanceOf(Customer.class);
        assertThat(found.get().getUsername()).isEqualTo("aliceSmith");
    }

    @Test
    public void findAllUsers_ShouldReturnAllUsersIncludingCustomers() {
        // Given
        final User user = new User("janeDoe", "password", "jane.doe@example.com");
        final Customer customer = new Customer("bobSmith", "password123", "bob.smith@example.com", new Date());
        entityManager.persist(user);
        entityManager.persist(customer);
        entityManager.flush();

        // When
        final List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername).containsExactlyInAnyOrder("janeDoe", "bobSmith");
    }

    @Test
    public void deleteUserById_ShouldRemoveUser() {
        // Given
        final User user = new User("charlie", "password", "charlie@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        userRepository.deleteById(user.getId());
        final Optional<User> found = userRepository.findById(user.getId());

        // Then
        assertThat(found).isNotPresent();
    }
}
