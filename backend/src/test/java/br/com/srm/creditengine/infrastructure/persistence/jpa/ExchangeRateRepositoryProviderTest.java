package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.currency.Currency;
import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.ExchangeRate;
import br.com.srm.creditengine.domain.currency.ExchangeRateResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeRateRepositoryProviderTest {

    @Test
    void findLatest_mapsEntityToResult_whenEntityFound() {
        ExchangeRateRepository repo = mock(ExchangeRateRepository.class);
        when(repo.findLatest(any(), any())).thenCallRealMethod();

        Currency brl = currencyWith(CurrencyCode.BRL);
        Currency usd = currencyWith(CurrencyCode.USD);

        ExchangeRate rate = new ExchangeRate();
        rate.setBaseCurrency(brl);
        rate.setQuoteCurrency(usd);
        rate.setRateValue(new BigDecimal("5.2500000000"));
        rate.setValidFrom(OffsetDateTime.now().minusHours(1));
        rate.setCreatedAt(OffsetDateTime.now());

        when(repo.findFirstByBaseCurrency_CodeAndQuoteCurrency_CodeOrderByValidFromDescCreatedAtDesc(
                CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(Optional.of(rate));

        Optional<ExchangeRateResult> result = repo.findLatest(CurrencyCode.BRL, CurrencyCode.USD);

        assertTrue(result.isPresent());
        assertEquals(CurrencyCode.BRL, result.get().baseCurrency());
        assertEquals(CurrencyCode.USD, result.get().quoteCurrency());
        assertEquals(new BigDecimal("5.2500000000"), result.get().rateValue());
        assertNotNull(result.get().validFrom());
        assertNotNull(result.get().capturedAt());
    }

    @Test
    void findLatest_returnsEmpty_whenNoEntityFound() {
        ExchangeRateRepository repo = mock(ExchangeRateRepository.class);
        when(repo.findLatest(any(), any())).thenCallRealMethod();

        when(repo.findFirstByBaseCurrency_CodeAndQuoteCurrency_CodeOrderByValidFromDescCreatedAtDesc(
                CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(Optional.empty());

        Optional<ExchangeRateResult> result = repo.findLatest(CurrencyCode.BRL, CurrencyCode.USD);

        assertTrue(result.isEmpty());
    }

    @Test
    void findLatest_capturedAt_matchesEntityCreatedAt() {
        ExchangeRateRepository repo = mock(ExchangeRateRepository.class);
        when(repo.findLatest(any(), any())).thenCallRealMethod();

        OffsetDateTime createdAt = OffsetDateTime.now().minusMinutes(30);

        ExchangeRate rate = new ExchangeRate();
        rate.setBaseCurrency(currencyWith(CurrencyCode.USD));
        rate.setQuoteCurrency(currencyWith(CurrencyCode.BRL));
        rate.setRateValue(new BigDecimal("0.1905000000"));
        rate.setValidFrom(OffsetDateTime.now().minusDays(1));
        rate.setCreatedAt(createdAt);

        when(repo.findFirstByBaseCurrency_CodeAndQuoteCurrency_CodeOrderByValidFromDescCreatedAtDesc(
                CurrencyCode.USD, CurrencyCode.BRL)).thenReturn(Optional.of(rate));

        Optional<ExchangeRateResult> result = repo.findLatest(CurrencyCode.USD, CurrencyCode.BRL);

        assertTrue(result.isPresent());
        assertEquals(createdAt, result.get().capturedAt());
    }

    private Currency currencyWith(CurrencyCode code) {
        Currency c = new Currency();
        c.setCode(code);
        return c;
    }
}
