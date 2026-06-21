package br.com.srm.creditengine.domain.currency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateResultTest {

    @Test
    void recordHoldsAllFields() {
        OffsetDateTime validFrom = OffsetDateTime.now().minusHours(1);
        OffsetDateTime capturedAt = OffsetDateTime.now();
        BigDecimal rate = new BigDecimal("5.2500000000");

        ExchangeRateResult result = new ExchangeRateResult(
                CurrencyCode.BRL, CurrencyCode.USD, rate, validFrom, capturedAt);

        assertEquals(CurrencyCode.BRL, result.baseCurrency());
        assertEquals(CurrencyCode.USD, result.quoteCurrency());
        assertEquals(new BigDecimal("5.2500000000"), result.rateValue());
        assertEquals(validFrom, result.validFrom());
        assertEquals(capturedAt, result.capturedAt());
    }

    @Test
    void equalRecordsAreEqual() {
        OffsetDateTime validFrom = OffsetDateTime.now().minusHours(1);
        OffsetDateTime capturedAt = OffsetDateTime.now();
        BigDecimal rate = new BigDecimal("5.2500000000");

        ExchangeRateResult r1 = new ExchangeRateResult(
                CurrencyCode.BRL, CurrencyCode.USD, rate, validFrom, capturedAt);
        ExchangeRateResult r2 = new ExchangeRateResult(
                CurrencyCode.BRL, CurrencyCode.USD, rate, validFrom, capturedAt);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}
