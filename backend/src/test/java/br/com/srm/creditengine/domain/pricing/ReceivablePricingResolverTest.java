package br.com.srm.creditengine.domain.pricing;

import br.com.srm.creditengine.domain.pricing.strategy.MercantileDuplicatePricingStrategy;
import br.com.srm.creditengine.domain.pricing.strategy.PostDatedCheckPricingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReceivablePricingResolverTest {

    @Test
    void resolveKnownStrategies() {
        ReceivablePricingStrategyResolver resolver = new ReceivablePricingStrategyResolver(
                java.util.List.of(new MercantileDuplicatePricingStrategy(), new PostDatedCheckPricingStrategy())
        );
        PricingStrategy s1 = resolver.resolve("DUPLICATA");
        assertNotNull(s1);
        assertEquals("DUPLICATA", s1.getReceivableTypeCode());
        PricingStrategy s2 = resolver.resolve("CHEQUE_PRE_DATADO");
        assertNotNull(s2);
        assertEquals("CHEQUE_PRE_DATADO", s2.getReceivableTypeCode());
    }

    @Test
    void resolveUnknownThrows() {
        ReceivablePricingStrategyResolver resolver = new ReceivablePricingStrategyResolver(
                java.util.List.of(new MercantileDuplicatePricingStrategy())
        );
        assertThrows(PricingException.class, () -> resolver.resolve("UNKNOWN"));
    }
}

