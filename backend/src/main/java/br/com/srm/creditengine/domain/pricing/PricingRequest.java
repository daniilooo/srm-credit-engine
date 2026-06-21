package br.com.srm.creditengine.domain.pricing;

import java.math.BigDecimal;
import java.util.Objects;

public final class PricingRequest {
    private final BigDecimal faceValue;
    private final BigDecimal baseTaxMonthly;
    private final int termInMonths;
    private final String receivableTypeCode;

    public PricingRequest(BigDecimal faceValue, BigDecimal baseTaxMonthly, int termInMonths, String receivableTypeCode) {
        this.faceValue = Objects.requireNonNull(faceValue, "faceValue");
        this.baseTaxMonthly = Objects.requireNonNull(baseTaxMonthly, "baseTaxMonthly");
        this.termInMonths = termInMonths;
        this.receivableTypeCode = Objects.requireNonNull(receivableTypeCode, "receivableTypeCode");
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public BigDecimal getBaseTaxMonthly() {
        return baseTaxMonthly;
    }

    public int getTermInMonths() {
        return termInMonths;
    }

    public String getReceivableTypeCode() {
        return receivableTypeCode;
    }
}

