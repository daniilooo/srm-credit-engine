package br.com.srm.creditengine.reporting.settlement;

import br.com.srm.creditengine.domain.currency.CurrencyCode;

import java.time.LocalDate;
import java.util.UUID;

public record SettlementReportQuery(
        LocalDate from,
        LocalDate to,
        UUID assignorId,
        CurrencyCode currency,
        int page,
        int size
) {}
