package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {
    private final BigDecimal amount;

    public static Money of(double value) {
        BigDecimal bd = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
        if (bd.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        return new Money(bd);
    }

    public static Money of(BigDecimal value) { return new Money(value); }

    public static Money zero() {
        return new Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("amount required");
        this.amount = a.setScale(2, RoundingMode.HALF_UP);
        // Ask in lab about this \/
        //if (this.amount.compareTo(BigDecimal.ZERO) < 0){
        //   throw new IllegalArgumentException("Amount cannot be negative");
        //}
    }

    public Money add(Money other)
    {
        Objects.requireNonNull(other, "Other money must not be null");
        return new Money(this.amount.add(other.getAmount()));
    }

    public Money multiply(int qty)
    {
        if (qty < 0)
        {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)));
    }

    public BigDecimal asBigDecimal() { return amount; }

    public BigDecimal getAmount()
    {
        return amount;
    }

    @Override
    public int compareTo(Money other)
    {
        return this.amount.compareTo(other.getAmount());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Money))
        {
            return false;
        }
        Money money = (Money) o;

        return amount.equals(money.getAmount());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(amount);
    }

    @Override
    public String toString()
    {
        return amount.toPlainString();
    }

// equals, hashCode, toString, etc.
}
