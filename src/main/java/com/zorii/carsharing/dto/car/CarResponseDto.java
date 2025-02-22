package com.zorii.carsharing.dto.car;

import java.math.BigDecimal;
import java.util.UUID;

public record CarResponseDto(
    UUID id,
    String model,
    String brand,
    String type,
    int inventory,
    BigDecimal dailyFee
) {}
