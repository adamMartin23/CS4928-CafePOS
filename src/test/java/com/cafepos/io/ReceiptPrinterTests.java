package com.cafepos.io;

import com.cafepos.common.Money;
import com.cafepos.pricing.PricingService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReceiptPrinterTests {

    @Test void receipt_includes_all_fields() {
        ReceiptPrinter printer = new ReceiptPrinter();
        PricingService.PricingResult result = new PricingService.PricingResult(
                Money.of(10.00),
                Money.of(2.00),
                Money.of(0.80),
                Money.of(8.80)
        );

        String receipt = printer.format("LAT+L", 2, result, 10);

        assertTrue(receipt.contains("Order (LAT+L) x2"));
        assertTrue(receipt.contains("Subtotal: 10.00"));
        assertTrue(receipt.contains("Discount: -2.00"));
        assertTrue(receipt.contains("Tax (10%): 0.80"));
        assertTrue(receipt.contains("Total: 8.80"));
    }

    @Test void receipt_omits_discount_if_zero() {
        ReceiptPrinter printer = new ReceiptPrinter();
        PricingService.PricingResult result = new PricingService.PricingResult(
                Money.of(5.00),
                Money.zero(),
                Money.of(0.50),
                Money.of(5.50)
        );

        String receipt = printer.format("ESP", 1, result, 10);

        assertFalse(receipt.contains("Discount:"));
        assertTrue(receipt.contains("Tax (10%): 0.50"));
    }
}
