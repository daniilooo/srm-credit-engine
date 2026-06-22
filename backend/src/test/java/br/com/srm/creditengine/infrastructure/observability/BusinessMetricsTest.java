package br.com.srm.creditengine.infrastructure.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessMetricsTest {

    private SimpleMeterRegistry registry;
    private BusinessMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new BusinessMetrics(registry);
    }

    @Test
    void incrementPricingSimulationsTotal_incrementsCounter() {
        metrics.incrementPricingSimulationsTotal();
        Counter counter = registry.find("pricing.simulations.total").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void incrementPricingSimulationsTotal_accumulatesMultipleCalls() {
        metrics.incrementPricingSimulationsTotal();
        metrics.incrementPricingSimulationsTotal();
        metrics.incrementPricingSimulationsTotal();
        assertEquals(3.0, registry.find("pricing.simulations.total").counter().count());
    }

    @Test
    void incrementExchangeRatesRegisteredTotal_incrementsCounter() {
        metrics.incrementExchangeRatesRegisteredTotal();
        Counter counter = registry.find("exchange.rates.registered.total").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void incrementSettlementsCreatedTotal_incrementsCounter() {
        metrics.incrementSettlementsCreatedTotal();
        Counter counter = registry.find("settlements.created.total").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void incrementSettlementsFailedTotal_incrementsCounter() {
        metrics.incrementSettlementsFailedTotal();
        Counter counter = registry.find("settlements.failed.total").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void incrementReportsSettlementQueriesTotal_incrementsCounter() {
        metrics.incrementReportsSettlementQueriesTotal();
        Counter counter = registry.find("reports.settlement.queries.total").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void getPricingSimulationDuration_returnsRegisteredTimer() {
        Timer timer = metrics.getPricingSimulationDuration();
        assertNotNull(timer);
        assertSame(timer, registry.find("pricing.simulation.duration").timer());
    }

    @Test
    void getSettlementExecutionDuration_returnsRegisteredTimer() {
        Timer timer = metrics.getSettlementExecutionDuration();
        assertNotNull(timer);
        assertSame(timer, registry.find("settlement.execution.duration").timer());
    }

    @Test
    void getReportSettlementQueryDuration_returnsRegisteredTimer() {
        Timer timer = metrics.getReportSettlementQueryDuration();
        assertNotNull(timer);
        assertSame(timer, registry.find("report.settlement.query.duration").timer());
    }
}
