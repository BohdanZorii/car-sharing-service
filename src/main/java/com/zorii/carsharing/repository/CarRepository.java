package com.zorii.carsharing.repository;

import com.zorii.carsharing.model.Car;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
}
