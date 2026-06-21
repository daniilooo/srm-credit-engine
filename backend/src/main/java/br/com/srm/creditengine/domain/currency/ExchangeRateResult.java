package br.com.srm.creditengine.domain.currency;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ExchangeRateResult(
        CurrencyCode baseCurrency,
        CurrencyCode quoteCurrency,
        BigDecimal rateValue,
        OffsetDateTime validFrom,
        OffsetDateTime capturedAt
) {}
