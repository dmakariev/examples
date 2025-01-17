package com.makariev.examples.spring.bookstore.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author dmakariev
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void getAllUsers_ShouldReturnAllUsers() throws Exception {
        final User user1 = new User(1L, "user1", "password1", "user1@example.com");
        final User user2 = new User(2L, "user2", "password2", "user2@example.com");
        final List<User> users = Arrays.asList(user1, user2);

        given(userService.findAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()));
    }

    @Test
    public void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        final Long userId = 1L;
        final User user = new User(userId, "user1", "password1", "user1@example.com");

        given(userService.findUserById(userId)).willReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    public void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        final Long userId = 1L;
        given(userService.findUserById(userId)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createUser_ShouldCreateUserAndReturn() throws Exception {
        final User newUser = new User(null, "newUser", "newPass", "newuser@example.com");
        final User savedUser = new User(1L, "newUser", "newPass", "newuser@example.com");

        given(userService.saveUser(newUser)).willReturn(savedUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()));
    }

    @Test
    public void updateUser_WhenUserExists_ShouldUpdateAndReturn() throws Exception {
        final Long userId = 1L;
        final User user = new User(userId, "updatedUser", "updatedPass", "updated@example.com");

        given(userService.saveUser(user)).willReturn(user);

        mockMvc.perform(put("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }

    @Test
    public void deleteUser_WhenUserExists_ShouldReturnOk() throws Exception {
        final Long userId = 1L;
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}
