package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.application.currency.FindLatestExchangeRateUseCase;
import br.com.srm.creditengine.application.currency.RegisterExchangeRateUseCase;
import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.ExchangeRateResult;
import br.com.srm.creditengine.interfaces.rest.dto.currency.ExchangeRateResponse;
import br.com.srm.creditengine.interfaces.rest.dto.currency.RegisterExchangeRateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final RegisterExchangeRateUseCase registerExchangeRateUseCase;
    private final FindLatestExchangeRateUseCase findLatestExchangeRateUseCase;

    public ExchangeRateController(RegisterExchangeRateUseCase registerExchangeRateUseCase,
                                   FindLatestExchangeRateUseCase findLatestExchangeRateUseCase) {
        this.registerExchangeRateUseCase = registerExchangeRateUseCase;
        this.findLatestExchangeRateUseCase = findLatestExchangeRateUseCase;
    }

    @PostMapping
    public ResponseEntity<ExchangeRateResponse> register(
            @Valid @RequestBody RegisterExchangeRateRequest request) {
        registerExchangeRateUseCase.execute(
                request.baseCurrency(),
                request.quoteCurrency(),
                request.rateValue(),
                request.validFrom());

        ExchangeRateResponse response = new ExchangeRateResponse(
                request.baseCurrency().name(),
                request.quoteCurrency().name(),
                request.rateValue(),
                request.validFrom(),
                request.validFrom());

        return ResponseEntity.created(URI.create("/api/v1/exchange-rates/latest"
                + "?base=" + request.baseCurrency()
                + "&quote=" + request.quoteCurrency()))
                .body(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<ExchangeRateResponse> findLatest(
            @RequestParam CurrencyCode base,
            @RequestParam CurrencyCode quote) {
        ExchangeRateResult result = findLatestExchangeRateUseCase.execute(base, quote);
        return ResponseEntity.ok(new ExchangeRateResponse(
                result.baseCurrency().name(),
                result.quoteCurrency().name(),
                result.rateValue(),
                result.validFrom(),
                result.capturedAt()));
    }
}
