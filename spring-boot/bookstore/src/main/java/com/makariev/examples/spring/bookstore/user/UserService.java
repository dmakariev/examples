package com.makariev.examples.spring.bookstore.user;

/**
 *
 * @author dmakariev
 */
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // Method to fetch specifically customers
    public Optional<Customer> findCustomerById(Long customerId) {
        return userRepository.findById(customerId)
                .filter(Customer.class::isInstance)
                .map(Customer.class::cast);
    }
}
