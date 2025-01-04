package com.zorii.carsharing.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.stripe.model.checkout.Session;
import com.zorii.carsharing.dto.payment.PaymentRequestDto;
import com.zorii.carsharing.dto.payment.PaymentResponseDto;
import com.zorii.carsharing.mapper.PaymentMapper;
import com.zorii.carsharing.model.Payment;
import com.zorii.carsharing.model.Payment.Type;
import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.repository.PaymentRepository;
import com.zorii.carsharing.service.impl.PaymentServiceImpl;
import com.zorii.carsharing.stripe.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PriceCalculationService priceCalculationService;

    @Mock
    private StripeService stripeService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RentalService rentalService;

    @Mock
    private CarService carService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID rentalId;
    private UUID userId;
    private Rental rental;
    private Payment payment;
    private PaymentRequestDto paymentRequestDto;
    private PaymentResponseDto paymentResponseDto;

    @BeforeEach
    void setUp() throws Exception {
        rentalId = UUID.randomUUID();
        userId = UUID.randomUUID();

        rental = new Rental();
        rental.setId(rentalId);
        rental.setUser(new User());

        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setAmountToPay(BigDecimal.valueOf(100));
        payment.setRental(rental);
        payment.setSessionId("session123");
        payment.setSessionUrl(new URL("http://example.com"));

        paymentRequestDto = new PaymentRequestDto(rentalId, "PAYMENT");

        paymentResponseDto = new PaymentResponseDto(
                payment.getId(),
                payment.getStatus(),
                payment.getType(),
                payment.getSessionUrl(),
                payment.getSessionId(),
                payment.getAmountToPay()
        );
    }

    @Test
    void getPaymentsByUserId_ShouldReturnPayments() {
        when(paymentRepository.findByRentalUserId(userId)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> payments = paymentService.getPaymentsByUserId(userId);

        assertEquals(1, payments.size());
        assertEquals(paymentResponseDto, payments.get(0));
        verify(paymentRepository).findByRentalUserId(userId);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    @Transactional
    void createPaymentSession_ShouldCreatePayment() throws Exception {
        String sessionId = payment.getSessionId();
        URL sessionUrl = payment.getSessionUrl();
        Type paymentType = payment.getType();
        BigDecimal amountToPay = BigDecimal.valueOf(100);
        when(rentalService.getRentalById(rentalId)).thenReturn(rental);
        when(priceCalculationService.calculateRentalPayment(rental)).thenReturn(amountToPay);
        Session stripeSession = mock(Session.class);
        when(stripeSession.getId()).thenReturn(sessionId);
        when(stripeSession.getUrl()).thenReturn(sessionUrl.toString());
        when(stripeService.createStripeSession(any())).thenReturn(stripeSession);
        when(paymentMapper.toEntity(paymentType, rental, sessionUrl, sessionId, amountToPay))
            .thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDto);


        PaymentResponseDto response = paymentService.createPaymentSession(paymentRequestDto);

        assertNotNull(response);
        assertEquals(paymentResponseDto, response);
        verify(rentalService).getRentalById(rentalId);
        verify(priceCalculationService).calculateRentalPayment(rental);
        verify(stripeService).createStripeSession(any());
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void processSuccessfulPayment_ShouldUpdateStatusAndNotify() {
        when(paymentRepository.findBySessionId("session123")).thenReturn(Optional.of(payment));

        paymentService.processSuccessfulPayment("session123");

        assertEquals(Payment.Status.PAID, payment.getStatus());
        verify(paymentRepository).save(payment);
    }
}
