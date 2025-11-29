// com/cafepos/demo/FinalCafePosDemo.java
package com.cafepos.demo;

import com.cafepos.infra.Wiring;
import com.cafepos.ui.OrderController;
import com.cafepos.ui.ConsoleView;
import com.cafepos.command.*;
import com.cafepos.payment.CashPayment;
import com.cafepos.printing.Printer;
import com.cafepos.printing.LegacyPrinterAdapter;
import vendor.legacy.LegacyThermalPrinter;
import com.cafepos.menu.*;
import com.cafepos.common.Money;
import com.cafepos.state.*;
import com.cafepos.app.events.*;

public final class FinalCafePosDemo {
    public static void main(String[] args) {
        // === Layered Wiring + MVC ===
        var c = Wiring.createDefault();
        var controller = new OrderController(c.repo(), c.checkout());
        var view = new ConsoleView();

        long id = 5001L;
        controller.createOrder(id);
        controller.addItem(id, "ESP+SHOT+OAT", 1);
        controller.addItem(id, "LAT+L", 2);
        String receipt = controller.checkout(id, 10);
        view.print(receipt);

        // === Adapter Pattern (legacy printer) ===
        Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
        printer.print(receipt);

        // === Command Pattern (remote control) ===
        var service = new OrderService(c.repo().findById(id).orElseThrow());
        var remote = new PosRemote(3);
        remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));
        remote.setSlot(2, new PayOrderCommand(service, new CashPayment(), 10));

        remote.press(0); // add Espresso
        remote.press(1); // add Latte
        remote.undo();   // undo Latte
        remote.press(2); // pay order

        // === Composite + Iterator (menu traversal) ===
        Menu breakfast = new Menu("Breakfast");
        breakfast.add(new MenuItem("Pancakes", Money.of(5.99), true));
        breakfast.add(new MenuItem("Bacon & Eggs", Money.of(7.49), false));

        Menu lunch = new Menu("Lunch");
        lunch.add(new MenuItem("Veggie Burger", Money.of(8.99), true));
        lunch.add(new MenuItem("Chicken Sandwich", Money.of(9.49), false));

        Menu allMenus = new Menu("All Menus");
        allMenus.add(breakfast);
        allMenus.add(lunch);

        System.out.println("\n-- Full Menu --");
        allMenus.print();

        System.out.println("\n-- Vegetarian Options --");
        for (MenuItem mi : allMenus.vegetarianItems()) {
            System.out.println(" * " + mi.name() + " = " + mi.price());
        }

        // === State Pattern (order lifecycle FSM) ===
        OrderFSM fsm = new OrderFSM();
        System.out.println("\nFSM initial: " + fsm.status());
        fsm.pay();
        fsm.prepare();
        fsm.markReady();
        fsm.deliver();
        System.out.println("FSM final: " + fsm.status());

        // === EventBus (components/connectors) ===
        EventBus bus = new EventBus();
        bus.on(OrderCreated.class, e -> System.out.println("[UI] order created: " + e.orderId()));
        bus.on(OrderPaid.class, e -> System.out.println("[UI] order paid: " + e.orderId()));

        bus.emit(new OrderCreated(id));
        bus.emit(new OrderPaid(id));
    }
}
