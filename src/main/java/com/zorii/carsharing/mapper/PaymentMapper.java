package com.zorii.carsharing.mapper;

import com.zorii.carsharing.dto.payment.PaymentResponseDto;
import com.zorii.carsharing.model.Payment;
import com.zorii.carsharing.model.Payment.Type;
import com.zorii.carsharing.model.Rental;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
  @Mapping(target = "rental", source = "rental")
  @Mapping(target = "id", ignore = true)
  Payment toEntity(Type type, Rental rental, URL sessionUrl, String sessionId, BigDecimal amountToPay);

  PaymentResponseDto toDto(Payment payment);

  List<PaymentResponseDto> toResponseDtoList(List<Payment> rentals);
}
