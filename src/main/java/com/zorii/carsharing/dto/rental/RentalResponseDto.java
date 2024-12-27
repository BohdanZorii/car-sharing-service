package com.zorii.carsharing.dto.rental;

import com.zorii.carsharing.dto.car.CarResponseDto;
import java.time.LocalDate;
import java.util.UUID;

public record RentalResponseDto(
    UUID id,
    LocalDate rentalDate,
    LocalDate returnDate,
    LocalDate actualReturnDate,
    CarResponseDto car
) {}

