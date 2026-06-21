package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.ExchangeRate;
import br.com.srm.creditengine.domain.currency.ExchangeRateProvider;
import br.com.srm.creditengine.domain.currency.ExchangeRateResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID>, ExchangeRateProvider {

    Optional<ExchangeRate> findFirstByBaseCurrency_CodeAndQuoteCurrency_CodeOrderByValidFromDescCreatedAtDesc(
            CurrencyCode base, CurrencyCode quote);

    @Override
    default Optional<ExchangeRateResult> findLatest(CurrencyCode base, CurrencyCode quote) {
        return findFirstByBaseCurrency_CodeAndQuoteCurrency_CodeOrderByValidFromDescCreatedAtDesc(base, quote)
                .map(e -> new ExchangeRateResult(
                        e.getBaseCurrency().getCode(),
                        e.getQuoteCurrency().getCode(),
                        e.getRateValue(),
                        e.getValidFrom(),
                        e.getCreatedAt()));
    }
}
