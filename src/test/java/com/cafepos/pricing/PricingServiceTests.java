package com.cafepos.pricing;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PricingServiceTests {

    @Test void pricing_result_matches_expected_values() {
        DiscountPolicy discount = new FixedCouponDiscount(Money.of(2.00));
        TaxPolicy tax = new FixedRateTaxPolicy(10);
        PricingService service = new PricingService(discount, tax);

        PricingService.PricingResult result = service.price(Money.of(10.00));

        assertEquals(Money.of(10.00), result.subtotal());
        assertEquals(Money.of(2.00), result.discount());
        assertEquals(Money.of(0.80), result.tax());
        assertEquals(Money.of(8.80), result.total());
    }

    @Test void discount_exceeding_subtotal_is_capped() {
        DiscountPolicy discount = new FixedCouponDiscount(Money.of(20.00));
        TaxPolicy tax = new FixedRateTaxPolicy(10);
        PricingService service = new PricingService(discount, tax);

        PricingService.PricingResult result = service.price(Money.of(10.00));
        Money discounted = Money.of(result.subtotal().asBigDecimal().subtract(result.discount().asBigDecimal()));

        assertEquals(Money.zero(), discounted);
        assertEquals(Money.zero(), result.tax());
        assertEquals(Money.zero(), result.total());
    }
}
