package com.makariev.examples.spring.bookstore.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author dmakariev
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void findAllUsers_ShouldReturnAllUsers() {
        // Arrange
        final User user1 = new User(1L, "user1", "password1", "user1@example.com");
        final User user2 = new User(2L, "user2", "password2", "user2@example.com");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        final List<User> users = userService.findAllUsers();

        // Assert
        assertThat(users).hasSize(2);
        assertThat(users.get(0)).isEqualTo(user1);
        assertThat(users.get(1)).isEqualTo(user2);
    }

    @Test
    public void findUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        final Long userId = 1L;
        final User user = new User(userId, "user1", "password1", "user1@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        final Optional<User> foundUser = userService.findUserById(userId);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void findUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        final Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        final Optional<User> foundUser = userService.findUserById(userId);

        // Assert
        assertThat(foundUser).isNotPresent();
    }

    @Test
    public void saveUser_ShouldReturnSavedUser() {
        // Arrange
        final User user = new User(null, "newUser", "newPass", "newuser@example.com");
        final User savedUser = new User(1L, "newUser", "newPass", "newuser@example.com");
        when(userRepository.save(user)).thenReturn(savedUser);

        // Act
        final User result = userService.saveUser(user);

        // Assert
        assertThat(result).isEqualTo(savedUser);
    }

    @Test
    public void deleteUser_ShouldInvokeDeleteById() {
        // Arrange
        final Long userId = 1L;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void findCustomerById_WhenCustomerExists_ShouldReturnCustomer() {
        // Arrange
        final Long customerId = 1L;
        final Customer customer = new Customer();
        customer.setId(customerId);
        customer.setUsername("customer1");
        customer.setEmail("customer1@example.com");
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        final Optional<Customer> foundCustomer = userService.findCustomerById(customerId);

        // Assert
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get()).isEqualTo(customer);
    }

    @Test
    public void findCustomerById_WhenNoCustomer_ShouldReturnEmpty() {
        // Arrange
        final Long customerId = 2L;
        final User user = new User(customerId, "user1", "password1", "user1@example.com");
        when(userRepository.findById(customerId)).thenReturn(Optional.of(user));

        // Act
        final Optional<Customer> foundCustomer = userService.findCustomerById(customerId);

        // Assert
        assertThat(foundCustomer).isEmpty();
    }
}
