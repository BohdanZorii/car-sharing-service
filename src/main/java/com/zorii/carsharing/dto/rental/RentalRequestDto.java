package com.zorii.carsharing.dto.rental;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record RentalRequestDto(
        @NotNull(message = "Car ID cannot be null")
        UUID carId,

        @NotNull(message = "Rental date cannot be null")
        @FutureOrPresent(message = "Rental date must be today or in the future")
        LocalDate rentalDate,

        @NotNull(message = "Return date cannot be null")
        @Future(message = "Return date must be in the future")
        LocalDate returnDate
) {}
