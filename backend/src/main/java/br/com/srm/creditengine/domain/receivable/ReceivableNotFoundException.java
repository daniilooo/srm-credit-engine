package br.com.srm.creditengine.domain.receivable;

import java.util.UUID;

public class ReceivableNotFoundException extends RuntimeException {
    public ReceivableNotFoundException(UUID id) {
        super("Receivable not found: " + id);
    }
}
