package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.application.settlement.SettleReceivableUseCase;
import br.com.srm.creditengine.domain.receivable.ReceivableNotFoundException;
import br.com.srm.creditengine.domain.settlement.SettlementNotAllowedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SettlementController.class)
class SettlementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SettleReceivableUseCase settleReceivableUseCase;

    private static final UUID RECEIVABLE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private static final String VALID_BODY = """
            {
              "receivableId": "550e8400-e29b-41d4-a716-446655440000",
              "paymentCurrencyCode": "BRL",
              "baseTaxMonthly": 0.0100
            }
            """;

    @Test
    void settle_returns201_withValidRequest() throws Exception {
        UUID settlementId = UUID.randomUUID();
        when(settleReceivableUseCase.execute(any())).thenReturn(settlementId);

        mockMvc.perform(post("/api/v1/settlements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.settlementId").value(settlementId.toString()));
    }

    @Test
    void settle_returns404_whenReceivableNotFound() throws Exception {
        when(settleReceivableUseCase.execute(any()))
                .thenThrow(new ReceivableNotFoundException(RECEIVABLE_ID));

        mockMvc.perform(post("/api/v1/settlements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Receivable not found: " + RECEIVABLE_ID))
                .andExpect(jsonPath("$.path").value("/api/v1/settlements"));
    }

    @Test
    void settle_returns409_whenSettlementNotAllowed() throws Exception {
        when(settleReceivableUseCase.execute(any()))
                .thenThrow(new SettlementNotAllowedException("Receivable is not eligible for settlement. Current status: SETTLED"));

        mockMvc.perform(post("/api/v1/settlements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void settle_returns400_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/settlements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentCurrencyCode": "BRL",
                                  "baseTaxMonthly": 0.0100
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
