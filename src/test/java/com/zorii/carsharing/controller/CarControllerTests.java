package com.zorii.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorii.carsharing.dto.car.CarRequestDto;
import com.zorii.carsharing.dto.car.CarResponseDto;
import java.math.BigDecimal;
import java.util.List;
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
public class CarControllerTests {

  private static MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeAll
  static void beforeAll(
      @Autowired WebApplicationContext applicationContext
  ) {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(applicationContext)
        .apply(springSecurity())
        .build();
  }

  @WithMockUser(username = "manager", roles = {"MANAGER"})
  @Test
  @DisplayName("Add a new car")
  @Sql(scripts = "classpath:database/delete-all-cars.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void addCar_ValidRequest_ReturnsCreatedCar() throws Exception {
    CarRequestDto requestDto = new CarRequestDto(
        "Model S",
        "Tesla",
        "SEDAN",
        10,
        BigDecimal.valueOf(199.99)
    );
    String jsonRequest = objectMapper.writeValueAsString(requestDto);

    MvcResult result = mockMvc.perform(post("/cars")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andReturn();

    CarResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
        CarResponseDto.class);
    assertNotNull(actual);
    assertNotNull(actual.id());
    assertEquals(requestDto.model(), actual.model());
  }

  @Test
  @DisplayName("Get all cars")
  @Sql(scripts = "classpath:database/insert-multiple-cars.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "classpath:database/delete-all-cars.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void getAllCars_ReturnsListOfCars() throws Exception {
    MvcResult result = mockMvc.perform(get("/cars")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    List<CarResponseDto> cars = objectMapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<>() {
        });
    assertNotNull(cars);
    assertFalse(cars.isEmpty());
    assertEquals(3, cars.size());
  }

  @Test
  @DisplayName("Get car by ID")
  @Sql(scripts = "classpath:database/insert-car-with-id-1.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "classpath:database/delete-all-cars.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void getCarById_ExistingId_ReturnsCar() throws Exception {
    MvcResult result = mockMvc.perform(get("/cars/{id}", "1e2f3d4c-5678-4abc-90de-f1234567890a")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    CarResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
        CarResponseDto.class);
    assertNotNull(actual);
    assertEquals("1e2f3d4c-5678-4abc-90de-f1234567890a", actual.id().toString());
  }

  @WithMockUser(username = "manager", roles = {"MANAGER"})
  @Test
  @DisplayName("Update car by ID")
  @Sql(scripts = "classpath:database/insert-car-with-id-1.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "classpath:database/delete-all-cars.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void updateCar_ValidRequest_ReturnsUpdatedCar() throws Exception {
    CarRequestDto requestDto = new CarRequestDto(
        "Updated Model",
        "Updated Brand",
        "SUV",
        15,
        BigDecimal.valueOf(299.99)
    );
    String jsonRequest = objectMapper.writeValueAsString(requestDto);

    MvcResult result = mockMvc.perform(put("/cars/{id}", "1e2f3d4c-5678-4abc-90de-f1234567890a")
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    CarResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
        CarResponseDto.class);
    assertNotNull(actual);
    assertEquals("Updated Model", actual.model());
  }

  @WithMockUser(username = "manager", roles = {"MANAGER"})
  @Test
  @DisplayName("Delete car by ID")
  @Sql(scripts = "classpath:database/insert-car-with-id-1.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "classpath:database/delete-all-cars.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void deleteCar_ExistingId_ReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/cars/{id}", "1e2f3d4c-5678-4abc-90de-f1234567890a")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }
}
