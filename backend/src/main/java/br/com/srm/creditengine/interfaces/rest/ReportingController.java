package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.reporting.settlement.SettlementReportPage;
import br.com.srm.creditengine.reporting.settlement.SettlementReportQuery;
import br.com.srm.creditengine.reporting.settlement.SettlementReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports/settlements")
public class ReportingController {

    private final SettlementReportService reportingService;

    public ReportingController(SettlementReportService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping
    public ResponseEntity<SettlementReportPage> findSettlements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID assignorId,
            @RequestParam(required = false) CurrencyCode currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SettlementReportQuery query = new SettlementReportQuery(from, to, assignorId, currency, page, size);
        SettlementReportPage result = reportingService.findSettlements(query);
        return ResponseEntity.ok(result);
    }
}
