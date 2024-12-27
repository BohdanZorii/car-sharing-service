package com.zorii.carsharing.service;

import com.zorii.carsharing.dto.rental.RentalRequestDto;
import com.zorii.carsharing.dto.rental.RentalResponseDto;
import java.util.List;
import java.util.UUID;

public interface RentalService {

    RentalResponseDto createRental(RentalRequestDto rentalRequestDto, String userEmail);

    List<RentalResponseDto> getRentals(UUID userId, boolean isActive);

    RentalResponseDto getRental(UUID rentalId);

    RentalResponseDto returnRental(UUID rentalId, String userEmail);
}

