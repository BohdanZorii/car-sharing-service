package com.zorii.carsharing.service.impl;

import com.zorii.carsharing.dto.car.CarRequestDto;
import com.zorii.carsharing.dto.car.CarResponseDto;
import com.zorii.carsharing.mapper.CarMapper;
import com.zorii.carsharing.model.Car;
import com.zorii.carsharing.repository.CarRepository;
import com.zorii.carsharing.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarResponseDto addCar(CarRequestDto carRequestDto) {
        Car car = carMapper.toEntity(carRequestDto);
        Car savedCar = carRepository.save(car);
        return carMapper.toResponseDto(savedCar);
    }

    @Override
    public List<CarResponseDto> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toResponseDto)
                .toList();
    }

    @Override
    public CarResponseDto getCarById(UUID id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));
        return carMapper.toResponseDto(car);
    }

    @Transactional
    @Override
    public CarResponseDto updateCar(UUID id, CarRequestDto carRequestDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));
        
        Car updatedCar = carMapper.toEntity(carRequestDto);
        updatedCar.setId(car.getId());
        updatedCar = carRepository.save(updatedCar);
        return carMapper.toResponseDto(updatedCar);
    }

    @Transactional
    @Override
    public void deleteCar(UUID id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Car not found with ID: " + id);
        }
        carRepository.deleteById(id);
    }

    @Override
    public CarResponseDto updateInventory(UUID id, int inventoryChange) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));

        int newInventory = car.getInventory() + inventoryChange;
        if (newInventory < 0) {
            throw new IllegalArgumentException("Inventory cannot be negative");
        }

        car.setInventory(newInventory);
        Car updatedCar = carRepository.save(car);
        return carMapper.toResponseDto(updatedCar);
    }

    @Override
    public String getCarName(Car car) {
        return String.format("%s %s %s", car.getBrand(), car.getModel(), car.getType());
    }
}
