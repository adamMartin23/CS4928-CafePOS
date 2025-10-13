package com.cafepos.decorator;

import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.common.Priced;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class decoratorTest {
    @Test
    void decorator_single_addon() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product withShot = new ExtraShot(espresso);
        assertEquals("Espresso + Extra Shot", withShot.name());
// if using Priced interface:
        assertEquals(Money.of(3.30), ((Priced) withShot).price());
    }
    @Test void decorator_stacks() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new
                ExtraShot(espresso)));
        assertEquals("Espresso + Extra Shot + Oat Milk (Large)",
                decorated.name());
        assertEquals(Money.of(4.50), ((Priced) decorated).price());
    }
    @Test void factory_parses_recipe() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");
        assertTrue(p.name().contains("Espresso") &&
                p.name().contains("Oat Milk"));
    }
    @Test void order_uses_decorated_price() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product withShot = new ExtraShot(espresso); // 3.30
        Order o = new Order(1);
        o.addItem(new LineItem(withShot, 2));
        assertEquals(Money.of(6.60), o.subtotal());
    }

    @Test
    void factory_vs_manual_construction_equivalence() {
        // Factory-built drink
        Product viaFactory = new ProductFactory().create("ESP+SHOT+OAT+L");

        // Manually wrapped drink
        Product viaManual = new SizeLarge(
                new OatMilk(
                        new ExtraShot(
                                new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
                        )
                )
        );

        // Name equivalence
        assertEquals(viaManual.name(), viaFactory.name());

        // Price equivalence
        assertEquals(((Priced) viaManual).price(), ((Priced) viaFactory).price());

        // Order subtotal and totalWithTax equivalence
        Order orderFactory = new Order(1);
        orderFactory.addItem(new LineItem(viaFactory, 1));

        Order orderManual = new Order(2);
        orderManual.addItem(new LineItem(viaManual, 1));

        assertEquals(orderManual.subtotal(), orderFactory.subtotal());
        assertEquals(orderManual.totalWithTax(10), orderFactory.totalWithTax(10));
    }

    @Test void simple_product_has_base_price_and_name() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        assertEquals("Espresso", espresso.name());
        assertEquals(Money.of(2.50), ((Priced) espresso).price());
    }

    @Test void duplicate_decorators_stack_surcharges() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product doubleShot = new ExtraShot(new ExtraShot(espresso));
        assertEquals("Espresso + Extra Shot + Extra Shot", doubleShot.name());
        assertEquals(Money.of(4.10), ((Priced) doubleShot).price()); // $2.50 + $0.80 + $0.80
    }
}
