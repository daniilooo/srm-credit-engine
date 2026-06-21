package br.com.srm.creditengine.domain.pricing.strategy;

import br.com.srm.creditengine.domain.pricing.PricingException;
import br.com.srm.creditengine.domain.pricing.PricingRequest;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import br.com.srm.creditengine.domain.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MercantileDuplicatePricingStrategy implements PricingStrategy {

    private static final BigDecimal SPREAD = new BigDecimal("0.015000"); // 1.5% a.m.
    private static final int INTERMEDIATE_SCALE = 10;
    private static final int FINAL_SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    @Override
    public PricingResult calculate(PricingRequest request) {
        if (request.getTermInMonths() <= 0) {
            throw new PricingException("Term must be positive");
        }
        if (request.getFaceValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PricingException("Face value must be positive");
        }
        BigDecimal tax = request.getBaseTaxMonthly();
        BigDecimal rate = tax.add(SPREAD);
        BigDecimal denominator = BigDecimal.ONE.add(rate).setScale(INTERMEDIATE_SCALE, ROUNDING);
        BigDecimal pow = denominator.pow(request.getTermInMonths());
        BigDecimal present = request.getFaceValue().divide(pow, INTERMEDIATE_SCALE, ROUNDING);
        present = present.setScale(FINAL_SCALE, ROUNDING);
        return new PricingResult(present, tax, SPREAD, request.getTermInMonths());
    }

    @Override
    public String getReceivableTypeCode() {
        return "DUPLICATA";
    }
}

