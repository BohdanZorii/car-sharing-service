package com.zorii.carsharing.service.impl;

import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.service.PriceCalculationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceCalculationServiceImpl implements PriceCalculationService {
  @Override
  public BigDecimal calculateRentalPayment(Rental rental) {
    BigDecimal dailyFee = rental.getCar().getDailyFee();

    LocalDate startDate = rental.getRentalDate();
    LocalDate endDate = rental.getActualReturnDate() != null ? rental.getActualReturnDate() : rental.getReturnDate();
    long rentalDays = calculateDaysBetween(startDate, endDate);

    return dailyFee.multiply(BigDecimal.valueOf(rentalDays));
  }

  @Override
  public BigDecimal calculateFine(Rental rental) {
    if (rental.getActualReturnDate() == null || !rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
      return BigDecimal.ZERO;
    }

    long overdueDays = calculateDaysBetween(rental.getReturnDate(), rental.getActualReturnDate());
    BigDecimal dailyFee = rental.getCar().getDailyFee();

    return dailyFee.multiply(BigDecimal.valueOf(overdueDays)).multiply(FINE_MULTIPLIER);
  }

  private long calculateDaysBetween(LocalDate startDate, LocalDate endDate) {
    return ChronoUnit.DAYS.between(startDate, endDate);
  }
}
