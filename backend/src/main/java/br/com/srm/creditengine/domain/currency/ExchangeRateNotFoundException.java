package br.com.srm.creditengine.domain.currency;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException(CurrencyCode base, CurrencyCode quote) {
        super("No exchange rate found for pair: " + base + " -> " + quote);
    }
}
