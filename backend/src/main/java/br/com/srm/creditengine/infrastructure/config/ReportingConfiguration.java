package br.com.srm.creditengine.infrastructure.config;

import br.com.srm.creditengine.reporting.settlement.SettlementReportRepository;
import br.com.srm.creditengine.reporting.settlement.SettlementReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportingConfiguration {

    @Bean
    public SettlementReportService settlementReportService(SettlementReportRepository reportRepository) {
        return new SettlementReportService(reportRepository);
    }
}
