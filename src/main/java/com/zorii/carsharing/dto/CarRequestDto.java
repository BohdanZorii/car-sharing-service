package com.zorii.carsharing.dto;

import com.zorii.carsharing.validation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CarRequestDto(
    @NotBlank(message = "Model cannot be blank")
    @Size(min = 1, max = 100, message = "Model must be between 1 and 100 characters")
    String model,

    @NotBlank(message = "Brand cannot be blank")
    @Size(min = 1, max = 100, message = "Brand must be between 1 and 100 characters")
    String brand,

    @EnumValue(enumValues = {"SEDAN", "SUV", "HATCHBACK", "UNIVERSAL"}, message = "Invalid car type")
    String type,

    @NotNull(message = "Inventory cannot be null")
    @Positive(message = "Inventory must be a positive number")
    int inventory,

    @NotNull(message = "Daily fee cannot be null")
    @Positive(message = "Daily fee must be a positive value")
    BigDecimal dailyFee
) {}
