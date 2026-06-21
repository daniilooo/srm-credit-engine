package br.com.srm.creditengine.domain.pricing;

import br.com.srm.creditengine.domain.pricing.strategy.PostDatedCheckPricingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PostDatedCheckPricingStrategyTest {

    private final PostDatedCheckPricingStrategy strategy = new PostDatedCheckPricingStrategy();

    @Test
    void calculateOneMonth() {
        PricingRequest request = new PricingRequest(new BigDecimal("1000.00"), new BigDecimal("0.004167"), 1, "CHEQUE_PRE_DATADO");
        PricingResult result = strategy.calculate(request);
        assertNotNull(result);
        assertEquals(1, result.getTermInMonths());
        // present value should be less than face; check it's reasonable
        assertTrue(result.getPresentValue().compareTo(new BigDecimal("980")) < 0);
    }

    @Test
    void invalidTermThrows() {
        PricingRequest request = new PricingRequest(new BigDecimal("1000.00"), new BigDecimal("0.004167"), -1, "CHEQUE_PRE_DATADO");
        assertThrows(PricingException.class, () -> strategy.calculate(request));
    }
}


