package br.com.srm.creditengine.infrastructure.config;

import br.com.srm.creditengine.application.currency.FindLatestExchangeRateUseCase;
import br.com.srm.creditengine.application.currency.RegisterExchangeRateUseCase;
import br.com.srm.creditengine.domain.currency.ExchangeRateLookupService;
import br.com.srm.creditengine.domain.currency.ExchangeRateProvider;
import br.com.srm.creditengine.infrastructure.observability.BusinessMetrics;
import br.com.srm.creditengine.infrastructure.persistence.jpa.CurrencyRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.ExchangeRateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfiguration {

    @Bean
    public ExchangeRateLookupService exchangeRateLookupService(ExchangeRateProvider exchangeRateProvider) {
        return new ExchangeRateLookupService(exchangeRateProvider);
    }

    @Bean
    public RegisterExchangeRateUseCase registerExchangeRateUseCase(
            CurrencyRepository currencyRepository,
            ExchangeRateRepository exchangeRateRepository,
            BusinessMetrics businessMetrics) {
        return new RegisterExchangeRateUseCase(currencyRepository, exchangeRateRepository, businessMetrics);
    }

    @Bean
    public FindLatestExchangeRateUseCase findLatestExchangeRateUseCase(
            ExchangeRateLookupService exchangeRateLookupService) {
        return new FindLatestExchangeRateUseCase(exchangeRateLookupService);
    }
}
