package br.com.srm.creditengine.domain.currency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateEntityTest {

    @Test
    void gettersAndSetters() {
        ExchangeRate e = new ExchangeRate();
        UUID id = UUID.randomUUID();
        Currency base = new Currency();
        Currency quote = new Currency();
        UUID baseId = UUID.randomUUID();
        UUID quoteId = UUID.randomUUID();
        base.setId(baseId);
        quote.setId(quoteId);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime from = now.minusHours(1);
        OffsetDateTime to = now.plusHours(1);

        e.setId(id);
        e.setBaseCurrency(base);
        e.setQuoteCurrency(quote);
        e.setRateValue(new BigDecimal("5.1234567890"));
        e.setValidFrom(from);
        e.setValidTo(to);

        assertEquals(id, e.getId());
        assertNotNull(e.getBaseCurrency());
        assertNotNull(e.getQuoteCurrency());
        assertEquals(baseId, e.getBaseCurrency().getId());
        assertEquals(quoteId, e.getQuoteCurrency().getId());
        assertEquals(new BigDecimal("5.1234567890"), e.getRateValue());
        assertEquals(from, e.getValidFrom());
        assertEquals(to, e.getValidTo());
    }
}


