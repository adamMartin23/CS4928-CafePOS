package com.cafepos.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void testAddition() {
        Money m1 = Money.of(10.00);
        Money m2 = Money.of(5.25);
        Money result = m1.add(m2);
        assertEquals(Money.of(15.25), result);
    }

    @Test
    void testAdditionWithZero() {
        Money m1 = Money.of(10.00);
        Money result = m1.add(Money.zero());
        assertEquals(Money.of(10.00), result);
    }

    @Test
    void testMultiplication() {
        Money m = Money.of(7.50);
        Money result = m.multiply(3);
        assertEquals(Money.of(22.50), result);
    }

    @Test
    void testMultiplicationByZero() {
        Money m = Money.of(7.50);
        Money result = m.multiply(0);
        assertEquals(Money.zero(), result);
    }

    @Test
    void testNegativeAdditionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(-1.00));
    }

    @Test
    void testNegativeMultiplicationThrowsException() {
        Money m = Money.of(5.00);
        assertThrows(IllegalArgumentException.class, () -> m.multiply(-2));
    }
}