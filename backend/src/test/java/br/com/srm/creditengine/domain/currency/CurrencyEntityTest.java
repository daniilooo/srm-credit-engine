package br.com.srm.creditengine.domain.currency;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyEntityTest {

    @Test
    void gettersAndSetters() {
        Currency c = new Currency();
        UUID id = UUID.randomUUID();
        c.setId(id);
        c.setCode(CurrencyCode.BRL);
        c.setName("Real");
        c.setSymbol("R$");

        assertEquals(id, c.getId());
        assertEquals(CurrencyCode.BRL, c.getCode());
        assertEquals("Real", c.getName());
        assertEquals("R$", c.getSymbol());
    }
}

