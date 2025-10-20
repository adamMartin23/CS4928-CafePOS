package com.cafepos.demo;

import com.cafepos.catalog.*;
import com.cafepos.checkout.CheckoutService;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.io.ReceiptPrinter;
import com.cafepos.payment.*;
import com.cafepos.Observer.*;
import com.cafepos.pricing.*;

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
            System.out.println("3. Remove Item from Order");
            System.out.println("4. Pay");
            System.out.println("5. Mark Order Ready");
            System.out.println("6. View Order Summary");
            System.out.println("7. Checkout");
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
                    System.out.print("Enter product ID to remove (e.g., P-ESP): ");
                    String productId = scanner.nextLine();

                    boolean removed = order.removeItem(productId);
                    if (removed) {
                        System.out.println("Removed item with ID: " + productId);
                    } else {
                        System.out.println("No matching item found.");
                    }
                }
                case "4" -> {
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
                case "5" -> {
                    order.markReady();
                }
                case "6" -> {
                    System.out.println("Order #" + order.id());
                    for (LineItem item : order.items()) {
                        System.out.println(" → " + item.quantity() + "x " + item.product().name() + " (€" + item.lineTotal() + ")");
                    }
                    System.out.println("Subtotal: €" + order.subtotal());
                    System.out.println("Tax (10%): €" + order.taxAtPercent(10));
                    System.out.println("Total: €" + order.totalWithTax(10));
                }
                case "7" -> {
                    int taxPercent = 10;
                    System.out.print("Enter recipe (e.g., ESP+SHOT+OAT): ");
                    String recipe = scanner.nextLine();

                    System.out.print("Enter quantity: ");
                    int qty;
                    try {
                        qty = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity.");
                        break;
                    }

                    System.out.println("Choose discount type:");
                    System.out.println("1. No Discount");
                    System.out.println("2. Loyalty Discount (%)");
                    System.out.println("3. Fixed Coupon Discount (€)");

                    DiscountPolicy discountPolicy = new NoDiscount(); // Default fallback
                    String discountChoice = scanner.nextLine();
                    switch (discountChoice) {
                        case "1" -> discountPolicy = new NoDiscount();
                        case "2" -> {
                            System.out.print("Enter loyalty discount percent: ");
                            try {
                                int percent = Integer.parseInt(scanner.nextLine());
                                discountPolicy = new LoyaltyPercentDiscount(percent);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid percent.");
                                break;
                            }
                        }
                        case "3" -> {
                            System.out.print("Enter coupon amount (€): ");
                            try {
                                double amount = Double.parseDouble(scanner.nextLine());
                                discountPolicy = new FixedCouponDiscount(Money.of(amount));
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid amount.");
                                break;
                            }
                        }
                        default -> {
                            System.out.println("Invalid discount type.");
                            break;
                        }
                    }

                    System.out.println("Choose payment method:");
                    System.out.println("1. Cash");
                    System.out.println("2. Card");
                    System.out.println("3. Wallet");

                    PaymentStrategy payment = new CashPayment(); // Default
                    String paymentChoice = scanner.nextLine();
                    switch (paymentChoice) {
                        case "1" -> payment = new CashPayment();
                        case "2" -> {
                            System.out.print("Enter card number: ");
                            String cardNumber = scanner.nextLine();
                            payment = new CardPayment(cardNumber);
                        }
                        case "3" -> {
                            System.out.print("Enter wallet ID: ");
                            String walletId = scanner.nextLine();
                            payment = new WalletPayment(walletId);
                        }
                        default -> {
                            System.out.println("Invalid payment method.");
                            break;
                        }
                    }

                    TaxPolicy taxPolicy = new FixedRateTaxPolicy(taxPercent);
                    PricingService pricing = new PricingService(discountPolicy, taxPolicy);
                    ReceiptPrinter printer = new ReceiptPrinter();
                    CheckoutService checkoutService = new CheckoutService(factory, pricing, printer, taxPercent);

                    String receipt = checkoutService.checkout(recipe, qty, payment);
                    System.out.println("\n--- Receipt ---");
                    System.out.println(receipt);
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