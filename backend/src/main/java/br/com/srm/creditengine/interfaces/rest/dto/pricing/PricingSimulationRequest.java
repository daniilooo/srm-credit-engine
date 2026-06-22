package br.com.srm.creditengine.interfaces.rest.dto.pricing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PricingSimulationRequest(
        @NotNull @DecimalMin("0.01") BigDecimal faceValue,
        @NotNull @Future LocalDate dueDate,
        @NotBlank String receivableType,
        @NotNull @DecimalMin("0.000001") BigDecimal baseTaxMonthly
) {}
