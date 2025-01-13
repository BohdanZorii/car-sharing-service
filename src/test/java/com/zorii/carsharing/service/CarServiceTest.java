package com.zorii.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zorii.carsharing.dto.car.CarRequestDto;
import com.zorii.carsharing.dto.car.CarResponseDto;
import com.zorii.carsharing.mapper.CarMapper;
import com.zorii.carsharing.model.Car;
import com.zorii.carsharing.repository.CarRepository;
import com.zorii.carsharing.service.impl.CarServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;
    private CarRequestDto carRequestDto;
    private CarResponseDto carResponseDto;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(UUID.randomUUID());
        car.setModel("Model S");
        car.setBrand("Tesla");
        car.setType(Car.Type.SEDAN);
        car.setInventory(10);
        car.setDailyFee(new BigDecimal("99.99"));

        carRequestDto = new CarRequestDto(
                "Model S",
                "Tesla",
                "SEDAN",
                10,
                new BigDecimal("99.99")
        );

        carResponseDto = new CarResponseDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType().toString(),
                car.getInventory(),
                car.getDailyFee()
        );
    }

    @Test
    @DisplayName("Add car")
    void addCar_ValidRequest_ReturnsResponseDto() {
        when(carMapper.toEntity(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponseDto(car)).thenReturn(carResponseDto);

        CarResponseDto actual = carService.addCar(carRequestDto);

        assertEquals(carResponseDto, actual);
        verify(carMapper, times(1)).toEntity(carRequestDto);
        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toResponseDto(car);
    }

    @Test
    @DisplayName("Get all cars")
    void getAllCars_ReturnsListOfResponseDtos() {
        when(carRepository.findAll()).thenReturn(List.of(car));
        when(carMapper.toResponseDtoList(List.of(car))).thenReturn(List.of(carResponseDto));

        List<CarResponseDto> actual = carService.getAllCars();

        assertEquals(1, actual.size());
        assertEquals(carResponseDto, actual.get(0));
        verify(carRepository, times(1)).findAll();
        verify(carMapper, times(1)).toResponseDtoList(List.of(car));
    }

    @Test
    @DisplayName("Get car by existing ID")
    void getCarById_ExistingId_ReturnsResponseDto() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carMapper.toResponseDto(car)).thenReturn(carResponseDto);

        CarResponseDto actual = carService.getCarById(car.getId());

        assertEquals(carResponseDto, actual);
        verify(carRepository, times(1)).findById(car.getId());
        verify(carMapper, times(1)).toResponseDto(car);
    }

    @Test
    @DisplayName("Get car by non-existing ID")
    void getCarById_NonExistingId_ThrowsException() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> carService.getCarById(car.getId()));

        assertEquals("Car not found with ID: " + car.getId(), ex.getMessage());
        verify(carRepository, times(1)).findById(car.getId());
    }

    @Test
    @DisplayName("Update car by existing ID")
    void updateCar_ExistingId_ReturnsUpdatedResponseDto() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carMapper.toEntity(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponseDto(car)).thenReturn(carResponseDto);

        CarResponseDto actual = carService.updateCar(car.getId(), carRequestDto);

        assertEquals(carResponseDto, actual);
        verify(carRepository, times(1)).findById(car.getId());
        verify(carMapper, times(1)).toEntity(carRequestDto);
        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toResponseDto(car);
    }

    @Test
    @DisplayName("Delete car by existing ID")
    void deleteCar_ExistingId_CallsRepositoryDelete() {
        when(carRepository.existsById(car.getId())).thenReturn(true);
        doNothing().when(carRepository).deleteById(car.getId());

        carService.deleteCar(car.getId());

        verify(carRepository, times(1)).existsById(car.getId());
        verify(carRepository, times(1)).deleteById(car.getId());
    }

    @Test
    @DisplayName("Delete car by non-existing ID")
    void deleteCar_NonExistingId_ThrowsException() {
        when(carRepository.existsById(car.getId())).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> carService.deleteCar(car.getId()));

        assertEquals("Car not found with ID: " + car.getId(), ex.getMessage());
        verify(carRepository, times(1)).existsById(car.getId());
    }

    @Test
    @DisplayName("Update car inventory with valid change")
    void updateInventory_ValidChange_ReturnsUpdatedResponseDto() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponseDto(car)).thenReturn(carResponseDto);

        CarResponseDto actual = carService.updateInventory(car.getId(), 5);

        assertEquals(carResponseDto, actual);
        verify(carRepository, times(1)).findById(car.getId());
        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toResponseDto(car);
    }

    @Test
    @DisplayName("Update car inventory with invalid change")
    void updateInventory_InvalidChange_ThrowsException() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> carService.updateInventory(car.getId(), -15));

        assertEquals("Inventory cannot be negative", ex.getMessage());
        verify(carRepository, times(1)).findById(car.getId());
    }

    @Test
    @DisplayName("Get car name")
    void getCarName_ValidCar_ReturnsFormattedName() {
        String actual = carService.getCarName(car);

        assertEquals("Tesla Model S SEDAN", actual);
    }
}
