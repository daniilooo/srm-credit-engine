package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}

