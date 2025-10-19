package com.cafepos.pricing;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DiscountPolicyTests {

    @Test void no_discount_returns_zero() {
        DiscountPolicy policy = new NoDiscount();
        assertEquals(Money.zero(), policy.discountOf(Money.of(10.00)));
    }

    @Test void loyalty_discount_applies_percentage() {
        DiscountPolicy policy = new LoyaltyPercentDiscount(5);
        assertEquals(Money.of(0.50), policy.discountOf(Money.of(10.00)));
    }

    @Test void fixed_coupon_discount_applies_flat_amount() {
        DiscountPolicy policy = new FixedCouponDiscount(Money.of(1.00));
        assertEquals(Money.of(1.00), policy.discountOf(Money.of(5.00)));
    }

    @Test void fixed_coupon_discount_capped_at_subtotal() {
        DiscountPolicy policy = new FixedCouponDiscount(Money.of(10.00));
        assertEquals(Money.of(5.00), policy.discountOf(Money.of(5.00)));
    }
}
