package com.zorii.carsharing.service;

import com.zorii.carsharing.dto.car.CarResponseDto;
import com.zorii.carsharing.dto.rental.RentalRequestDto;
import com.zorii.carsharing.dto.rental.RentalResponseDto;
import com.zorii.carsharing.mapper.RentalMapper;
import com.zorii.carsharing.model.Car;
import com.zorii.carsharing.model.Car.Type;
import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.repository.CarRepository;
import com.zorii.carsharing.repository.RentalRepository;
import com.zorii.carsharing.repository.UserRepository;
import com.zorii.carsharing.service.impl.RentalServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private NotificationService notificationService;

    private User mockUser;
    private Car mockCar;
    private Rental mockRental;
    private RentalRequestDto mockRentalRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("user@example.com");
        mockUser.setTelegramChatId(123456789L);

        mockCar = new Car();
        mockCar.setId(UUID.randomUUID());
        mockCar.setModel("Model X");
        mockCar.setBrand("Brand Y");
        mockCar.setInventory(5);
        mockCar.setDailyFee(BigDecimal.valueOf(100));
        mockCar.setType(Type.SEDAN);

        mockRental = new Rental();
        mockRental.setId(UUID.randomUUID());
        mockRental.setRentalDate(LocalDate.now());
        mockRental.setReturnDate(LocalDate.now().plusDays(5));
        mockRental.setCar(mockCar);
        mockRental.setUser(mockUser);

        mockRentalRequestDto = new RentalRequestDto(
            mockCar.getId(),
            LocalDate.now(),
            LocalDate.now().plusDays(5)
        );
    }

    @Test
    void addRental_Success() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(carRepository.findById(mockCar.getId())).thenReturn(Optional.of(mockCar));
        when(rentalMapper.toEntity(mockRentalRequestDto, mockUser, mockCar)).thenReturn(mockRental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(mockRental);
        when(rentalMapper.toResponseDto(mockRental)).thenReturn(new RentalResponseDto(
            mockRental.getId(),
            mockRental.getRentalDate(),
            mockRental.getReturnDate(),
            null,
            new CarResponseDto(
                mockCar.getId(),
                mockCar.getModel(),
                mockCar.getBrand(),
                mockCar.getType().name(),
                mockCar.getInventory() - 1,
                mockCar.getDailyFee()
            )
        ));

        RentalResponseDto result = rentalService.addRental(mockRentalRequestDto, mockUser.getEmail());

        assertNotNull(result);
        assertEquals(mockRental.getId(), result.id());
        verify(notificationService, times(1)).sendNotification(anyString(), eq(mockUser.getTelegramChatId()));
    }

    @Test
    void addRental_CarNotAvailable() {
        mockCar.setInventory(0);
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(carRepository.findById(mockCar.getId())).thenReturn(Optional.of(mockCar));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            rentalService.addRental(mockRentalRequestDto, mockUser.getEmail()));

        assertEquals("Car is not available", exception.getMessage());
    }

    @Test
    void getRental_Success() {
        when(rentalRepository.findByIdAndUserEmail(mockRental.getId(), mockUser.getEmail()))
            .thenReturn(Optional.of(mockRental));
        when(rentalMapper.toResponseDto(mockRental)).thenReturn(new RentalResponseDto(
            mockRental.getId(),
            mockRental.getRentalDate(),
            mockRental.getReturnDate(),
            null,
            null
        ));

        RentalResponseDto result = rentalService.getRental(mockRental.getId(), mockUser.getEmail());

        assertNotNull(result);
        assertEquals(mockRental.getId(), result.id());
    }

    @Test
    void getRental_NotFound() {
        when(rentalRepository.findByIdAndUserEmail(mockRental.getId(), mockUser.getEmail()))
            .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
            rentalService.getRental(mockRental.getId(), mockUser.getEmail()));

        assertEquals("Rental not found with id " + mockRental.getId(), exception.getMessage());
    }

    @Test
    void returnRental_Success() {
        when(rentalRepository.findById(mockRental.getId())).thenReturn(Optional.of(mockRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(mockRental);
        when(rentalMapper.toResponseDto(mockRental)).thenReturn(new RentalResponseDto(
            mockRental.getId(),
            mockRental.getRentalDate(),
            mockRental.getReturnDate(),
            LocalDate.now(),
            null
        ));

        RentalResponseDto result = rentalService.returnRental(mockRental.getId(), mockUser.getEmail());

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.actualReturnDate());
        verify(notificationService, times(1)).sendNotification(anyString(), eq(mockUser.getTelegramChatId()));
    }

    @Test
    void returnRental_AlreadyReturned() {
        mockRental.setActualReturnDate(LocalDate.now());
        when(rentalRepository.findById(mockRental.getId())).thenReturn(Optional.of(mockRental));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            rentalService.returnRental(mockRental.getId(), mockUser.getEmail()));

        assertEquals("Rental has already been returned", exception.getMessage());
    }
}
