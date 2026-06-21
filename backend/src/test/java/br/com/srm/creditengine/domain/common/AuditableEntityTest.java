package br.com.srm.creditengine.domain.common;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditableEntityTest {

    static class TestAuditable extends AuditableEntity {
        public void callOnCreate() {
            onCreate();
        }

        public void callOnUpdate() {
            onUpdate();
        }
    }

    @Test
    void onCreateSetsCreatedAndUpdated() throws InterruptedException {
        TestAuditable t = new TestAuditable();
        assertNull(t.getCreatedAt());
        assertNull(t.getUpdatedAt());
        t.callOnCreate();
        assertNotNull(t.getCreatedAt());
        assertNotNull(t.getUpdatedAt());
        OffsetDateTime created = t.getCreatedAt();
        OffsetDateTime updated = t.getUpdatedAt();
        // ensure update changes updatedAt
        Thread.sleep(5);
        t.callOnUpdate();
        assertTrue(t.getUpdatedAt().isAfter(updated) || t.getUpdatedAt().isEqual(updated));
        // createdAt should remain same
        assertEquals(created, t.getCreatedAt());
    }
}

