package com.zorii.carsharing.service;

import com.zorii.carsharing.dto.payment.PaymentRequestDto;
import com.zorii.carsharing.dto.payment.PaymentResponseDto;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
  List<PaymentResponseDto> getPaymentsByUserId(UUID userId);

  PaymentResponseDto createPaymentSession(PaymentRequestDto paymentRequestDto);

  void processSuccessfulPayment(String sessionId);

  void handlePaymentCancellation(String sessionId);
}
