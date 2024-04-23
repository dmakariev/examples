package com.makariev.examples.spring.bookstore.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author dmakariev
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("johnDoe", "password123", "john.doe@example.com");
        userService.saveUser(user);
    }

    @Test
    void givenExistingUserId_whenGetUserById_thenReturnsUser() throws Exception {
        // Given
        final Long userId = user.getId();

        // When & Then
        mockMvc.perform(get("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("johnDoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void givenNonexistentUserId_whenGetUserById_thenReturnsNotFound() throws Exception {
        // Given
        final Long userId = 999L;

        // When & Then
        mockMvc.perform(get("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidUser_whenCreateUser_thenReturnsCreatedUser() throws Exception {
        // Given
        final User newUser = new User("janeDoe", "password456", "jane.doe@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("janeDoe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    @Test
    void givenValidUserIdAndUpdatedUser_whenUpdateUser_thenReturnsUpdatedUser() throws Exception {
        // Given
        final Long userId = user.getId();
        final User updatedUser = new User("johnDoeUpdated", "password123Updated", "john.doe@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("johnDoeUpdated"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void givenExistingUserId_whenDeleteUser_thenReturnsOk() throws Exception {
        // Given
        final Long userId = user.getId();

        // When & Then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
