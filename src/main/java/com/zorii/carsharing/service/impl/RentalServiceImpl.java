package com.zorii.carsharing.service.impl;

import com.zorii.carsharing.dto.rental.RentalRequestDto;
import com.zorii.carsharing.dto.rental.RentalResponseDto;
import com.zorii.carsharing.mapper.RentalMapper;
import com.zorii.carsharing.model.Car;
import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.repository.CarRepository;
import com.zorii.carsharing.repository.RentalRepository;
import com.zorii.carsharing.repository.UserRepository;
import com.zorii.carsharing.service.RentalService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;

    @Transactional
    @Override
    public RentalResponseDto createRental(RentalRequestDto rentalRequestDto, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Car car = carRepository.findById(rentalRequestDto.carId())
            .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        if (car.getInventory() <= 0) {
            throw new IllegalStateException("Car is not available");
        }

        car.setInventory(car.getInventory() - 1);
        Rental rental = rentalMapper.toEntity(rentalRequestDto, user, car);
        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toResponseDto(savedRental);
    }

    @Override
    public List<RentalResponseDto> getRentals(UUID userId, boolean isActive) {
        List<Rental> rentals = isActive
            ? rentalRepository.findByUserIdAndActualReturnDateIsNull(userId)
            : rentalRepository.findByUserIdAndActualReturnDateIsNotNull(userId);
        return rentals.stream()
            .map(rentalMapper::toResponseDto)
            .toList();
    }

    @Override
    public RentalResponseDto getRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new EntityNotFoundException("Rental not found"));
        return rentalMapper.toResponseDto(rental);
    }

    @Transactional
    @Override
    public RentalResponseDto returnRental(UUID rentalId, String email) {
        Rental rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new EntityNotFoundException("Rental not found"));

        if (rental.getActualReturnDate() != null) {
            throw new IllegalStateException("Rental has already been returned");
        }

        rental.setActualReturnDate(LocalDate.now());
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toResponseDto(updatedRental);
    }
}
