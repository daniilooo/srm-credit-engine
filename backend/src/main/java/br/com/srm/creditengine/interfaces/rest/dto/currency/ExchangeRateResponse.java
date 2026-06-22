package br.com.srm.creditengine.interfaces.rest.dto.currency;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ExchangeRateResponse(
        String baseCurrencyCode,
        String quoteCurrencyCode,
        BigDecimal rateValue,
        OffsetDateTime validFrom,
        OffsetDateTime capturedAt
) {}
