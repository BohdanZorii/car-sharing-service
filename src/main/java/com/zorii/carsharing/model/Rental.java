package com.zorii.carsharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@ToString
public class Rental {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "Rental date cannot be null")
  @Column(nullable = false)
  private LocalDate rentalDate;

  @NotNull(message = "Return date cannot be null")
  @Column(nullable = false)
  private LocalDate returnDate;

  private LocalDate actualReturnDate;

  @NotNull(message = "Car ID cannot be null")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "car_id", nullable = false)
  private Car car;

  @NotNull(message = "User ID cannot be null")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @PrePersist
  @PreUpdate
  private void validateDates() {
    if (rentalDate.isAfter(returnDate)) {
      throw new IllegalStateException("Rental date must be before or equal to the return date.");
    }
    if (actualReturnDate != null && actualReturnDate.isBefore(rentalDate)) {
      throw new IllegalStateException("Actual return date must be on or after the rental date.");
    }
  }
}
