package br.com.srm.creditengine.domain.pricing;

import java.util.HashMap;
import java.util.Map;

public class ReceivablePricingStrategyResolver {
    private final Map<String, PricingStrategy> registry = new HashMap<>();

    public ReceivablePricingStrategyResolver(Iterable<PricingStrategy> strategies) {
        for (PricingStrategy s : strategies) {
            registry.put(s.getReceivableTypeCode(), s);
        }
    }

    public PricingStrategy resolve(String receivableTypeCode) {
        PricingStrategy s = registry.get(receivableTypeCode);
        if (s == null) {
            throw new PricingException("No pricing strategy for type: " + receivableTypeCode);
        }
        return s;
    }
}

