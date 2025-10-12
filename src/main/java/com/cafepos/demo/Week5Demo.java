package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.decorator.ExtraShot;
import com.cafepos.decorator.OatMilk;
import com.cafepos.decorator.SizeLarge;

public class Week5Demo {

// decorated.name() => "Espresso + Extra Shot + Oat Milk (Large)"
// decorated.price() => 2.50 + 0.80 + 0.50 + 0.70 = 4.50

    public static void main(String[] args) {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));

        System.out.println(decorated.name());
        System.out.println(((SizeLarge) decorated).price());
    }
}
