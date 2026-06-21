package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.receivable.ReceivableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceivableTypeRepository extends JpaRepository<ReceivableType, UUID> {
}

