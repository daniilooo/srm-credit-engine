package br.com.srm.creditengine.application.currency;

import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.ExchangeRateLookupService;
import br.com.srm.creditengine.domain.currency.ExchangeRateResult;

import java.util.Objects;

public class FindLatestExchangeRateUseCase {

    private final ExchangeRateLookupService lookupService;

    public FindLatestExchangeRateUseCase(ExchangeRateLookupService lookupService) {
        this.lookupService = Objects.requireNonNull(lookupService, "lookupService");
    }

    public ExchangeRateResult execute(CurrencyCode base, CurrencyCode quote) {
        return lookupService.findLatest(base, quote);
    }
}
