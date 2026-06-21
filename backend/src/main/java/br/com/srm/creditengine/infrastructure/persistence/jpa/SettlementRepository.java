package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.settlement.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
}

