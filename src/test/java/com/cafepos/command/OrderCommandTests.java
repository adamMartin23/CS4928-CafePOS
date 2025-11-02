package com.cafepos.command;

import com.cafepos.command.*;
import com.cafepos.domain.*;
import com.cafepos.domain.Order;
import com.cafepos.payment.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderCommandTests {

    Order order;
    OrderService service;

    @BeforeEach
    void setup() {
        order = new Order(OrderIds.next());
        service = new OrderService(order);
    }

    @Test
    void addItemCommand_adds_item_to_order() {
        var cmd = new AddItemCommand(service, "LAT+L", 2);
        cmd.execute();
        assertEquals(1, order.items().size());
        assertEquals("Latte (Large)", order.items().get(0).product().name());
        assertEquals(2, order.items().get(0).quantity());
    }

    @Test
    void addItemCommand_undo_removes_last_item() {
        var cmd = new AddItemCommand(service, "ESP+SHOT+OAT", 1);
        cmd.execute();
        assertEquals(1, order.items().size());
        cmd.undo();
        assertEquals(0, order.items().size());
    }

    @Test
    void payOrderCommand_executes_payment() {
        service.addItem("LAT+L", 2);
        var cmd = new PayOrderCommand(service, new CardPayment("1234567890123456"), 10);
        cmd.execute();
        // Console output confirms behavior; no assertion unless mocking PaymentStrategy
    }

    @Test
    void posRemote_executes_and_undoes_commands() {
        var remote = new PosRemote(3);
        remote.setSlot(0, new AddItemCommand(service, "LAT+L", 2));
        remote.setSlot(1, new AddItemCommand(service, "ESP+SHOT+OAT", 1));
        remote.setSlot(2, new PayOrderCommand(service, new CardPayment("1234567890123456"), 10));

        remote.press(0);
        remote.press(1);
        assertEquals(2, order.items().size());

        remote.undo(); // should remove ESP+SHOT+OAT
        assertEquals(1, order.items().size());
        assertEquals("Latte (Large)", order.items().get(0).product().name());

        remote.press(2); // pay
    }

    @Test
    void macroCommand_executes_and_undoes_in_reverse() {
        var cmd1 = new AddItemCommand(service, "LAT+L", 1);
        var cmd2 = new AddItemCommand(service, "ESP+SHOT+OAT", 1);
        var macro = new MacroCommand(cmd1, cmd2);

        macro.execute();
        assertEquals(2, order.items().size());

        macro.undo();
        assertEquals(0, order.items().size());
    }
}


