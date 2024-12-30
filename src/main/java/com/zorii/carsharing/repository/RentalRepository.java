package com.zorii.carsharing.repository;

import com.zorii.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, UUID> {

    List<Rental> findByUserIdAndActualReturnDateIsNull(UUID userId);

    List<Rental> findByUserIdAndActualReturnDateIsNotNull(UUID userId);

  Optional<Rental> findByIdAndUserEmail(UUID rentalId, String email);

  List<Rental> findByReturnDateBeforeAndActualReturnDateIsNull(LocalDate today);
}

