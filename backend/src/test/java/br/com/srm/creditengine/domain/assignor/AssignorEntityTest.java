package br.com.srm.creditengine.domain.assignor;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AssignorEntityTest {

    @Test
    void gettersAndSetters() {
        Assignor a = new Assignor();
        UUID id = UUID.randomUUID();
        a.setId(id);
        a.setLegalName("Legal");
        a.setTradeName("Trade");
        a.setDocumentNumber("123456789");

        assertEquals(id, a.getId());
        assertEquals("Legal", a.getLegalName());
        assertEquals("Trade", a.getTradeName());
        assertEquals("123456789", a.getDocumentNumber());
    }
}

