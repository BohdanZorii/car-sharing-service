package com.zorii.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorii.carsharing.dto.payment.PaymentRequestDto;
import com.zorii.carsharing.dto.payment.PaymentResponseDto;
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
public class PaymentControllerTests {

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

    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get payments for a user")
    @Sql(scripts = {"classpath:database/setup-rentals.sql", "classpath:database/insert-payments.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all-payments.sql", "classpath:database/delete-all-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getPayments_ValidRequest_ReturnsPayments() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].type").value("PAYMENT"));
    }

    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Create a payment session successfully")
    @Sql(scripts = {"classpath:database/setup-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all-payments.sql", "classpath:database/delete-all-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPaymentSession_ValidRequest_ReturnsCreated() throws Exception {
        PaymentRequestDto paymentRequest = new PaymentRequestDto(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174100"),
                "PAYMENT"
        );
        String jsonRequest = objectMapper.writeValueAsString(paymentRequest);

        MvcResult result = mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto response = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentResponseDto.class);
        assertNotNull(response.id());
        assertEquals("PENDING", response.status().toString());
    }

    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Handle successful payment")
    @Sql(scripts = {"classpath:database/setup-rentals.sql", "classpath:database/insert-payments.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all-payments.sql", "classpath:database/delete-all-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handlePaymentSuccess_ValidSession_ReturnsSuccessMessage() throws Exception {
        mockMvc.perform(get("/payments/success")
                        .param("sessionId", "session-123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment successfully processed."));
    }

    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Handle payment cancellation")
    @Sql(scripts = {"classpath:database/setup-rentals.sql", "classpath:database/insert-payments.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all-payments.sql", "classpath:database/delete-all-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handlePaymentCancellation_ValidSession_ReturnsCancelMessage() throws Exception {
        mockMvc.perform(get("/payments/cancel")
                        .param("sessionId", "session-123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment was canceled. Please try again if needed."));
    }
}
