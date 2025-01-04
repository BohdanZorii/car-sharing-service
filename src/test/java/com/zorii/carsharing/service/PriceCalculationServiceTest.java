package com.zorii.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zorii.carsharing.model.Car;
import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.service.impl.PriceCalculationServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PriceCalculationServiceTest {

    private PriceCalculationService priceCalculationService;

    private Rental rental;
    private Car car;

    @BeforeEach
    void setUp() {
        priceCalculationService = new PriceCalculationServiceImpl();

        car = new Car();
        car.setDailyFee(new BigDecimal("50.00"));

        rental = new Rental();
        rental.setCar(car);
        rental.setRentalDate(LocalDate.of(2025, 1, 1));
        rental.setReturnDate(LocalDate.of(2025, 1, 10));
    }

    @Test
    @DisplayName("Calculate rental payment for a completed rental")
    void calculateRentalPayment_CompletedRental_ReturnsCorrectPayment() {
        rental.setActualReturnDate(LocalDate.of(2025, 1, 10));

        BigDecimal actual = priceCalculationService.calculateRentalPayment(rental);

        assertEquals(new BigDecimal("450.00"), actual);
    }

    @Test
    @DisplayName("Calculate rental payment for an ongoing rental")
    void calculateRentalPayment_OngoingRental_ReturnsCorrectPayment() {
        rental.setActualReturnDate(null);

        BigDecimal actual = priceCalculationService.calculateRentalPayment(rental);

        assertEquals(new BigDecimal("450.00"), actual);
    }

    @Test
    @DisplayName("Calculate fine when rental is overdue")
    void calculateFine_OverdueRental_ReturnsCorrectFine() {
        rental.setActualReturnDate(LocalDate.of(2025, 1, 12));

        BigDecimal actual = priceCalculationService.calculateFine(rental);

        assertEquals(new BigDecimal("200.00"), actual);
    }

    @Test
    @DisplayName("Calculate fine when rental is returned on time")
    void calculateFine_ReturnedOnTime_ReturnsZeroFine() {
        rental.setActualReturnDate(LocalDate.of(2025, 1, 10));

        BigDecimal actual = priceCalculationService.calculateFine(rental);

        assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    @DisplayName("Calculate fine when rental is still ongoing")
    void calculateFine_OngoingRental_ReturnsZeroFine() {
        rental.setActualReturnDate(null);

        BigDecimal actual = priceCalculationService.calculateFine(rental);

        assertEquals(BigDecimal.ZERO, actual);
    }
}
