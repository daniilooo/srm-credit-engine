package br.com.srm.creditengine.application.currency;

import br.com.srm.creditengine.domain.currency.Currency;
import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.CurrencyConversionException;
import br.com.srm.creditengine.infrastructure.observability.BusinessMetrics;
import br.com.srm.creditengine.infrastructure.persistence.jpa.CurrencyRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.ExchangeRateRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterExchangeRateUseCaseTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private RegisterExchangeRateUseCase useCase;
    private static final BusinessMetrics METRICS = new BusinessMetrics(new SimpleMeterRegistry());

    @BeforeEach
    void setUp() {
        useCase = new RegisterExchangeRateUseCase(currencyRepository, exchangeRateRepository, METRICS);
    }

    @Test
    void execute_registersExchangeRate_whenInputIsValid() {
        Currency brl = currencyWith(CurrencyCode.BRL);
        Currency usd = currencyWith(CurrencyCode.USD);
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(brl));
        when(currencyRepository.findByCode(CurrencyCode.USD)).thenReturn(Optional.of(usd));
        when(exchangeRateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> useCase.execute(
                CurrencyCode.BRL, CurrencyCode.USD,
                new BigDecimal("5.2500000000"),
                OffsetDateTime.now()));

        verify(exchangeRateRepository).save(any());
    }

    @Test
    void execute_throwsCurrencyConversionException_whenRateIsZero() {
        CurrencyConversionException ex = assertThrows(CurrencyConversionException.class,
                () -> useCase.execute(CurrencyCode.BRL, CurrencyCode.USD, BigDecimal.ZERO, OffsetDateTime.now()));

        assertTrue(ex.getMessage().contains("positive"));
        verifyNoInteractions(currencyRepository);
        verifyNoInteractions(exchangeRateRepository);
    }

    @Test
    void execute_throwsCurrencyConversionException_whenRateIsNegative() {
        CurrencyConversionException ex = assertThrows(CurrencyConversionException.class,
                () -> useCase.execute(CurrencyCode.BRL, CurrencyCode.USD,
                        new BigDecimal("-0.01"), OffsetDateTime.now()));

        assertTrue(ex.getMessage().contains("positive"));
        verifyNoInteractions(currencyRepository);
        verifyNoInteractions(exchangeRateRepository);
    }

    @Test
    void execute_throwsCurrencyConversionException_whenSameCurrency() {
        CurrencyConversionException ex = assertThrows(CurrencyConversionException.class,
                () -> useCase.execute(CurrencyCode.BRL, CurrencyCode.BRL,
                        new BigDecimal("1.00"), OffsetDateTime.now()));

        assertTrue(ex.getMessage().contains("BRL"));
        verifyNoInteractions(currencyRepository);
        verifyNoInteractions(exchangeRateRepository);
    }

    @Test
    void execute_throwsCurrencyConversionException_whenBaseCurrencyNotFound() {
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.empty());

        CurrencyConversionException ex = assertThrows(CurrencyConversionException.class,
                () -> useCase.execute(CurrencyCode.BRL, CurrencyCode.USD,
                        new BigDecimal("5.25"), OffsetDateTime.now()));

        assertTrue(ex.getMessage().contains("BRL"));
        verifyNoInteractions(exchangeRateRepository);
    }

    @Test
    void execute_throwsCurrencyConversionException_whenQuoteCurrencyNotFound() {
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(currencyWith(CurrencyCode.BRL)));
        when(currencyRepository.findByCode(CurrencyCode.USD)).thenReturn(Optional.empty());

        CurrencyConversionException ex = assertThrows(CurrencyConversionException.class,
                () -> useCase.execute(CurrencyCode.BRL, CurrencyCode.USD,
                        new BigDecimal("5.25"), OffsetDateTime.now()));

        assertTrue(ex.getMessage().contains("USD"));
        verifyNoInteractions(exchangeRateRepository);
    }

    @Test
    void execute_usdToBrl_registersDistinctPair() {
        Currency usd = currencyWith(CurrencyCode.USD);
        Currency brl = currencyWith(CurrencyCode.BRL);
        when(currencyRepository.findByCode(CurrencyCode.USD)).thenReturn(Optional.of(usd));
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(brl));
        when(exchangeRateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> useCase.execute(
                CurrencyCode.USD, CurrencyCode.BRL,
                new BigDecimal("0.1905000000"),
                OffsetDateTime.now()));

        verify(exchangeRateRepository).save(any());
    }

    private Currency currencyWith(CurrencyCode code) {
        Currency c = new Currency();
        c.setCode(code);
        return c;
    }
}
