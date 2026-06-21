package br.com.srm.creditengine.domain.receivable;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReceivableTypeEntityTest {

    @Test
    void gettersAndSetters() {
        ReceivableType t = new ReceivableType();
        UUID id = UUID.randomUUID();
        t.setId(id);
        t.setCode(ReceivableTypeCode.DUPLICATA);
        t.setName("Duplicata");
        t.setDescription("Desc");

        assertEquals(id, t.getId());
        assertEquals(ReceivableTypeCode.DUPLICATA, t.getCode());
        assertEquals("Duplicata", t.getName());
        assertEquals("Desc", t.getDescription());
    }
}

