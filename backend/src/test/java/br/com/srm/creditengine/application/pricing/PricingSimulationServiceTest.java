package br.com.srm.creditengine.application.pricing;

import br.com.srm.creditengine.domain.pricing.PricingRequest;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import br.com.srm.creditengine.domain.pricing.ReceivablePricingStrategyResolver;
import br.com.srm.creditengine.domain.pricing.strategy.MercantileDuplicatePricingStrategy;
import br.com.srm.creditengine.domain.pricing.strategy.PostDatedCheckPricingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingSimulationServiceTest {

    @Test
    void simulateDuplicate() {
        ReceivablePricingStrategyResolver resolver = new ReceivablePricingStrategyResolver(
                java.util.List.of(new MercantileDuplicatePricingStrategy(), new PostDatedCheckPricingStrategy())
        );
        PricingSimulationService service = new PricingSimulationService(resolver);
        PricingRequest request = new PricingRequest(new BigDecimal("1000.00"), new BigDecimal("0.004167"), 1, "DUPLICATA");
        PricingResult result = service.simulate(request);
        assertNotNull(result);
        assertEquals(1, result.getTermInMonths());
        assertTrue(result.getPresentValue().compareTo(new BigDecimal("900")) > 0);
    }

    @Test
    void simulateCheque() {
        ReceivablePricingStrategyResolver resolver = new ReceivablePricingStrategyResolver(
                java.util.List.of(new MercantileDuplicatePricingStrategy(), new PostDatedCheckPricingStrategy())
        );
        PricingSimulationService service = new PricingSimulationService(resolver);
        PricingRequest request = new PricingRequest(new BigDecimal("1000.00"), new BigDecimal("0.004167"), 1, "CHEQUE_PRE_DATADO");
        PricingResult result = service.simulate(request);
        assertNotNull(result);
        assertEquals(1, result.getTermInMonths());
        assertTrue(result.getPresentValue().compareTo(new BigDecimal("900")) > 0);
    }
}

