package br.com.srm.creditengine.reporting.settlement;

import br.com.srm.creditengine.infrastructure.observability.BusinessMetrics;
import io.micrometer.core.instrument.Timer;

public class SettlementReportService {

    private final SettlementReportRepository repository;
    private final BusinessMetrics metrics;

    public SettlementReportService(SettlementReportRepository repository, BusinessMetrics metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }

    public SettlementReportPage findSettlements(SettlementReportQuery query) {
        if (query.page() < 0) {
            throw new InvalidReportPeriodException(
                    "page must not be negative, got: " + query.page());
        }
        if (query.size() < 1 || query.size() > 100) {
            throw new InvalidReportPeriodException(
                    "size must be between 1 and 100, got: " + query.size());
        }
        if (query.from() != null && query.to() != null && query.from().isAfter(query.to())) {
            throw new InvalidReportPeriodException(
                    "'from' must not be after 'to': from=" + query.from() + ", to=" + query.to());
        }
        metrics.incrementReportsSettlementQueriesTotal();
        Timer.Sample sample = Timer.start();
        try {
            return repository.findAll(query);
        } finally {
            sample.stop(metrics.getReportSettlementQueryDuration());
        }
    }
}
