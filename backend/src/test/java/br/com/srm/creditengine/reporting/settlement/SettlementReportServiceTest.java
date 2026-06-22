package br.com.srm.creditengine.reporting.settlement;

import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.infrastructure.observability.BusinessMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementReportServiceTest {

    @Mock
    private SettlementReportRepository repository;

    private SettlementReportService service;
    private static final BusinessMetrics METRICS = new BusinessMetrics(new SimpleMeterRegistry());
    private static final SettlementReportPage EMPTY_PAGE =
            new SettlementReportPage(List.of(), 0, 20, 0L, 0);

    @BeforeEach
    void setUp() {
        service = new SettlementReportService(repository, METRICS);
    }

    @Test
    void findSettlements_delegatesToRepository_whenNoFilters() {
        when(repository.findAll(any())).thenReturn(EMPTY_PAGE);

        SettlementReportQuery query = new SettlementReportQuery(null, null, null, null, 0, 20);
        SettlementReportPage result = service.findSettlements(query);

        verify(repository).findAll(query);
        assertEquals(0L, result.totalElements());
    }

    @Test
    void findSettlements_delegatesToRepository_whenAllFiltersProvided() {
        when(repository.findAll(any())).thenReturn(EMPTY_PAGE);

        SettlementReportQuery query = new SettlementReportQuery(
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30),
                UUID.randomUUID(), CurrencyCode.BRL, 0, 20);

        service.findSettlements(query);

        verify(repository).findAll(query);
    }

    @Test
    void findSettlements_passes_whenFromEqualsTo() {
        when(repository.findAll(any())).thenReturn(EMPTY_PAGE);

        LocalDate date = LocalDate.of(2026, 6, 15);
        SettlementReportQuery query = new SettlementReportQuery(date, date, null, null, 0, 20);

        service.findSettlements(query);

        verify(repository).findAll(query);
    }

    @Test
    void findSettlements_throwsInvalidReportPeriodException_whenFromIsAfterTo() {
        SettlementReportQuery query = new SettlementReportQuery(
                LocalDate.of(2026, 6, 30), LocalDate.of(2026, 6, 1),
                null, null, 0, 20);

        InvalidReportPeriodException ex = assertThrows(
                InvalidReportPeriodException.class,
                () -> service.findSettlements(query));

        assertEquals("'from' must not be after 'to': from=2026-06-30, to=2026-06-01", ex.getMessage());
    }

    @Test
    void findSettlements_throwsInvalidReportPeriodException_whenPageIsNegative() {
        SettlementReportQuery query = new SettlementReportQuery(null, null, null, null, -1, 20);

        assertThrows(InvalidReportPeriodException.class, () -> service.findSettlements(query));
    }

    @Test
    void findSettlements_throwsInvalidReportPeriodException_whenSizeIsZero() {
        SettlementReportQuery query = new SettlementReportQuery(null, null, null, null, 0, 0);

        assertThrows(InvalidReportPeriodException.class, () -> service.findSettlements(query));
    }

    @Test
    void findSettlements_throwsInvalidReportPeriodException_whenSizeExceedsMax() {
        SettlementReportQuery query = new SettlementReportQuery(null, null, null, null, 0, 101);

        InvalidReportPeriodException ex = assertThrows(
                InvalidReportPeriodException.class,
                () -> service.findSettlements(query));

        assertEquals("size must be between 1 and 100, got: 101", ex.getMessage());
    }
}
