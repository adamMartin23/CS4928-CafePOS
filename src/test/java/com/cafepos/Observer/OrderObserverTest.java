package com.cafepos.Observer;

import com.cafepos.common.Money;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.payment.CashPayment;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderObserverTest {

    // Fake observer that records event types
    static class FakeObserver implements OrderObserver {
        List<String> receivedEvents = new ArrayList<>();

        @Override
        public void updated(Order order, String eventType) {
            receivedEvents.add(eventType);
        }

        public boolean received(String eventType) {
            return receivedEvents.contains(eventType);
        }

        public int count(String eventType) {
            return (int) receivedEvents.stream().filter(e -> e.equals(eventType)).count();
        }
    }

    @Test
    void observers_notified_on_item_add() {
        var product = new SimpleProduct("A", "Americano", Money.of(2.00));
        var order = new Order(1001);

        var observer = new FakeObserver();
        order.register(observer);

        order.addItem(new LineItem(product, 1));

        assertTrue(observer.received("itemAdded"));
    }

    @Test
    void multiple_observers_receive_ready_event() {
        var order = new Order(1002);

        var observer1 = new FakeObserver();
        var observer2 = new FakeObserver();

        order.register(observer1);
        order.register(observer2);

        order.markReady();

        assertTrue(observer1.received("ready"));
        assertTrue(observer2.received("ready"));
    }

    @Test
    void observer_receives_paid_event() {
        var product = new SimpleProduct("B", "Latte", Money.of(3.00));
        var order = new Order(1003);
        var observer = new FakeObserver();

        order.register(observer);
        order.addItem(new LineItem(product, 1));
        order.pay(new CashPayment());

        assertTrue(observer.received("paid"));
    }

    @Test
    void observer_receives_ready_event_after_payment() {
        var order = new Order(1004);
        var observer = new FakeObserver();

        order.register(observer);
        order.pay(new CashPayment());
        order.markReady();

        assertTrue(observer.received("ready"));
    }

    @Test
    void observer_not_registered_does_not_receive_event() {
        var order = new Order(1005);
        var observer = new FakeObserver();

        // Not registered
        order.addItem(new LineItem(new SimpleProduct("C", "Mocha", Money.of(3.50)), 1));

        assertFalse(observer.received("itemAdded"));
    }

    @Test
    void observer_unregistered_does_not_receive_event() {
        var order = new Order(1006);
        var observer = new FakeObserver();

        order.register(observer);
        order.unregister(observer);
        order.addItem(new LineItem(new SimpleProduct("D", "Cappuccino", Money.of(2.75)), 1));

        assertFalse(observer.received("itemAdded"));
    }

    @Test
    void duplicate_registration_does_not_duplicate_notifications() {
        var order = new Order(1007);
        var observer = new FakeObserver();

        order.register(observer);
        order.register(observer); // Should not duplicate
        order.addItem(new LineItem(new SimpleProduct("E", "Flat White", Money.of(2.80)), 1));

        assertEquals(1, observer.count("itemAdded"));
    }

    @Test
    void multiple_events_are_received_in_order() {
        var order = new Order(1008);
        var observer = new FakeObserver();

        order.register(observer);
        order.addItem(new LineItem(new SimpleProduct("F", "Macchiato", Money.of(2.60)), 1));
        order.pay(new CashPayment());
        order.markReady();

        assertEquals(List.of("itemAdded", "paid", "ready"), observer.receivedEvents);
    }
}
