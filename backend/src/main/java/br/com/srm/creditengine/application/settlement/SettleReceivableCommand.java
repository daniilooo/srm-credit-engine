package br.com.srm.creditengine.application.settlement;

import br.com.srm.creditengine.domain.currency.CurrencyCode;

import java.math.BigDecimal;
import java.util.UUID;

public record SettleReceivableCommand(
        UUID receivableId,
        CurrencyCode paymentCurrencyCode,
        BigDecimal baseTaxMonthly
) {}
