package com.zorii.carsharing.mapper;

import com.zorii.carsharing.dto.payment.PaymentSessionResponseDto;
import com.zorii.carsharing.model.Payment;
import com.zorii.carsharing.model.Payment.Type;
import com.zorii.carsharing.model.Rental;
import java.math.BigDecimal;
import java.net.URL;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
  @Mapping(target = "rental", source = "rental")
  Payment toEntity(Type type, Rental rental, URL sessionUrl, String sessionId, BigDecimal amountToPay);

  PaymentSessionResponseDto toDto(Payment payment);
}
