package br.com.srm.creditengine.interfaces.rest.dto.currency;

import br.com.srm.creditengine.domain.currency.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RegisterExchangeRateRequest(
        @NotNull CurrencyCode baseCurrency,
        @NotNull CurrencyCode quoteCurrency,
        @NotNull @DecimalMin("0.000001") BigDecimal rateValue,
        @NotNull OffsetDateTime validFrom
) {}
