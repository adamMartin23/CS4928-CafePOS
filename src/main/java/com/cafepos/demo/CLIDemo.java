package com.cafepos.demo;

import com.cafepos.catalog.*;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.*;
import com.cafepos.Observer.*;

import java.util.Scanner;

public final class CLIDemo {
    public static void main(String[] args) {
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-LAT", "Latte", Money.of(3.00)));
        catalog.add(new SimpleProduct("P-CAP", "Cappuccino", Money.of(2.75)));

        Order order = new Order(OrderIds.next());

        // Register observers
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        ProductFactory factory = new ProductFactory();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Café POS CLI");
        boolean running = true;

        while (running) {
            System.out.println("\nChoose an action:");
            System.out.println("1. View Catalog");
            System.out.println("2. Add Item to Order");
            System.out.println("3. Pay");
            System.out.println("4. Mark Order Ready");
            System.out.println("5. View Order Summary");
            System.out.println("0. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.println("Catalog:");
                    for (Product p : ((InMemoryCatalog) catalog).all()) {
                        System.out.println(" → " + p.id() + ": " + p.name() + " (€" + p.basePrice() + ")");
                    }
                    System.out.println("Available add-ons: SHOT, OAT, SYP, L");
                    System.out.println("Use recipe format like ESP+SHOT+OAT");
                }
                case "2" -> {
                    System.out.print("Enter recipe (e.g., ESP+SHOT+OAT): ");
                    String recipe = scanner.nextLine();
                    Product product;
                    try {
                        product = factory.create(recipe);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid recipe: " + e.getMessage());
                        break;
                    }

                    System.out.print("Enter quantity: ");
                    int qty;
                    try {
                        qty = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity.");
                        break;
                    }

                    order.addItem(new LineItem(product, qty));
                    System.out.println("Added: " + qty + "x " + product.name());
                }
                case "3" -> {
                    System.out.println("Choose payment method:");
                    System.out.println("1. Cash");
                    System.out.println("2. Card");
                    System.out.println("3. Wallet");
                    String paymentChoice = scanner.nextLine();

                    switch (paymentChoice) {
                        case "1" -> order.pay(new CashPayment());
                        case "2" -> {
                            System.out.print("Enter card number: ");
                            String cardNumber = scanner.nextLine();
                            order.pay(new CardPayment(cardNumber));
                        }
                        case "3" -> {
                            System.out.print("Enter wallet ID: ");
                            String walletId = scanner.nextLine();
                            order.pay(new WalletPayment(walletId));
                        }
                        default -> System.out.println("Invalid payment method.");
                    }
                }
                case "4" -> {
                    order.markReady();
                }
                case "5" -> {
                    System.out.println("Order #" + order.id());
                    for (LineItem item : order.items()) {
                        System.out.println(" → " + item.quantity() + "x " + item.product().name() + " (€" + item.lineTotal() + ")");
                    }
                    System.out.println("Subtotal: €" + order.subtotal());
                    System.out.println("Tax (10%): €" + order.taxAtPercent(10));
                    System.out.println("Total: €" + order.totalWithTax(10));
                }
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