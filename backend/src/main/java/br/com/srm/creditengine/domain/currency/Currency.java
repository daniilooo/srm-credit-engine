package br.com.srm.creditengine.domain.currency;

import br.com.srm.creditengine.domain.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(name = "currencies", uniqueConstraints = {
		@UniqueConstraint(name = "uk_currencies_code", columnNames = "code")
})
public class Currency extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "code", nullable = false, length = 3)
	private CurrencyCode code;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "symbol", length = 10)
	private String symbol;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public CurrencyCode getCode() {
		return code;
	}

	public void setCode(CurrencyCode code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}

