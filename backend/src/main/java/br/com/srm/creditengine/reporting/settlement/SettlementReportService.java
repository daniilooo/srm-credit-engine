package br.com.srm.creditengine.reporting.settlement;

public class SettlementReportService {

    private final SettlementReportRepository repository;

    public SettlementReportService(SettlementReportRepository repository) {
        this.repository = repository;
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
        return repository.findAll(query);
    }
}
