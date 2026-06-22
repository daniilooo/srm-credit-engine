package br.com.srm.creditengine.infrastructure.config;

import br.com.srm.creditengine.domain.currency.ExchangeRateLookupService;
import br.com.srm.creditengine.domain.currency.ExchangeRateProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfiguration {

    @Bean
    public ExchangeRateLookupService exchangeRateLookupService(ExchangeRateProvider exchangeRateProvider) {
        return new ExchangeRateLookupService(exchangeRateProvider);
    }
}
