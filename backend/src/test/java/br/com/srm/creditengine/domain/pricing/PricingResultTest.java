package br.com.srm.creditengine.domain.pricing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingResultTest {

    @Test
    void gettersReturnValues() {
        PricingResult r = new PricingResult(new BigDecimal("900.00"), new BigDecimal("100.00"), new BigDecimal("0.50"), 1);
        assertEquals(new BigDecimal("900.00"), r.getPresentValue());
        assertEquals(new BigDecimal("100.00"), r.getAppliedTax());
        assertEquals(new BigDecimal("0.50"), r.getAppliedSpread());
        assertEquals(1, r.getTermInMonths());
    }

    @Test
    void constructorRejectsNulls() {
        assertThrows(NullPointerException.class, () -> new PricingResult(null, BigDecimal.ZERO, BigDecimal.ZERO, 1));
        assertThrows(NullPointerException.class, () -> new PricingResult(BigDecimal.ZERO, null, BigDecimal.ZERO, 1));
        assertThrows(NullPointerException.class, () -> new PricingResult(BigDecimal.ZERO, BigDecimal.ZERO, null, 1));
    }
}

