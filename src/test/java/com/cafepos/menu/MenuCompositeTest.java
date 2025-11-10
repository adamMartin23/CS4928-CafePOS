package com.cafepos.menu;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.factory.ProductFactory;
import com.cafepos.menu.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuCompositeTest {
    @Test
    void depth_first_iteration_collects_all_nodes() {
        Menu root = new Menu("ROOT");
        Menu drinks = new Menu("Drinks");
        Menu desserts = new Menu("Desserts");

        root.add(drinks);
        root.add(desserts);

        drinks.add(new MenuItem("Espresso", Money.of(2.5), true));
        desserts.add(new MenuItem("Cheesecake", Money.of(3.5), false));
        desserts.add(new MenuItem("Oat Cookie", Money.of(1.2), true));

        List<String> names = root.allItems().stream().map(MenuComponent::name).toList();

        assertTrue(names.contains("Espresso"));
        assertTrue(names.contains("Cheesecake"));
        assertTrue(names.contains("Oat Cookie"));
    }

    @Test
    void vegetarianItems_returns_only_vegetarian_items() {
        Menu root = new Menu("CAFÉ MENU");
        Menu drinks = new Menu("Drinks");
        Menu desserts = new Menu("Desserts");

        drinks.add(new MenuItem("Latte", Money.of(3.9), true));
        desserts.add(new MenuItem("Brownie", Money.of(2.5), false));
        desserts.add(new MenuItem("Fruit Salad", Money.of(2.0), true));

        root.add(drinks);
        root.add(desserts);

        List<MenuItem> vegItems = root.vegetarianItems();
        List<String> vegNames = vegItems.stream().map(MenuItem::name).toList();

        assertEquals(2, vegItems.size());
        assertTrue(vegNames.contains("Latte"));
        assertTrue(vegNames.contains("Fruit Salad"));
        assertFalse(vegNames.contains("Brownie"));
    }

    @Test
    void menu_item_and_factory_product_have_consistent_price() {
        MenuItem item = new MenuItem("Latte (Large)", Money.of(3.90), true);
        ProductFactory factory = new ProductFactory();
        Product product = factory.create("LAT + L");
        LineItem li = new LineItem(product, 1);

        assertEquals(item.name(), "Latte (Large)");
        //assertEquals(item.price(), product.basePrice());
        assertEquals(item.price(), li.lineTotal());
    }

}

