package com.zorii.carsharing.service.impl;

import com.stripe.model.checkout.Session;
import com.zorii.carsharing.dto.payment.PaymentRequestDto;
import com.zorii.carsharing.dto.payment.PaymentResponseDto;
import com.zorii.carsharing.dto.payment.StripeSessionCreationDto;
import com.zorii.carsharing.mapper.PaymentMapper;
import com.zorii.carsharing.model.Payment;
import com.zorii.carsharing.model.Payment.Type;
import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.repository.PaymentRepository;
import com.zorii.carsharing.service.CarService;
import com.zorii.carsharing.service.NotificationService;
import com.zorii.carsharing.service.PaymentService;
import com.zorii.carsharing.service.PriceCalculationService;
import com.zorii.carsharing.service.RentalService;
import com.zorii.carsharing.stripe.StripeService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  private final PriceCalculationService priceCalculationService;
  private final StripeService stripeService;

  private final NotificationService notificationService;
  private final RentalService rentalService;
  private final CarService carService;

  @Override
  public List<PaymentResponseDto> getPaymentsByUserId(UUID userId) {
    return paymentRepository.findByRentalUserId(userId).stream()
        .map(paymentMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public PaymentResponseDto createPaymentSession(PaymentRequestDto paymentRequestDto) {
    Rental rental = rentalService.getRentalById(paymentRequestDto.rentalId());
    Type paymentType = Type.valueOf(paymentRequestDto.paymentType());

    BigDecimal amountToPay = calculateAmountToPay(rental, paymentType);
    String paymentName = formatPaymentName(rental, paymentType);
    Session stripeSession = stripeService.createStripeSession(
        new StripeSessionCreationDto(amountToPay, paymentName));

    Payment payment = savePayment(amountToPay, stripeSession, rental, paymentRequestDto);
    return paymentMapper.toDto(payment);
  }

  @Transactional
  @Override
  public void processSuccessfulPayment(String sessionId) {
    Payment payment = getPaymentBySessionId(sessionId);
    payment.setStatus(Payment.Status.PAID);
    paymentRepository.save(payment);

    Long userTelegramChatId = payment.getRental().getUser().getTelegramChatId();
    if (userTelegramChatId != null) {
      String carName = carService.getCarName(payment.getRental().getCar());
      String message = String.format("Payment for %s car is successful.", carName);
      notificationService.sendNotification(message, userTelegramChatId);
    }
  }

  @Transactional
  @Override
  public void handlePaymentCancellation(String sessionId) {
    Payment payment = getPaymentBySessionId(sessionId);
    Long userTelegramChatId = payment.getRental().getUser().getTelegramChatId();
    if (userTelegramChatId != null) {
      String carName = carService.getCarName(payment.getRental().getCar());
      String message = String.format("Payment for %s car is canceled.", carName);
      notificationService.sendNotification(message, userTelegramChatId);
    }
  }

  private BigDecimal calculateAmountToPay(Rental rental, Payment.Type paymentType) {
    return switch (paymentType) {
      case PAYMENT -> priceCalculationService.calculateRentalPayment(rental);
      case FINE -> priceCalculationService.calculateFine(rental);
    };
  }

  private String formatPaymentName(Rental rental, Type paymentType) {
    return String.format("%s for rental of %s", paymentType,
        carService.getCarName(rental.getCar()));
  }

  private Payment savePayment(BigDecimal amountToPay, Session session, Rental rental,
      PaymentRequestDto requestDto) {
    Type paymentType = Type.valueOf(requestDto.paymentType());
    String sessionId = session.getId();
    URL sessionUrl;
    try {
      sessionUrl = new URL(session.getUrl());
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid session url " + session.getUrl(), e);
    }

    Payment payment = paymentMapper.toEntity(paymentType, rental, sessionUrl, sessionId,
        amountToPay);
    return paymentRepository.save(payment);
  }

  private Payment getPaymentBySessionId(String sessionId) {
    return paymentRepository.findBySessionId(sessionId)
        .orElseThrow(() -> new EntityNotFoundException("Payment not found with session id " + sessionId));
  }
}
