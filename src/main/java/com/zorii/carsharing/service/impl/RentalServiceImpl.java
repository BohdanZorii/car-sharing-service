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
import com.zorii.carsharing.service.NotificationService;
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
@Transactional
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;
    private final NotificationService notificationService;

    @Override
    public RentalResponseDto addRental(RentalRequestDto rentalRequestDto, String email) {
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

        if (user.getTelegramChatId() != null) {
            String rentalCreationMessage = buildCreationMessage(car, rental);
            notificationService.sendNotification(rentalCreationMessage, user.getTelegramChatId());
        }

        return rentalMapper.toResponseDto(savedRental);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RentalResponseDto> getRentals(UUID userId, boolean isActive) {
        List<Rental> rentals = isActive
            ? rentalRepository.findByUserIdAndActualReturnDateIsNull(userId)
            : rentalRepository.findByUserIdAndActualReturnDateIsNotNull(userId);
        return rentalMapper.toResponseDtoList(rentals);
    }

    @Transactional(readOnly = true)
    @Override
    public RentalResponseDto getRental(UUID id, String email) {
        Rental rental = rentalRepository.findByIdAndUserEmail(id, email)
            .orElseThrow(() -> new EntityNotFoundException("Rental not found with id " + id));
        return rentalMapper.toResponseDto(rental);
    }

    @Transactional(readOnly = true)
    @Override
    public Rental getRentalById(UUID id) {
        return rentalRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rental not found with id " + id));
    }

    @Override
    public RentalResponseDto returnRental(UUID id, String email) {
        Rental rental = getRentalById(id);

        if (rental.getActualReturnDate() != null) {
            throw new IllegalStateException("Rental has already been returned");
        }

        rental.setActualReturnDate(LocalDate.now());
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        Rental updatedRental = rentalRepository.save(rental);

        Long userTelegramChatId = rental.getUser().getTelegramChatId();
        if (userTelegramChatId != null) {
            String rentalReturnMessage = buildReturnMessage(car, rental);
            notificationService.sendNotification(rentalReturnMessage, userTelegramChatId);
        }

        return rentalMapper.toResponseDto(updatedRental);
    }

    private String buildCreationMessage(Car car, Rental rental) {
        return String.format(
            """
                Rental Created Successfully!
                Car: %s %s
                Rental Date: %s
                Return Date: %s
                Daily Fee: $%.2f""",
            car.getBrand(), car.getModel(), rental.getRentalDate(), rental.getReturnDate(), car.getDailyFee());
    }

    private String buildReturnMessage(Car car, Rental rental) {
        return String.format(
            """
                Rental Returned Successfully!
                Car: %s %s
                Return Date: %s""",
            car.getBrand(), car.getModel(), rental.getActualReturnDate());
    }
}
