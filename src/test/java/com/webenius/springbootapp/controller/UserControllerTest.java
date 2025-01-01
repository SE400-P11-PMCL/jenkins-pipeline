package com.webenius.springbootapp.controller;

import com.webenius.springbootapp.CrudAppApplication;
import com.webenius.springbootapp.model.User;
import com.webenius.springbootapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrudAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        User user1 = new User(1L, "John Doe", "john@example.com");
        User user2 = new User(2L, "Jane Doe", "jane@example.com");
        userRepository.saveAll(Arrays.asList(user1, user2));
    }

    @Test
    public void getAllUsersReturnsListOfUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity("/", User[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<User> users = Arrays.asList(response.getBody());
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("John Doe");
        assertThat(users.get(1).getName()).isEqualTo("Jane Doe");
    }

    @Test
    public void createUserReturnsCreatedUser() {
        User newUser = new User(null, "John Smith", "johnsmith@example.com");
        ResponseEntity<User> response = restTemplate.postForEntity("/", newUser, User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        User createdUser = response.getBody();
        assertThat(createdUser.getName()).isEqualTo("John Smith");
    }

    @Test
    public void deleteUserReturnsSuccessMessage() {
        ResponseEntity<String> response = restTemplate.exchange("/1", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User deleted with id 1");
    }
}