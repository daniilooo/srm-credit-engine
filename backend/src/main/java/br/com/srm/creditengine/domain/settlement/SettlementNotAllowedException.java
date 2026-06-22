package br.com.srm.creditengine.domain.settlement;

public class SettlementNotAllowedException extends RuntimeException {
    public SettlementNotAllowedException(String message) {
        super(message);
    }
}
