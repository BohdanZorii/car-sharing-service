package com.zorii.carsharing.repository;

import com.zorii.carsharing.model.Car;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, UUID> {
}
