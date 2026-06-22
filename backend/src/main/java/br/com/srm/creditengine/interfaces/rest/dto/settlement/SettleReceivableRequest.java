package br.com.srm.creditengine.interfaces.rest.dto.settlement;

import br.com.srm.creditengine.domain.currency.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record SettleReceivableRequest(
        @NotNull UUID receivableId,
        @NotNull CurrencyCode paymentCurrencyCode,
        @NotNull @DecimalMin("0.000001") BigDecimal baseTaxMonthly
) {}
