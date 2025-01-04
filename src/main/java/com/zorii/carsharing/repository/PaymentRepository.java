package com.zorii.carsharing.repository;

import com.zorii.carsharing.model.Payment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  Optional<Payment> findBySessionId(String sessionId);

  List<Payment> findByRentalUserId(UUID userId);
}
