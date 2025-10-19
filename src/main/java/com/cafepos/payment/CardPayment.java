package com.cafepos.payment;

import com.cafepos.domain.Order;
import com.cafepos.pricing.PricingService;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;

    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(Order order) {
        String masked = "****" + cardNumber.substring(cardNumber.length() - 4);
        System.out.println("[Card] Customer paid " + order.total() + " EUR with card " + masked);
    }

}
