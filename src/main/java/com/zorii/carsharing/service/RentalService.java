package com.zorii.carsharing.service;

import com.zorii.carsharing.dto.rental.RentalRequestDto;
import com.zorii.carsharing.dto.rental.RentalResponseDto;
import com.zorii.carsharing.model.Rental;
import java.util.List;
import java.util.UUID;

public interface RentalService {

    RentalResponseDto addRental(RentalRequestDto rentalRequestDto, String userEmail);

    List<RentalResponseDto> getRentals(UUID userId, boolean isActive);

    RentalResponseDto getRental(UUID rentalId, String email);

    Rental getRentalById(UUID rentalId);

    RentalResponseDto returnRental(UUID rentalId, String userEmail);
}

