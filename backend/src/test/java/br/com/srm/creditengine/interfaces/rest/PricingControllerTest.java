package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.application.pricing.PricingSimulationService;
import br.com.srm.creditengine.domain.pricing.PricingException;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PricingController.class)
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PricingSimulationService pricingSimulationService;

    private static final String FUTURE_DATE = LocalDate.now().plusDays(60).toString();

    @Test
    void simulate_returns200_withValidRequest() throws Exception {
        when(pricingSimulationService.simulate(any())).thenReturn(new PricingResult(
                new BigDecimal("9500.0000"),
                new BigDecimal("0.0100"),
                new BigDecimal("0.015000"),
                2));

        mockMvc.perform(post("/api/v1/pricing/simulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "faceValue": 10000.00,
                                  "dueDate": "%s",
                                  "receivableType": "DUPLICATA",
                                  "baseTaxMonthly": 0.0100
                                }
                                """.formatted(FUTURE_DATE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.presentValue").isNumber())
                .andExpect(jsonPath("$.appliedTax").isNumber())
                .andExpect(jsonPath("$.termInMonths").value(2));
    }

    @Test
    void simulate_returns400_whenFaceValueIsNull() throws Exception {
        mockMvc.perform(post("/api/v1/pricing/simulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dueDate": "%s",
                                  "receivableType": "DUPLICATA",
                                  "baseTaxMonthly": 0.0100
                                }
                                """.formatted(FUTURE_DATE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/pricing/simulations"));
    }

    @Test
    void simulate_returns400_whenDueDateIsInPast() throws Exception {
        mockMvc.perform(post("/api/v1/pricing/simulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "faceValue": 10000.00,
                                  "dueDate": "2020-01-01",
                                  "receivableType": "DUPLICATA",
                                  "baseTaxMonthly": 0.0100
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void simulate_returns422_whenPricingExceptionThrown() throws Exception {
        when(pricingSimulationService.simulate(any())).thenThrow(new PricingException("No pricing strategy for type: UNKNOWN"));

        mockMvc.perform(post("/api/v1/pricing/simulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "faceValue": 10000.00,
                                  "dueDate": "%s",
                                  "receivableType": "UNKNOWN",
                                  "baseTaxMonthly": 0.0100
                                }
                                """.formatted(FUTURE_DATE)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("No pricing strategy for type: UNKNOWN"));
    }
}
