package com.cafepos.checkout;

import com.cafepos.factory.ProductFactory;
import com.cafepos.io.ReceiptPrinter;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.pricing.*;

import com.cafepos.domain.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutServiceTests {

    static class TestPaymentStrategy implements PaymentStrategy {
        public boolean wasCalled = false;
        public Order capturedOrder = null;

        @Override
        public void pay(Order order) {
            wasCalled = true;
            capturedOrder = order;
        }
    }

    @Test
    void checkout_with_decorated_product_generates_correct_receipt() {
        var factory = new ProductFactory(); // real factory
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var payment = new TestPaymentStrategy();

        var service = new CheckoutService(factory, pricing, printer, 10);
        String receipt = service.checkout(2001L, "LAT+L+OAT", 1, payment);

        // LAT = 3.20, L = +0.70, OAT = +0.50 → subtotal = 4.40
        // Tax = 10% of 4.50 = 0.45 → total = 4.95

        System.out.println(receipt);
        assertTrue(receipt.contains("Order (LAT+L+OAT) x1"));
        assertTrue(receipt.contains("Subtotal: 4.40"));
        assertTrue(receipt.contains("Tax (10%): 0.44"));
        assertTrue(receipt.contains("Total: 4.84"));
        assertTrue(payment.wasCalled);
        assertNotNull(payment.capturedOrder);
    }

    @Test
    void checkout_with_multiple_quantity_and_addons() {
        var factory = new ProductFactory();
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var payment = new TestPaymentStrategy();

        var service = new CheckoutService(factory, pricing, printer, 10);
        String receipt = service.checkout(2002L, "CAP+SHOT+SYP", 2, payment);

        // CAP = 3.00, SHOT = +0.80, SYP = +0.40 → unit = 4.20
        // subtotal = 8.40, tax = 0.84, total = 9.24

        assertTrue(receipt.contains("Order (CAP+SHOT+SYP) x2"));
        assertTrue(receipt.contains("Subtotal: 8.40"));
        assertTrue(receipt.contains("Tax (10%): 0.84"));
        assertTrue(receipt.contains("Total: 9.24"));
    }
}
