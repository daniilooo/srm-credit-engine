package br.com.srm.creditengine.infrastructure.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {

    private final Counter pricingSimulationsTotal;
    private final Counter exchangeRatesRegisteredTotal;
    private final Counter settlementsCreatedTotal;
    private final Counter settlementsFailedTotal;
    private final Counter reportsSettlementQueriesTotal;
    private final Timer pricingSimulationDuration;
    private final Timer settlementExecutionDuration;
    private final Timer reportSettlementQueryDuration;

    public BusinessMetrics(MeterRegistry registry) {
        pricingSimulationsTotal = Counter.builder("pricing.simulations.total")
                .description("Total number of pricing simulations executed")
                .register(registry);
        exchangeRatesRegisteredTotal = Counter.builder("exchange.rates.registered.total")
                .description("Total number of exchange rates registered")
                .register(registry);
        settlementsCreatedTotal = Counter.builder("settlements.created.total")
                .description("Total number of successful settlements created")
                .register(registry);
        settlementsFailedTotal = Counter.builder("settlements.failed.total")
                .description("Total number of failed settlement attempts")
                .register(registry);
        reportsSettlementQueriesTotal = Counter.builder("reports.settlement.queries.total")
                .description("Total number of settlement report queries")
                .register(registry);
        pricingSimulationDuration = Timer.builder("pricing.simulation.duration")
                .description("Duration of pricing simulation")
                .register(registry);
        settlementExecutionDuration = Timer.builder("settlement.execution.duration")
                .description("Duration of settlement execution")
                .register(registry);
        reportSettlementQueryDuration = Timer.builder("report.settlement.query.duration")
                .description("Duration of settlement report query")
                .register(registry);
    }

    public void incrementPricingSimulationsTotal() {
        pricingSimulationsTotal.increment();
    }

    public void incrementExchangeRatesRegisteredTotal() {
        exchangeRatesRegisteredTotal.increment();
    }

    public void incrementSettlementsCreatedTotal() {
        settlementsCreatedTotal.increment();
    }

    public void incrementSettlementsFailedTotal() {
        settlementsFailedTotal.increment();
    }

    public void incrementReportsSettlementQueriesTotal() {
        reportsSettlementQueriesTotal.increment();
    }

    public Timer getPricingSimulationDuration() {
        return pricingSimulationDuration;
    }

    public Timer getSettlementExecutionDuration() {
        return settlementExecutionDuration;
    }

    public Timer getReportSettlementQueryDuration() {
        return reportSettlementQueryDuration;
    }
}
