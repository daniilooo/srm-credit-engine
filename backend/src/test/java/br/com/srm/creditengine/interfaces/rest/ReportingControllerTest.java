package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.reporting.settlement.InvalidReportPeriodException;
import br.com.srm.creditengine.reporting.settlement.SettlementReportPage;
import br.com.srm.creditengine.reporting.settlement.SettlementReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportingController.class)
class ReportingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SettlementReportService reportingService;

    private static final SettlementReportPage EMPTY_PAGE =
            new SettlementReportPage(List.of(), 0, 20, 0L, 0);

    @Test
    void findSettlements_returns200_withNoFilters() throws Exception {
        when(reportingService.findSettlements(any())).thenReturn(EMPTY_PAGE);

        mockMvc.perform(get("/api/v1/reports/settlements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void findSettlements_returns200_withAllFilters() throws Exception {
        when(reportingService.findSettlements(any())).thenReturn(EMPTY_PAGE);

        mockMvc.perform(get("/api/v1/reports/settlements")
                        .param("from", "2026-06-01")
                        .param("to", "2026-06-30")
                        .param("assignorId", "550e8400-e29b-41d4-a716-446655440000")
                        .param("currency", "BRL")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findSettlements_returns400_whenServiceThrowsInvalidReportPeriodException() throws Exception {
        when(reportingService.findSettlements(any()))
                .thenThrow(new InvalidReportPeriodException("'from' must not be after 'to'"));

        mockMvc.perform(get("/api/v1/reports/settlements")
                        .param("from", "2026-06-30")
                        .param("to", "2026-06-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("'from' must not be after 'to'"))
                .andExpect(jsonPath("$.path").value("/api/v1/reports/settlements"));
    }

    @Test
    void findSettlements_returns200_withEmptyItems() throws Exception {
        when(reportingService.findSettlements(any())).thenReturn(EMPTY_PAGE);

        mockMvc.perform(get("/api/v1/reports/settlements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void findSettlements_returns400_whenCurrencyParamIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/reports/settlements")
                        .param("currency", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
