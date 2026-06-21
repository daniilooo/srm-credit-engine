package br.com.srm.creditengine.domain.currency;

import java.util.Optional;

public interface ExchangeRateProvider {
    Optional<ExchangeRateResult> findLatest(CurrencyCode base, CurrencyCode quote);
}
