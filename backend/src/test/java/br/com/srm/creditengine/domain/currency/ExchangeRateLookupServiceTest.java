package br.com.srm.creditengine.domain.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateLookupServiceTest {

    @Mock
    private ExchangeRateProvider provider;

    private ExchangeRateLookupService service;

    @BeforeEach
    void setUp() {
        service = new ExchangeRateLookupService(provider);
    }

    @Test
    void findLatest_returnsResult_whenRateExists() {
        ExchangeRateResult expected = new ExchangeRateResult(
                CurrencyCode.BRL, CurrencyCode.USD,
                new BigDecimal("5.2500000000"),
                OffsetDateTime.now().minusHours(1),
                OffsetDateTime.now());
        when(provider.findLatest(CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(Optional.of(expected));

        ExchangeRateResult result = service.findLatest(CurrencyCode.BRL, CurrencyCode.USD);

        assertNotNull(result);
        assertEquals(CurrencyCode.BRL, result.baseCurrency());
        assertEquals(CurrencyCode.USD, result.quoteCurrency());
        assertEquals(new BigDecimal("5.2500000000"), result.rateValue());
    }

    @Test
    void findLatest_throwsExchangeRateNotFoundException_whenNoRateExists() {
        when(provider.findLatest(CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(Optional.empty());

        ExchangeRateNotFoundException ex = assertThrows(ExchangeRateNotFoundException.class,
                () -> service.findLatest(CurrencyCode.BRL, CurrencyCode.USD));

        assertTrue(ex.getMessage().contains("BRL"));
        assertTrue(ex.getMessage().contains("USD"));
    }

    @Test
    void findLatest_throwsCurrencyConversionException_whenSameCurrency() {
        CurrencyConversionException ex = assertThrows(CurrencyConversionException.class,
                () -> service.findLatest(CurrencyCode.BRL, CurrencyCode.BRL));

        assertTrue(ex.getMessage().contains("BRL"));
        verifyNoInteractions(provider);
    }

    @Test
    void findLatest_doesNotReturnInversePair_whenOnlyInverseExists() {
        // USD->BRL might exist, but BRL->USD is not found
        when(provider.findLatest(CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(Optional.empty());

        assertThrows(ExchangeRateNotFoundException.class,
                () -> service.findLatest(CurrencyCode.BRL, CurrencyCode.USD));

        verify(provider).findLatest(CurrencyCode.BRL, CurrencyCode.USD);
        verify(provider, never()).findLatest(CurrencyCode.USD, CurrencyCode.BRL);
    }

    @Test
    void findLatest_usdToBrl_returnsCorrectResult() {
        ExchangeRateResult expected = new ExchangeRateResult(
                CurrencyCode.USD, CurrencyCode.BRL,
                new BigDecimal("0.1905000000"),
                OffsetDateTime.now().minusHours(2),
                OffsetDateTime.now());
        when(provider.findLatest(CurrencyCode.USD, CurrencyCode.BRL)).thenReturn(Optional.of(expected));

        ExchangeRateResult result = service.findLatest(CurrencyCode.USD, CurrencyCode.BRL);

        assertEquals(CurrencyCode.USD, result.baseCurrency());
        assertEquals(CurrencyCode.BRL, result.quoteCurrency());
    }
}
