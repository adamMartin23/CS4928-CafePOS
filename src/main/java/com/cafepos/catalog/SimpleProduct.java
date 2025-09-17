package com.cafepos.catalog;

import com.cafepos.common.Money;

import java.math.BigDecimal;

public final class SimpleProduct implements Product {
    private final String id;
    private final String name;
    private final Money basePrice;

    public SimpleProduct(String id, String name, Money basePrice)
    {
        if (basePrice.getAmount().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    @Override public String id() { return id; }

    @Override public String name() { return name; }

    @Override public Money basePrice() { return basePrice; }
}
