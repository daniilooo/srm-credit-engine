package br.com.srm.creditengine.reporting.settlement;

public interface SettlementReportRepository {

    SettlementReportPage findAll(SettlementReportQuery query);
}
