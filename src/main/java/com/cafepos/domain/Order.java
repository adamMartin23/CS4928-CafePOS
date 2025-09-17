package com.cafepos.domain;

import com.cafepos.common.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Order {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();

    public Order(long id) { this.id = id; }

    public void addItem(LineItem li) {
        Objects.requireNonNull(li, "LineItem cannot be null");
        if(li.quantity() <= 0)
        {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        items.add(li);
    }


    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal).reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0)
        {
            throw new IllegalArgumentException("Tax percent cannot be negative");
        }

        //BigDecimal rate = BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100));
        //double tax = subtotal().getAmount().multiply(rate);

        //return Money.of(tax);

        BigDecimal rate = BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100));
        BigDecimal taxAmount = subtotal().getAmount().multiply(rate);
        double tax = taxAmount.doubleValue(); // Convert BigDecimal to double

        return Money.of(tax);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public long id(){
        return id;
    }

    public List<LineItem> items(){
        return items;
    }
}
