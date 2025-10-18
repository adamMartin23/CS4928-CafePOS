package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;

public class OrderManagerGod {

    public static int TAX_PERCENT = 10; // Global/Static State // Primitive Obsession
    public static String LAST_DISCOUNT_CODE = null; // Global/Static State

    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        // God Class & Long Method: One method performs creation, pricing, discounting, tax, payment I/O, and printing.

        ProductFactory factory = new ProductFactory(); // God Class: creation logic embedded here
        Product product = factory.create(recipe);
        Money unitPrice;

        try {
            var priced = product instanceof com.cafepos.decorator.Priced p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice(); // Duplicated Logic
        }
        if (qty <= 0) qty = 1; // Primitive Obsession: magic number clamp
        Money subtotal = unitPrice.multiply(qty); // Duplicated Logic
        Money discount = Money.zero();
        if (discountCode != null) { // Primitive Obsession
            if (discountCode.equalsIgnoreCase("LOYAL5")) { // Primitive Obsession
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5)) // Duplicated Logic // Shotgun Surgery // Feature Envy
                        .divide(java.math.BigDecimal.valueOf(100))); // Duplicated Logic // Feature Envy
            } else if (discountCode.equalsIgnoreCase("COUPON1")) { // Primitive Obsession
                discount = Money.of(1.00); // Primitive Obsession // Feature Envy
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            LAST_DISCOUNT_CODE = discountCode; // Global/Static State
        }

        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal())); // Duplicated Logic

        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero(); // Primitive Obsession
        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT)) // Duplicated Logic // Shotgun Surgery // Feature Envy
                .divide(java.math.BigDecimal.valueOf(100))); // Duplicated Logic // Feature Envy
        var total = discounted.add(tax); // Duplicated Logic

        if (paymentType != null) { // Primitive Obsession
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + " EUR"); // God Class: payment I/O
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234"); // God Class: payment I/O
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789"); // God Class: payment I/O
            } else {
                System.out.println("[UnknownPayment] " + total); // God Class: payment I/O
            }
        }

        StringBuilder receipt = new StringBuilder(); // God Class: receipt formatting
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");

        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total); // God Class: receipt formatting

        String out = receipt.toString();

        if (printReceipt) {
            System.out.println(out); // God Class: receipt formatting
        }
        return out;
    }
}
