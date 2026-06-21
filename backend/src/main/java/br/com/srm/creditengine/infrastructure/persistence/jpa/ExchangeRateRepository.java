package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.currency.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {
}

