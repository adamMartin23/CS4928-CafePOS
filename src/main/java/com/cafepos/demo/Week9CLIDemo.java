package com.cafepos.demo;

import com.cafepos.menu.*;
import com.cafepos.common.Money;
import com.cafepos.state.OrderFSM;

import java.util.Scanner;

public final class Week9CLIDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        // Build menu tree
        Menu root = new Menu("CAFÉ MENU");
        Menu drinks = new Menu("Drinks");
        Menu coffee = new Menu("Coffee");
        Menu desserts = new Menu("Desserts");

        coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
        coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));
        drinks.add(coffee);
        desserts.add(new MenuItem("Cheesecake", Money.of(3.50), false));
        desserts.add(new MenuItem("Oat Cookie", Money.of(1.20), true));
        root.add(drinks);
        root.add(desserts);

        // Initialize FSM
        OrderFSM fsm = new OrderFSM();

        System.out.println("Welcome to Café POS Lab CLI");

        while (running) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Print Full Menu");
            System.out.println("2. List Vegetarian Items");
            System.out.println("3. View Order Status");
            System.out.println("4. Pay");
            System.out.println("5. Prepare");
            System.out.println("6. Mark Ready");
            System.out.println("7. Deliver");
            System.out.println("8. Cancel");
            System.out.println("0. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> root.print();
                case "2" -> {
                    System.out.println("Vegetarian options:");
                    for (MenuItem mi : root.vegetarianItems()) {
                        System.out.println(" * " + mi.name() + " = " + mi.price());
                    }
                }
                case "3" -> System.out.println("Order Status: " + fsm.status());
                case "4" -> fsm.pay();
                case "5" -> fsm.prepare();
                case "6" -> fsm.markReady();
                case "7" -> fsm.deliver();
                case "8" -> fsm.cancel();

                case "0" -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }
}

