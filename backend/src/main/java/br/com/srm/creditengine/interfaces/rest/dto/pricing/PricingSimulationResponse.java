package br.com.srm.creditengine.interfaces.rest.dto.pricing;

import java.math.BigDecimal;

public record PricingSimulationResponse(
        BigDecimal presentValue,
        BigDecimal appliedTax,
        BigDecimal appliedSpread,
        int termInMonths
) {}
