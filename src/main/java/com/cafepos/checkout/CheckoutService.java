package com.cafepos.checkout;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.io.ReceiptPrinter;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.pricing.PricingService;

public final class CheckoutService {
    private final ProductFactory factory;
    private final PricingService pricing;
    private final ReceiptPrinter printer;
    private final int taxPercent;

    public CheckoutService(ProductFactory factory, PricingService pricing, ReceiptPrinter printer, int taxPercent) {
        this.factory = factory;
        this.pricing = pricing;
        this.printer = printer;
        this.taxPercent = taxPercent;
    }

    public String checkout(String recipe, int qty, PaymentStrategy payment) {
        Product product = factory.create(recipe);
        if (qty <= 0){
            qty = 1;
        }

        Money unit = (product instanceof com.cafepos.decorator.Priced p) ? p.price() : product.basePrice();
        Money subtotal = unit.multiply(qty);
        var result = pricing.price(subtotal);

        LineItem item = new LineItem(product, qty);
        Order order = new Order(OrderIds.next());
        order.addItem(item);
        order.setTotal(result.total());

        // Adapt to your Week-3 signature; if your strategy expects an Order, pass the real one here.
        // If your strategy prints based on totals, wrap in a tiny adapter and call after pricing.
        order.pay(payment);

        return printer.format(recipe, qty, result, taxPercent);
    }
}
