package com.cafepos.payment;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStrategyTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void payment_strategy_called() {
        var product = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(product, 1));
        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;
        order.pay(fake);
        assertTrue(called[0], "Payment strategy should be called");
    }

    @Test
    void pay_null_strategy_throws() {
        var order = new Order(99);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> order.pay(null));
        assertEquals("strategy required", ex.getMessage());
    }

    @Test
    void card_payment_masks_card_number_and_prints_amount() {

        var product = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        var order = new Order(100);
        order.addItem(new LineItem(product, 2)); // 2 * 2.50 = 5.00

        var cookie = new SimpleProduct("P-CCK", "Cookie", Money.of(3.50));
        order.addItem(new LineItem(cookie, 1)); // +3.50 => 8.50

        var card = new CardPayment("1234567812341234");
        order.pay(card);

        String out = outContent.toString().trim();
        assertTrue(out.startsWith("[Card]"), "Output should start with Card prefix");
        assertTrue(out.contains("****1234"), "Card number must be masked leaving last 4 digits");

        assertTrue(out.contains("EUR"), "Output should contain currency marker EUR");
    }

    @Test
    void cash_payment_prints_amount() {
        var product = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        var order = new Order(101);
        order.addItem(new LineItem(product, 2));
        var cash = new CashPayment();
        order.pay(cash);

        String out = outContent.toString().trim();
        assertTrue(out.startsWith("[Cash]"), "Output should start with Cash prefix");
        assertTrue(out.contains("EUR"), "Output should contain currency marker EUR");
    }

    @Test
    void wallet_payment_prints_wallet_id_and_amount() {
        var product = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        var order = new Order(200);
        order.addItem(new LineItem(product, 2)); // 5.00
        var cookie = new SimpleProduct("P-CCK", "Cookie", Money.of(3.50));
        order.addItem(new LineItem(cookie, 1)); // +3.50 => 8.50

        String walletId = "alice-wallet-01";
        var walletPayment = new WalletPayment(walletId);
        order.pay(walletPayment);

        String out = outContent.toString().trim();
        assertTrue(out.startsWith("[Wallet]"), "Output should start with Wallet prefix");
        assertTrue(out.contains(walletId), "Output should include the wallet id");
        assertTrue(out.contains("EUR"), "Output should contain currency marker EUR");

    }
}
