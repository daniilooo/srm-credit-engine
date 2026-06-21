package br.com.srm.creditengine.application.currency;

import br.com.srm.creditengine.domain.currency.Currency;
import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.CurrencyConversionException;
import br.com.srm.creditengine.domain.currency.ExchangeRate;
import br.com.srm.creditengine.infrastructure.persistence.jpa.CurrencyRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.ExchangeRateRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public class RegisterExchangeRateUseCase {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public RegisterExchangeRateUseCase(CurrencyRepository currencyRepository,
                                       ExchangeRateRepository exchangeRateRepository) {
        this.currencyRepository = Objects.requireNonNull(currencyRepository, "currencyRepository");
        this.exchangeRateRepository = Objects.requireNonNull(exchangeRateRepository, "exchangeRateRepository");
    }

    public void execute(CurrencyCode baseCode, CurrencyCode quoteCode,
                        BigDecimal rate, OffsetDateTime validFrom) {
        Objects.requireNonNull(baseCode, "base currency");
        Objects.requireNonNull(quoteCode, "quote currency");
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(validFrom, "validFrom");

        if (baseCode == quoteCode) {
            throw new CurrencyConversionException(
                    "Base and quote currencies must be different: " + baseCode);
        }
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CurrencyConversionException(
                    "Exchange rate must be positive, but was: " + rate);
        }

        Currency base = currencyRepository.findByCode(baseCode)
                .orElseThrow(() -> new CurrencyConversionException("Currency not found: " + baseCode));
        Currency quote = currencyRepository.findByCode(quoteCode)
                .orElseThrow(() -> new CurrencyConversionException("Currency not found: " + quoteCode));

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(base);
        exchangeRate.setQuoteCurrency(quote);
        exchangeRate.setRateValue(rate);
        exchangeRate.setValidFrom(validFrom);

        exchangeRateRepository.save(exchangeRate);
    }
}
