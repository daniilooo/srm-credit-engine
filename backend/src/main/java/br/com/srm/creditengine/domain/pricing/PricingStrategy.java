package br.com.srm.creditengine.domain.pricing;

public interface PricingStrategy {
    PricingResult calculate(PricingRequest request);
    String getReceivableTypeCode();
}

