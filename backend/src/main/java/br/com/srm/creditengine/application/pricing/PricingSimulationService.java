package br.com.srm.creditengine.application.pricing;

import br.com.srm.creditengine.domain.pricing.PricingRequest;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import br.com.srm.creditengine.domain.pricing.PricingStrategy;
import br.com.srm.creditengine.domain.pricing.ReceivablePricingStrategyResolver;

public class PricingSimulationService {

    private final ReceivablePricingStrategyResolver resolver;

    public PricingSimulationService(ReceivablePricingStrategyResolver resolver) {
        this.resolver = resolver;
    }

    public PricingResult simulate(PricingRequest request) {
        PricingStrategy strategy = resolver.resolve(request.getReceivableTypeCode());
        return strategy.calculate(request);
    }
}

