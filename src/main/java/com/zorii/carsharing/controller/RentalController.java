package com.zorii.carsharing.controller;

import com.zorii.carsharing.dto.rental.RentalRequestDto;
import com.zorii.carsharing.dto.rental.RentalResponseDto;
import com.zorii.carsharing.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
@Validated
public class RentalController {

    private final RentalService rentalService;

    @Operation(summary = "Add a new rental",
        responses = {
            @ApiResponse(responseCode = "201", description = "Rental created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Car or User not found"),
            @ApiResponse(responseCode = "409", description = "Car is not available")
        })
    @PostMapping
    public ResponseEntity<RentalResponseDto> addRental(
        @Valid @RequestBody RentalRequestDto rentalRequestDto,
        @AuthenticationPrincipal UserDetails userDetails) {
        RentalResponseDto rental = rentalService.addRental(rentalRequestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @Operation(summary = "Get rentals by user ID and active status",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of rentals"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
        })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<RentalResponseDto>> getRentals(
        @org.hibernate.validator.constraints.UUID @RequestParam UUID userId,
        @RequestParam(required = false) Boolean isActive) {
        List<RentalResponseDto> rentals = rentalService.getRentals(userId, isActive);
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "Get a rental by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Rental found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
        })
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponseDto> getRental(
        @org.hibernate.validator.constraints.UUID @PathVariable UUID id,
        @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        RentalResponseDto rental = rentalService.getRental(id, email);
        return ResponseEntity.ok(rental);
    }

    @Operation(summary = "Return a rental and update car inventory",
        responses = {
            @ApiResponse(responseCode = "200", description = "Rental returned and car inventory updated"),
            @ApiResponse(responseCode = "400", description = "Invalid rental ID"),
            @ApiResponse(responseCode = "404", description = "Rental or Car not found"),
            @ApiResponse(responseCode = "409", description = "Rental has already been returned")
        })
    @PostMapping("/return")
    public ResponseEntity<RentalResponseDto> returnRental(
        @org.hibernate.validator.constraints.UUID @RequestParam UUID rentalId,
        @AuthenticationPrincipal UserDetails userDetails) {
        RentalResponseDto rental = rentalService.returnRental(rentalId, userDetails.getUsername());
        return ResponseEntity.ok(rental);
    }
}

