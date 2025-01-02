package com.zorii.carsharing.controller;

import com.zorii.carsharing.dto.payment.PaymentRequestDto;
import com.zorii.carsharing.dto.payment.PaymentResponseDto;
import com.zorii.carsharing.dto.payment.PaymentSessionResponseDto;
import com.zorii.carsharing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(summary = "Get payments for a user",
      responses = {
          @ApiResponse(responseCode = "200", description = "List of payments"),
          @ApiResponse(responseCode = "400", description = "Invalid user ID"),
          @ApiResponse(responseCode = "404", description = "User not found")
      })
  @GetMapping
  public ResponseEntity<List<PaymentResponseDto>> getPayments(@RequestParam UUID userId) {
    List<PaymentResponseDto> payments = paymentService.getPaymentsByUserId(userId);
    return ResponseEntity.ok(payments);
  }

  @Operation(summary = "Create a payment session",
      responses = {
          @ApiResponse(responseCode = "201", description = "Payment session created"),
          @ApiResponse(responseCode = "400", description = "Invalid request data")
      })
  @PostMapping
  public ResponseEntity<PaymentSessionResponseDto> createPaymentSession(
      @Valid @RequestBody PaymentRequestDto paymentRequestDto) {
    PaymentSessionResponseDto session = paymentService.createPaymentSession(paymentRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(session);
  }

  @Operation(summary = "Handle successful payments",
      responses = {
          @ApiResponse(responseCode = "200", description = "Payment successfully processed")
      })
  @GetMapping("/success")
  public ResponseEntity<String> handlePaymentSuccess(@RequestParam @NotBlank String sessionId) {
    paymentService.processSuccessfulPayment(sessionId);
    return ResponseEntity.ok("Payment successfully processed.");
  }

  @Operation(summary = "Handle payment cancellation",
      responses = {
          @ApiResponse(responseCode = "200", description = "Payment canceled message returned")
      })
  @GetMapping("/cancel")
  public ResponseEntity<String> handlePaymentCancellation(@RequestParam @NotBlank String sessionId) {
    paymentService.handlePaymentCancellation(sessionId);
    return ResponseEntity.ok("Payment was canceled. Please try again if needed.");
  }
}
