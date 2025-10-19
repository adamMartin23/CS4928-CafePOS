package com.cafepos.pricing;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaxPolicyTests {

    @Test void fixed_rate_tax_applies_correct_percentage() {
        TaxPolicy policy = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(1.00), policy.taxOn(Money.of(10.00)));
    }

    @Test void zero_tax_returns_zero() {
        TaxPolicy policy = new FixedRateTaxPolicy(0);
        assertEquals(Money.zero(), policy.taxOn(Money.of(100.00)));
    }

    @Test void negative_tax_rate_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> new FixedRateTaxPolicy(-5));
    }
}
