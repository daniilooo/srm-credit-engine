package br.com.srm.creditengine.domain.settlement;

import br.com.srm.creditengine.domain.assignor.Assignor;
import br.com.srm.creditengine.domain.currency.Currency;
import br.com.srm.creditengine.domain.receivable.Receivable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SettlementEntityTest {

    @Test
    void gettersAndSetters() {
        Settlement s = new Settlement();
        UUID id = UUID.randomUUID();
        Receivable r = new Receivable();
        Assignor a = new Assignor();
        Currency c = new Currency();

        s.setId(id);
        s.setVersion(1L);
        s.setReceivable(r);
        s.setAssignor(a);
        s.setPaymentCurrency(c);
        s.setStatus(SettlementStatus.CONFIRMED);
        s.setSettledAmount(new BigDecimal("950.1234"));
        s.setExchangeRateBaseCurrencyCode("USD");
        s.setExchangeRateQuoteCurrencyCode("BRL");
        s.setExchangeRateValue(new BigDecimal("5.1234567890"));
        s.setExchangeRateUsedAt(OffsetDateTime.now());

        assertEquals(id, s.getId());
        assertEquals(1L, s.getVersion());
        assertEquals(r, s.getReceivable());
        assertEquals(a, s.getAssignor());
        assertEquals(c, s.getPaymentCurrency());
        assertEquals(SettlementStatus.CONFIRMED, s.getStatus());
    }
}

