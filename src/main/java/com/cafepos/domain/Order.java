package com.cafepos.domain;

import com.cafepos.Observer.OrderObserver;
import com.cafepos.Observer.OrderPublisher;
import com.cafepos.common.Money;
import com.cafepos.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    private Money total;

    public Order(long id) { this.id = id; }

    @Override
    public void register(OrderObserver o) {
        if (o != null && !observers.contains(o)){
            observers.add(o);
        }
    }

    @Override
    public void unregister(OrderObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver observer : observers){
            observer.updated(this, eventType);
        }
    }

    public void addItem(LineItem li) {
        Objects.requireNonNull(li, "LineItem cannot be null");
        if(li.quantity() <= 0)
        {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        items.add(li);

        notifyObservers(this, "itemAdded");
    }

    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal).reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0)
        {
            throw new IllegalArgumentException("Tax percent cannot be negative");
        }

        BigDecimal rate = BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100));
        BigDecimal taxAmount = subtotal().getAmount().multiply(rate);
        double tax = taxAmount.doubleValue(); // Convert BigDecimal to double

        return Money.of(tax);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy required");
        }
        strategy.pay(this);

        notifyObservers(this, "paid");
    }

    public void markReady() {
        notifyObservers(this, "ready");
    }

    public long id(){
        return id;
    }

    public List<LineItem> items(){
        return items;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public Money total() {
        return (total != null) ? total : totalWithTax(10);
    }
}
