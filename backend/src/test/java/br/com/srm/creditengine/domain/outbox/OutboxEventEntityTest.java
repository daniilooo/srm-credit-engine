package br.com.srm.creditengine.domain.outbox;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OutboxEventEntityTest {

    @Test
    void gettersAndSetters() {
        OutboxEvent o = new OutboxEvent();
        UUID id = UUID.randomUUID();

        o.setId(id);
        o.setAggregateType("RECEIVABLE");
        o.setAggregateId(UUID.randomUUID());
        o.setEventType("ReceivableRegistered");
        o.setPayload("{\"foo\":\"bar\"}");
        o.setStatus(OutboxEventStatus.PENDING);
        o.setCorrelationId("corr-123");
        o.setAttempts(0);
        o.setErrorMessage(null);
        o.setCreatedAt(OffsetDateTime.now());
        o.setProcessedAt(null);

        assertEquals(id, o.getId());
        assertEquals("RECEIVABLE", o.getAggregateType());
        assertEquals("ReceivableRegistered", o.getEventType());
        assertEquals(OutboxEventStatus.PENDING, o.getStatus());
    }

    @Test
    void onCreateSetsCreatedAtWhenNull() {
        class TestOutbox extends OutboxEvent {
            public void callOnCreate() {
                onCreate();
            }
        }

        TestOutbox o = new TestOutbox();
        assertNull(o.getCreatedAt());
        o.callOnCreate();
        assertNotNull(o.getCreatedAt());
    }
}


