package br.com.srm.creditengine.application.currency;

import br.com.srm.creditengine.domain.currency.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindLatestExchangeRateUseCaseTest {

    @Mock
    private ExchangeRateLookupService lookupService;

    private FindLatestExchangeRateUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindLatestExchangeRateUseCase(lookupService);
    }

    @Test
    void execute_returnsResult_whenRateExists() {
        ExchangeRateResult expected = new ExchangeRateResult(
                CurrencyCode.BRL, CurrencyCode.USD,
                new BigDecimal("5.2500000000"),
                OffsetDateTime.now().minusHours(1),
                OffsetDateTime.now());
        when(lookupService.findLatest(CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(expected);

        ExchangeRateResult result = useCase.execute(CurrencyCode.BRL, CurrencyCode.USD);

        assertSame(expected, result);
        verify(lookupService).findLatest(CurrencyCode.BRL, CurrencyCode.USD);
    }

    @Test
    void execute_propagatesExchangeRateNotFoundException_whenNoRateFound() {
        when(lookupService.findLatest(CurrencyCode.BRL, CurrencyCode.USD))
                .thenThrow(new ExchangeRateNotFoundException(CurrencyCode.BRL, CurrencyCode.USD));

        assertThrows(ExchangeRateNotFoundException.class,
                () -> useCase.execute(CurrencyCode.BRL, CurrencyCode.USD));
    }

    @Test
    void execute_propagatesCurrencyConversionException_whenSameCurrency() {
        when(lookupService.findLatest(CurrencyCode.BRL, CurrencyCode.BRL))
                .thenThrow(new CurrencyConversionException("same currency"));

        assertThrows(CurrencyConversionException.class,
                () -> useCase.execute(CurrencyCode.BRL, CurrencyCode.BRL));
    }

    @Test
    void execute_usdToBrl_returnsDistinctPairResult() {
        ExchangeRateResult expected = new ExchangeRateResult(
                CurrencyCode.USD, CurrencyCode.BRL,
                new BigDecimal("0.1905000000"),
                OffsetDateTime.now().minusHours(2),
                OffsetDateTime.now());
        when(lookupService.findLatest(CurrencyCode.USD, CurrencyCode.BRL)).thenReturn(expected);

        ExchangeRateResult result = useCase.execute(CurrencyCode.USD, CurrencyCode.BRL);

        assertEquals(CurrencyCode.USD, result.baseCurrency());
        assertEquals(CurrencyCode.BRL, result.quoteCurrency());
    }
}
