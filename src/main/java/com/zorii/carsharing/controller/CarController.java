package com.zorii.carsharing.controller;

import com.zorii.carsharing.dto.car.CarRequestDto;
import com.zorii.carsharing.dto.car.CarResponseDto;
import com.zorii.carsharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@Validated
public class CarController {

  private final CarService carService;

  @Operation(summary = "Add a new car",
      responses = {
          @ApiResponse(responseCode = "201", description = "Car created"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "403", description = "Access denied")
      })
  @PreAuthorize("hasRole('MANAGER')")
  @PostMapping
  public ResponseEntity<CarResponseDto> addCar(@Valid @RequestBody CarRequestDto dto) {
    CarResponseDto createdCar = carService.addCar(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
  }

  @Operation(summary = "Get all cars",
      responses = {
          @ApiResponse(responseCode = "200", description = "List of cars")
      })
  @GetMapping
  public ResponseEntity<List<CarResponseDto>> getAllCars() {
    List<CarResponseDto> cars = carService.getAllCars();
    return ResponseEntity.ok(cars);
  }

  @Operation(summary = "Get a car by ID",
      responses = {
          @ApiResponse(responseCode = "200", description = "Car found"),
          @ApiResponse(responseCode = "404", description = "Car not found")
      })
  @GetMapping("/{id}")
  public ResponseEntity<CarResponseDto> getCarById(@PathVariable UUID id) {
    CarResponseDto car = carService.getCarById(id);
    return ResponseEntity.ok(car);
  }

  @Operation(summary = "Update a car by ID",
      responses = {
          @ApiResponse(responseCode = "200", description = "Car updated"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "403", description = "Access denied"),
          @ApiResponse(responseCode = "404", description = "Car not found")
      })
  @PreAuthorize("hasRole('MANAGER')")
  @PutMapping("/{id}")
  public ResponseEntity<CarResponseDto> updateCar(@PathVariable UUID id,
      @Valid @RequestBody CarRequestDto dto) {
    CarResponseDto updatedCar = carService.updateCar(id, dto);
    return ResponseEntity.ok(updatedCar);
  }

  @Operation(summary = "Update inventory for a car",
      responses = {
          @ApiResponse(responseCode = "200", description = "Inventory updated"),
          @ApiResponse(responseCode = "400", description = "Invalid inventory change value"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Car not found")
      })
  @PatchMapping("/{id}/inventory")
  public ResponseEntity<CarResponseDto> updateInventory(
       @PathVariable UUID id,
      @RequestParam @Min(value = 1, message = "Inventory change must be at least 1") int inventoryChange) {
    CarResponseDto updatedCar = carService.updateInventory(id, inventoryChange);
    return ResponseEntity.ok(updatedCar);
  }

  @Operation(summary = "Delete a car by ID",
      responses = {
          @ApiResponse(responseCode = "204", description = "Car deleted"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "403", description = "Access denied"),
          @ApiResponse(responseCode = "404", description = "Car not found")
      })
  @PreAuthorize("hasRole('MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCar(@PathVariable UUID id) {
    carService.deleteCar(id);
    return ResponseEntity.noContent().build();
  }
}
