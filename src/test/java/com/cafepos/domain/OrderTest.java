package com.cafepos.domain;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    @Test
    void order_totals() {
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var p2 = new SimpleProduct("B", "B", Money.of(3.50));
        var o = new Order(1);
        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1));
        assertEquals(Money.of(8.50), o.subtotal());
        assertEquals(Money.of(0.85), o.taxAtPercent(10));
        assertEquals(Money.of(9.35), o.totalWithTax(10));
    }

    @Test
    void addItem_null_shouldThrowException() {
        var order = new Order(1);
        assertThrows(NullPointerException.class, () -> order.addItem(null));
    }

    @Test
    void addItem_zeroQuantity_shouldThrowException() {
        var product = new SimpleProduct("A", "A", Money.of(2.50));
        var order = new Order(1);
        assertThrows(IllegalArgumentException.class, () -> order.addItem(new LineItem(product, 0)));
    }

    @Test
    void addItem_negativeQuantity_shouldThrowException() {
        var product = new SimpleProduct("A", "A", Money.of(2.50));
        var order = new Order(1);
        assertThrows(IllegalArgumentException.class, () -> order.addItem(new LineItem(product, -1)));
    }

    @Test
    void taxAtPercent_negative_shouldThrowException() {
        var product = new SimpleProduct("A", "A", Money.of(2.50));
        var order = new Order(1);
        order.addItem(new LineItem(product, 2));
        assertThrows(IllegalArgumentException.class, () -> order.taxAtPercent(-5));
    }

    @Test
    void subtotal_emptyOrder_shouldBeZero() {
        var order = new Order(1);
        assertEquals(Money.zero(), order.subtotal());
    }

    @Test
    void taxAtPercent_zero_shouldBeZero() {
        var product = new SimpleProduct("A", "A", Money.of(2.50));
        var order = new Order(1);
        order.addItem(new LineItem(product, 2));
        assertEquals(Money.zero(), order.taxAtPercent(0));
    }

    @Test
    void totalWithTax_zeroTax_shouldEqualSubtotal() {
        var product = new SimpleProduct("A", "A", Money.of(2.50));
        var order = new Order(1);
        order.addItem(new LineItem(product, 2));
        assertEquals(order.subtotal(), order.totalWithTax(0));
    }

    @Test
    void id_shouldReturnCorrectValue() {
        var order = new Order(42);
        assertEquals(42, order.id());
    }

    @Test
    void items_shouldContainAddedItems() {
        var product = new SimpleProduct("A", "A", Money.of(2.50));
        var order = new Order(1);
        var item = new LineItem(product, 1);
        order.addItem(item);
        assertTrue(order.items().contains(item));
    }
}
