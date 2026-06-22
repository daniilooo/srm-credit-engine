package br.com.srm.creditengine.reporting.settlement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SettlementReportItem(
        UUID settlementId,
        UUID receivableId,
        UUID assignorId,
        String assignorName,
        String receivableType,
        BigDecimal faceValue,
        BigDecimal settledAmount,
        String receivableCurrency,
        String paymentCurrency,
        BigDecimal exchangeRateValue,
        String exchangeRateBaseCurrency,
        String exchangeRateQuoteCurrency,
        OffsetDateTime exchangeRateUsedAt,
        String settlementStatus,
        OffsetDateTime settledAt,
        LocalDate dueDate
) {}
