package br.com.srm.creditengine.domain.currency;

import java.util.Objects;

public class ExchangeRateLookupService {

    private final ExchangeRateProvider provider;

    public ExchangeRateLookupService(ExchangeRateProvider provider) {
        this.provider = Objects.requireNonNull(provider, "provider");
    }

    public ExchangeRateResult findLatest(CurrencyCode base, CurrencyCode quote) {
        Objects.requireNonNull(base, "base currency");
        Objects.requireNonNull(quote, "quote currency");
        if (base == quote) {
            throw new CurrencyConversionException(
                    "Base and quote currencies must be different: " + base);
        }
        return provider.findLatest(base, quote)
                .orElseThrow(() -> new ExchangeRateNotFoundException(base, quote));
    }
}
