package com.webenius.springbootapp.controller;

import com.webenius.springbootapp.CrudAppApplication;
import com.webenius.springbootapp.model.User;
import com.webenius.springbootapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrudAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerITest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User(1L, "John Doe", "john@example.com");
        User user2 = new User(2L, "Jane Smith", "jane@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com");

        when(userRepository.save(any())).thenReturn(user);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUser() throws Exception {
        User existingUser = new User(1L, "John Doe", "john@example.com");
        User updatedUser = new User(1L, "Johnny", "johnny@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(updatedUser);

        mockMvc.perform(put("/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Johnny\",\"email\":\"johnny@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Johnny")))
                .andExpect(jsonPath("$.email", is("johnny@example.com")));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any());
    }

//    @Test
//    void testDeleteUser() throws Exception {
//        doNothing().when(userRepository).deleteById(1L);
//
//        mockMvc.perform(delete("/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User deleted with id 1"));
//
//        verify(userRepository, times(1)).deleteById(1L);
//    }
}
