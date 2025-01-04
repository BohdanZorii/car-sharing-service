package com.zorii.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorii.carsharing.dto.rental.RentalRequestDto;
import com.zorii.carsharing.dto.rental.RentalResponseDto;
import com.zorii.carsharing.repository.RentalRepository;
import java.time.LocalDate;
import java.util.UUID;
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
public class RentalControllerTests {

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Add a new rental successfully")
    @Sql(scripts = {"classpath:database/setup-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addRental_ValidRequest_ReturnsCreated() throws Exception {
        RentalRequestDto rentalRequest = new RentalRequestDto(
                UUID.fromString("b3a29d93-8e21-4d53-92f3-d7a7d3d4a13d"),
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );
        String jsonRequest = objectMapper.writeValueAsString(rentalRequest);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        RentalResponseDto response = objectMapper.readValue(result.getResponse().getContentAsString(), RentalResponseDto.class);
        assertNotNull(response.id());
        assertEquals("Camry", response.car().model());
    }

    @WithMockUser(username = "manager1@example.com", roles = {"MANAGER"})
    @Test
    @DisplayName("Get rentals by user ID and active status")
    @Sql(scripts = {"classpath:database/setup-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRentals_ValidRequest_ReturnsRentals() throws Exception {
        mockMvc.perform(get("/rentals")
                        .param("userId", "123e4567-e89b-12d3-a456-426614174000")
                        .param("isActive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].car.model").value("Camry"));
    }

    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get rental by ID successfully")
    @Sql(scripts = {"classpath:database/setup-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRental_ValidId_ReturnsRental() throws Exception {
        System.out.println(rentalRepository.existsById(
            UUID.fromString("123e4567-e89b-12d3-a456-426614174100")));
        mockMvc.perform(get("/rentals/{id}", "123e4567-e89b-12d3-a456-426614174100"))
                .andExpect(status().isOk());
    }
}
