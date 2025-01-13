package com.zorii.carsharing.repository;

import com.zorii.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, UUID> {

    @EntityGraph(attributePaths = {"car"})
    List<Rental> findByUserIdAndActualReturnDateIsNull(UUID userId);

    @EntityGraph(attributePaths = {"car"})
    List<Rental> findByUserIdAndActualReturnDateIsNotNull(UUID userId);

    @EntityGraph(attributePaths = {"car"})
    Optional<Rental> findByIdAndUserEmail(UUID rentalId, String email);

    @EntityGraph(attributePaths = {"car", "user"})
    List<Rental> findByReturnDateBeforeAndActualReturnDateIsNull(LocalDate today);

    @EntityGraph(attributePaths = {"car", "user"})
    Optional<Rental> findById(UUID id);
}

