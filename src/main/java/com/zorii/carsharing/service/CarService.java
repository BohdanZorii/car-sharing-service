package com.zorii.carsharing.service;

import com.zorii.carsharing.dto.CarRequestDto;
import com.zorii.carsharing.dto.CarResponseDto;
import java.util.List;
import java.util.UUID;

public interface CarService {
  CarResponseDto addCar(CarRequestDto carRequestDto);

  List<CarResponseDto> getAllCars();

  CarResponseDto getCarById(UUID id);

  CarResponseDto updateCar(UUID id, CarRequestDto carRequestDto);

  void deleteCar(UUID id);

  CarResponseDto updateInventory(UUID id, int inventoryChange);
}
