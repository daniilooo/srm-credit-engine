package br.com.srm.creditengine.interfaces.rest;

import br.com.srm.creditengine.application.pricing.PricingSimulationService;
import br.com.srm.creditengine.domain.pricing.PricingRequest;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import br.com.srm.creditengine.interfaces.rest.dto.pricing.PricingSimulationRequest;
import br.com.srm.creditengine.interfaces.rest.dto.pricing.PricingSimulationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private final PricingSimulationService pricingSimulationService;

    public PricingController(PricingSimulationService pricingSimulationService) {
        this.pricingSimulationService = pricingSimulationService;
    }

    @PostMapping("/simulations")
    public ResponseEntity<PricingSimulationResponse> simulate(
            @Valid @RequestBody PricingSimulationRequest request) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), request.dueDate());
        int termInMonths = (int) ((days + 29) / 30);

        PricingRequest pricingRequest = new PricingRequest(
                request.faceValue(),
                request.baseTaxMonthly(),
                termInMonths,
                request.receivableType());
        PricingResult result = pricingSimulationService.simulate(pricingRequest);

        return ResponseEntity.ok(new PricingSimulationResponse(
                result.getPresentValue(),
                result.getAppliedTax(),
                result.getAppliedSpread(),
                result.getTermInMonths()));
    }
}
