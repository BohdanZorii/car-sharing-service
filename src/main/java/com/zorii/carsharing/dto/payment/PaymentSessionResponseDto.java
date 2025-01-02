package com.zorii.carsharing.dto.payment;

import com.zorii.carsharing.model.Payment;
import java.math.BigDecimal;
import java.net.URL;
import java.util.UUID;

public record PaymentSessionResponseDto(
    UUID id,
    Payment.Status status,
    Payment.Type type,
    URL sessionUrl,
    String sessionId,
    BigDecimal amountToPay
) {

}
