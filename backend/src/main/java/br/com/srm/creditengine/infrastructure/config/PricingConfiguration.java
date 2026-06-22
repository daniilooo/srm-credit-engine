package br.com.srm.creditengine.infrastructure.config;

import br.com.srm.creditengine.application.pricing.PricingSimulationService;
import br.com.srm.creditengine.domain.pricing.ReceivablePricingStrategyResolver;
import br.com.srm.creditengine.domain.pricing.strategy.MercantileDuplicatePricingStrategy;
import br.com.srm.creditengine.domain.pricing.strategy.PostDatedCheckPricingStrategy;
import br.com.srm.creditengine.infrastructure.observability.BusinessMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PricingConfiguration {

    @Bean
    public ReceivablePricingStrategyResolver receivablePricingStrategyResolver() {
        return new ReceivablePricingStrategyResolver(List.of(
                new MercantileDuplicatePricingStrategy(),
                new PostDatedCheckPricingStrategy()));
    }

    @Bean
    public PricingSimulationService pricingSimulationService(
            ReceivablePricingStrategyResolver resolver, BusinessMetrics businessMetrics) {
        return new PricingSimulationService(resolver, businessMetrics);
    }
}
