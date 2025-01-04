package com.zorii.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorii.carsharing.dto.UserLoginRequestDto;
import com.zorii.carsharing.dto.user.UserLoginResponseDto;
import com.zorii.carsharing.dto.user.UserRegistrationDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTests {

  private static MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeAll
  static void beforeAll(@Autowired WebApplicationContext applicationContext) {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(applicationContext)
        .apply(springSecurity())
        .build();
  }

  @Test
  @DisplayName("Register a new user successfully")
  @Sql(scripts = {"classpath:database/delete-all-users.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void registerUser_ValidRequest_ReturnsCreated() throws Exception {
    UserRegistrationDto registrationDto = new UserRegistrationDto(
        "newuser@example.com",
        "John",
        "Doe",
        "password123"
    );
    String jsonRequest = objectMapper.writeValueAsString(registrationDto);

    MvcResult result = mockMvc.perform(post("/auth/registration")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andReturn();

    UserResponseDto response = objectMapper.readValue(result.getResponse().getContentAsString(),
        UserResponseDto.class);
    assertNotNull(response.id());
    assertEquals("newuser@example.com", response.email());
    assertEquals("CUSTOMER", response.role());
  }

  @Test
  @DisplayName("Register user with duplicate email returns conflict")
  @Sql(scripts = {"classpath:database/insert-user-with-email.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = {"classpath:database/delete-all-users.sql"},
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void registerUser_DuplicateEmail_ReturnsConflict() throws Exception {
    UserRegistrationDto registrationDto = new UserRegistrationDto(
        "user@example.com",
        "John",
        "Doe",
        "password123"
    );
    String jsonRequest = objectMapper.writeValueAsString(registrationDto);

    mockMvc.perform(post("/auth/registration")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("Login with valid credentials returns token")
  @Sql(scripts = {"classpath:database/insert-user-with-email.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = {"classpath:database/delete-all-users.sql"},
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void login_ValidCredentials_ReturnsToken() throws Exception {
    UserLoginRequestDto loginRequest = new UserLoginRequestDto(
        "user@example.com",
        "password123"
    );
    String jsonRequest = objectMapper.writeValueAsString(loginRequest);

    MvcResult result = mockMvc.perform(post("/auth/login")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    UserLoginResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(), UserLoginResponseDto.class);
    assertNotNull(response.token());
    assertFalse(response.token().isEmpty());
  }

  @Test
  @DisplayName("Login with nonexistent email returns not found")
  void login_NonexistentEmail_ReturnsNotFound() throws Exception {
    UserLoginRequestDto loginRequest = new UserLoginRequestDto(
        "nonexistent@example.com",
        "wrongpassword"
    );
    String jsonRequest = objectMapper.writeValueAsString(loginRequest);

    mockMvc.perform(post("/auth/login")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
