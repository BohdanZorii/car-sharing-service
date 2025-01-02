package com.zorii.carsharing.service;

import com.zorii.carsharing.model.Rental;
import java.math.BigDecimal;

public interface PriceCalculationService {
  BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(2);

  BigDecimal calculateRentalPayment(Rental rental);

  BigDecimal calculateFine(Rental rental);
}
