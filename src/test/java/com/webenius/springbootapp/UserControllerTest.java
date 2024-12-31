package com.webenius.springbootapp.controller;

import com.webenius.springbootapp.model.User;
import com.webenius.springbootapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void getAllUsersReturnsListOfUsers() throws Exception {
        User user1 = new User(1L, "John Doe", "john@example.com");
        User user2 = new User(2L, "Jane Doe", "jane@example.com");

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")));
    }

    @Test
    public void getUserByIdReturnsUser() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    public void createUserReturnsCreatedUser() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    public void updateUserReturnsUpdatedUser() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com");
        User updatedUser = new User(1L, "John Smith", "johnsmith@example.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Smith\",\"email\":\"johnsmith@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("John Smith")));
    }

    @Test
    public void deleteUserReturnsSuccessMessage() throws Exception {
        Mockito.doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted with id 1"));
    }
}