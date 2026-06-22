package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.application.settlement.SettleReceivableCommand;
import br.com.srm.creditengine.application.settlement.SettleReceivableUseCase;
import br.com.srm.creditengine.interfaces.rest.dto.settlement.SettleReceivableRequest;
import br.com.srm.creditengine.interfaces.rest.dto.settlement.SettleReceivableResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final SettleReceivableUseCase settleReceivableUseCase;

    public SettlementController(SettleReceivableUseCase settleReceivableUseCase) {
        this.settleReceivableUseCase = settleReceivableUseCase;
    }

    @PostMapping
    public ResponseEntity<SettleReceivableResponse> settle(
            @Valid @RequestBody SettleReceivableRequest request) {
        SettleReceivableCommand command = new SettleReceivableCommand(
                request.receivableId(),
                request.paymentCurrencyCode(),
                request.baseTaxMonthly());

        UUID settlementId = settleReceivableUseCase.execute(command);

        return ResponseEntity.created(URI.create("/api/v1/settlements/" + settlementId))
                .body(new SettleReceivableResponse(settlementId));
    }
}
