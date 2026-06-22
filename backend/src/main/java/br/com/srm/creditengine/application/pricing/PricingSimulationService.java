package br.com.srm.creditengine.application.pricing;

import br.com.srm.creditengine.domain.pricing.PricingRequest;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import br.com.srm.creditengine.domain.pricing.PricingStrategy;
import br.com.srm.creditengine.domain.pricing.ReceivablePricingStrategyResolver;
import br.com.srm.creditengine.infrastructure.observability.BusinessMetrics;
import io.micrometer.core.instrument.Timer;

public class PricingSimulationService {

    private final ReceivablePricingStrategyResolver resolver;
    private final BusinessMetrics metrics;

    public PricingSimulationService(ReceivablePricingStrategyResolver resolver, BusinessMetrics metrics) {
        this.resolver = resolver;
        this.metrics = metrics;
    }

    public PricingResult simulate(PricingRequest request) {
        Timer.Sample sample = Timer.start();
        try {
            PricingStrategy strategy = resolver.resolve(request.getReceivableTypeCode());
            PricingResult result = strategy.calculate(request);
            metrics.incrementPricingSimulationsTotal();
            return result;
        } finally {
            sample.stop(metrics.getPricingSimulationDuration());
        }
    }
}

