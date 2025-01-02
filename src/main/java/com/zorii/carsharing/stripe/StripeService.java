package com.zorii.carsharing.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import com.zorii.carsharing.dto.payment.StripeSessionCreationDto;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    private static final BigDecimal CENTS_MULTIPLIER = BigDecimal.valueOf(100);
    private static final String DEFAULT_CURRENCY = "USD";
    private static final Long DEFAULT_QUANTITY = 1L;

    @Value("${stripe.secret.key}")
    private String secretKey;
    @Value("${stripe.success.url}")
    private String successUrl;
    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Session createStripeSession(StripeSessionCreationDto stripeDto) {
        if (stripeDto.paymentName() == null || stripeDto.amountToPay() == null) {
            throw new IllegalArgumentException("Invalid stripe payment data");
        }

        ProductData productData = ProductData.builder()
            .setName(stripeDto.paymentName())
            .build();

        PriceData priceData = PriceData.builder()
            .setCurrency(DEFAULT_CURRENCY)
            .setUnitAmountDecimal(convertToCents(stripeDto.amountToPay()))
            .setProductData(productData)
            .build();

        LineItem lineItem = LineItem.builder()
            .setQuantity(DEFAULT_QUANTITY)
            .setPriceData(priceData)
            .build();

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(successUrl)
            .setCancelUrl(cancelUrl)
            .addLineItem(lineItem)
            .build();

        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Can`t create stripe session", e);
        }
    }

    private BigDecimal convertToCents(BigDecimal amount) {
        return amount.multiply(CENTS_MULTIPLIER);
    }
}
