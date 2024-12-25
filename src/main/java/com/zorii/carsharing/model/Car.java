package com.zorii.carsharing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cars")
@Getter
@Setter
public class Car {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank(message = "Model cannot be blank")
  @Size(min = 1, max = 100, message = "Model must be between 1 and 100 characters")
  private String model;

  @NotBlank(message = "Brand cannot be blank")
  @Size(min = 1, max = 100, message = "Brand must be between 1 and 100 characters")
  private String brand;

  @NotNull(message = "Type cannot be null")
  @Enumerated(EnumType.STRING)
  private Type type;

  @NotNull(message = "Inventory cannot be null")
  @Positive(message = "Inventory must be a positive number")
  private int inventory;

  @NotNull(message = "Daily fee cannot be null")
  @Positive(message = "Daily fee must be a positive value")
  private BigDecimal dailyFee;

  public enum Type {
    SEDAN,
    SUV,
    HATCHBACK,
    UNIVERSAL
  }
}
