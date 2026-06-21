package br.com.srm.creditengine.infrastructure.persistence.jpa;

import br.com.srm.creditengine.domain.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {
}

