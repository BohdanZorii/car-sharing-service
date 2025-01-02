package com.zorii.carsharing.dto.payment;

import java.math.BigDecimal;

public record StripeSessionCreationDto(
    BigDecimal amountToPay,
    String paymentName
) {

}
