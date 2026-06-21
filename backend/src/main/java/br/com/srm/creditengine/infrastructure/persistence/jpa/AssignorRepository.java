package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.assignor.Assignor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssignorRepository extends JpaRepository<Assignor, UUID> {
}

