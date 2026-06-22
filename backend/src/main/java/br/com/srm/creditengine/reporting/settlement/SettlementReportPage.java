package br.com.srm.creditengine.reporting.settlement;

import java.util.List;

public record SettlementReportPage(
        List<SettlementReportItem> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
