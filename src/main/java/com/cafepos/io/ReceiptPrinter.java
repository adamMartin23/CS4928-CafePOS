package com.cafepos.io;

import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;

import com.cafepos.domain.Order;
import com.cafepos.pricing.PricingService;

public final class ReceiptPrinter {
    public String format(String recipe, int qty, PricingService.PricingResult pr, int taxPercent) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(pr.subtotal()).append("\n");

        if (pr.discount().asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(pr.discount()).append("\n");
        }

        receipt.append("Tax (").append(taxPercent).append("%): ").append(pr.tax()).append("\n");
        receipt.append("Total: ").append(pr.total());

        return receipt.toString();
    }

    public String format(Order order, PricingService.PricingResult pr, int taxPercent) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order #").append(order.id()).append("\n");

        for (LineItem item : order.items()) {
            String productName = item.product().name();
            int quantity = item.quantity();
            Money lineTotal = item.lineTotal();

            receipt.append("- ")
                    .append(productName)
                    .append(" x").append(quantity)
                    .append(": ").append(lineTotal)
                    .append("\n");
        }

        receipt.append("Subtotal: ").append(pr.subtotal()).append("\n");

        if (pr.discount().asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(pr.discount()).append("\n");
        }

        receipt.append("Tax (").append(taxPercent).append("%): ").append(pr.tax()).append("\n");
        receipt.append("Total: ").append(pr.total());

        return receipt.toString();
    }

}

