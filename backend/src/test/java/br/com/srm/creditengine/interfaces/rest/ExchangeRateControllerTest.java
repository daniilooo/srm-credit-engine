package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.application.currency.FindLatestExchangeRateUseCase;
import br.com.srm.creditengine.application.currency.RegisterExchangeRateUseCase;
import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.CurrencyConversionException;
import br.com.srm.creditengine.domain.currency.ExchangeRateNotFoundException;
import br.com.srm.creditengine.domain.currency.ExchangeRateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterExchangeRateUseCase registerExchangeRateUseCase;

    @MockitoBean
    private FindLatestExchangeRateUseCase findLatestExchangeRateUseCase;

    private static final String VALID_REGISTER_BODY = """
            {
              "baseCurrency": "BRL",
              "quoteCurrency": "USD",
              "rateValue": 0.2000000000,
              "validFrom": "2026-06-21T10:00:00Z"
            }
            """;

    @Test
    void register_returns201_withValidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/exchange-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REGISTER_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.baseCurrencyCode").value("BRL"))
                .andExpect(jsonPath("$.quoteCurrencyCode").value("USD"))
                .andExpect(jsonPath("$.rateValue").isNumber());
    }

    @Test
    void register_returns400_whenCurrencyConversionException() throws Exception {
        doThrow(new CurrencyConversionException("Base and quote currencies must be different: BRL"))
                .when(registerExchangeRateUseCase).execute(any(), any(), any(), any());

        mockMvc.perform(post("/api/v1/exchange-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REGISTER_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Base and quote currencies must be different: BRL"));
    }

    @Test
    void register_returns400_whenRateValueIsNull() throws Exception {
        mockMvc.perform(post("/api/v1/exchange-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "baseCurrency": "BRL",
                                  "quoteCurrency": "USD",
                                  "validFrom": "2026-06-21T10:00:00Z"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findLatest_returns200_withValidPair() throws Exception {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        when(findLatestExchangeRateUseCase.execute(CurrencyCode.BRL, CurrencyCode.USD))
                .thenReturn(new ExchangeRateResult(
                        CurrencyCode.BRL, CurrencyCode.USD,
                        new BigDecimal("5.2500000000"),
                        now.minusDays(1), now));

        mockMvc.perform(get("/api/v1/exchange-rates/latest")
                        .param("base", "BRL")
                        .param("quote", "USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseCurrencyCode").value("BRL"))
                .andExpect(jsonPath("$.quoteCurrencyCode").value("USD"))
                .andExpect(jsonPath("$.rateValue").isNumber());
    }

    @Test
    void findLatest_returns404_whenExchangeRateNotFound() throws Exception {
        when(findLatestExchangeRateUseCase.execute(CurrencyCode.BRL, CurrencyCode.USD))
                .thenThrow(new ExchangeRateNotFoundException(CurrencyCode.BRL, CurrencyCode.USD));

        mockMvc.perform(get("/api/v1/exchange-rates/latest")
                        .param("base", "BRL")
                        .param("quote", "USD"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No exchange rate found for pair: BRL -> USD"));
    }

    @Test
    void findLatest_returns400_whenMissingParam() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/latest")
                        .param("quote", "USD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findLatest_returns400_whenInvalidEnumParam() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/latest")
                        .param("base", "INVALID")
                        .param("quote", "USD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid value 'INVALID' for parameter 'base'"));
    }
}
