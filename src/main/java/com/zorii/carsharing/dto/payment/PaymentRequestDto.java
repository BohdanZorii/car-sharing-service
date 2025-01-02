package com.zorii.carsharing.dto.payment;

import com.zorii.carsharing.validation.EnumValue;
import java.util.UUID;

public record PaymentRequestDto(

    UUID rentalId,
    @EnumValue(enumValues = {"PAYMENT", "FINE"}, message = "Invalid payment type")
    String paymentType
) {
}
