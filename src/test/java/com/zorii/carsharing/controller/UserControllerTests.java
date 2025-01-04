package com.zorii.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorii.carsharing.dto.user.RoleDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import com.zorii.carsharing.dto.user.UserUpdateDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

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

  @WithMockUser(username = "user@example.com", roles = {"CUSTOMER"})
  @Test
  @DisplayName("Get profile of authenticated user")
  @Sql(scripts = {"classpath:database/insert-user-with-email.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = {"classpath:database/delete-all-users.sql"},
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void getUserProfile_ReturnsProfileInfo() throws Exception {
    MvcResult result = mockMvc.perform(get("/users/me")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    UserResponseDto userProfile = objectMapper.readValue(result.getResponse().getContentAsString(),
        UserResponseDto.class);
    assertNotNull(userProfile);
    assertEquals("user@example.com", userProfile.email());
  }

  @WithMockUser(username = "user@example.com", roles = {"CUSTOMER"})
  @Test
  @DisplayName("Update profile of authenticated user")
  @Sql(scripts = {"classpath:database/insert-user-with-email.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = {"classpath:database/delete-all-users.sql"},
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void updateUserProfile_ValidRequest_UpdatesProfile() throws Exception {
    UserUpdateDto updateDto = new UserUpdateDto("UpdatedFirstName", "UpdatedLastName");
    String jsonRequest = objectMapper.writeValueAsString(updateDto);

    MvcResult result = mockMvc.perform(put("/users/me")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    UserResponseDto updatedProfile = objectMapper.readValue(
        result.getResponse().getContentAsString(), UserResponseDto.class);
    assertNotNull(updatedProfile);
    assertEquals("UpdatedFirstName", updatedProfile.firstName());
    assertEquals("UpdatedLastName", updatedProfile.lastName());
  }

  @WithMockUser(username = "manager1@example.com", roles = {"MANAGER"})
  @Test
  @DisplayName("Update user role")
  @Sql(scripts = {"classpath:database/insert-users-with-roles.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = {"classpath:database/delete-all-users.sql"},
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void updateUserRole_ValidRequest_UpdatesRole() throws Exception {
    RoleDto roleDto = new RoleDto("MANAGER");
    String jsonRequest = objectMapper.writeValueAsString(roleDto);

    MvcResult result = mockMvc.perform(
            put("/users/{id}/role", "123e4567-e89b-12d3-a456-426614174003")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    UserResponseDto updatedUser = objectMapper.readValue(result.getResponse().getContentAsString(),
        UserResponseDto.class);
    assertNotNull(updatedUser);
    assertEquals("MANAGER", updatedUser.role());
  }

  @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
  @Test
  @DisplayName("Unauthorized access to update user role")
  @Sql(scripts = {"classpath:database/insert-users-with-roles.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = {"classpath:database/delete-all-users.sql"},
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void updateUserRole_Unauthorized_ReturnsForbidden() throws Exception {
    RoleDto roleDto = new RoleDto("MANAGER");
    String jsonRequest = objectMapper.writeValueAsString(roleDto);

    mockMvc.perform(put("/users/{id}/role", "123e4567-e89b-12d3-a456-426614174003")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
