package br.com.srm.creditengine.domain.receivable;

import br.com.srm.creditengine.domain.assignor.Assignor;
import br.com.srm.creditengine.domain.currency.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReceivableEntityTest {

    @Test
    void gettersAndSetters() {
        Receivable r = new Receivable();
        UUID id = UUID.randomUUID();
        Assignor a = new Assignor();
        ReceivableType t = new ReceivableType();
        Currency c = new Currency();

        r.setId(id);
        r.setVersion(1L);
        r.setAssignor(a);
        r.setReceivableType(t);
        r.setCurrency(c);
        r.setExternalReference("ref123");
        r.setFaceValue(new BigDecimal("1000.00"));
        r.setDueDate(LocalDate.now().plusDays(30));
        r.setStatus(ReceivableStatus.REGISTERED);

        assertEquals(id, r.getId());
        assertEquals(1L, r.getVersion());
        assertEquals(a, r.getAssignor());
        assertEquals(t, r.getReceivableType());
        assertEquals(c, r.getCurrency());
        assertEquals("ref123", r.getExternalReference());
        assertEquals(new BigDecimal("1000.00"), r.getFaceValue());
        assertEquals(ReceivableStatus.REGISTERED, r.getStatus());
    }
}

