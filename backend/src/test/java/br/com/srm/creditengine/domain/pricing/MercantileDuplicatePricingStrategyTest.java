package br.com.srm.creditengine.domain.pricing;

import br.com.srm.creditengine.domain.pricing.strategy.MercantileDuplicatePricingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MercantileDuplicatePricingStrategyTest {

    private final MercantileDuplicatePricingStrategy strategy = new MercantileDuplicatePricingStrategy();

    @Test
    void calculateOneMonth() {
        PricingRequest request = new PricingRequest(new BigDecimal("1000.00"), new BigDecimal("0.004167"), 1, "DUPLICATA");
        PricingResult result = strategy.calculate(request);
        assertNotNull(result);
        assertEquals(1, result.getTermInMonths());
        // present value should be less than face
        assertTrue(result.getPresentValue().compareTo(new BigDecimal("990")) < 0);
    }

    @Test
    void invalidTermThrows() {
        PricingRequest request = new PricingRequest(new BigDecimal("1000.00"), new BigDecimal("0.004167"), 0, "DUPLICATA");
        assertThrows(PricingException.class, () -> strategy.calculate(request));
    }

    @Test
    void invalidFaceThrows() {
        PricingRequest request = new PricingRequest(new BigDecimal("0"), new BigDecimal("0.004167"), 1, "DUPLICATA");
        assertThrows(PricingException.class, () -> strategy.calculate(request));
    }
}

