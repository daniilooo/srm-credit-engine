package br.com.srm.creditengine.domain.pricing;

import java.math.BigDecimal;
import java.util.Objects;

public final class PricingResult {
    private final BigDecimal presentValue;
    private final BigDecimal appliedTax;
    private final BigDecimal appliedSpread;
    private final int termInMonths;

    public PricingResult(BigDecimal presentValue, BigDecimal appliedTax, BigDecimal appliedSpread, int termInMonths) {
        this.presentValue = Objects.requireNonNull(presentValue, "presentValue");
        this.appliedTax = Objects.requireNonNull(appliedTax, "appliedTax");
        this.appliedSpread = Objects.requireNonNull(appliedSpread, "appliedSpread");
        this.termInMonths = termInMonths;
    }

    public BigDecimal getPresentValue() {
        return presentValue;
    }

    public BigDecimal getAppliedTax() {
        return appliedTax;
    }

    public BigDecimal getAppliedSpread() {
        return appliedSpread;
    }

    public int getTermInMonths() {
        return termInMonths;
    }
}

