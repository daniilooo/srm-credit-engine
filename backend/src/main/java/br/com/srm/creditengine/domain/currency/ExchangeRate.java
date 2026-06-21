package br.com.srm.creditengine.domain.currency;

import br.com.srm.creditengine.domain.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "exchange_rates", uniqueConstraints = {
		@UniqueConstraint(name = "uk_exchange_rates_pair_valid_from", columnNames = {"base_currency_id", "quote_currency_id", "valid_from"})
})
public class ExchangeRate extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "base_currency_id", nullable = false)
	private Currency baseCurrency;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "quote_currency_id", nullable = false)
	private Currency quoteCurrency;

	@Column(name = "rate_value", nullable = false, precision = 19, scale = 10)
	private BigDecimal rateValue;

	@Column(name = "valid_from", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime validFrom;

	@Column(name = "valid_to", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime validTo;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public Currency getQuoteCurrency() {
		return quoteCurrency;
	}

	public void setQuoteCurrency(Currency quoteCurrency) {
		this.quoteCurrency = quoteCurrency;
	}

	public BigDecimal getRateValue() {
		return rateValue;
	}

	public void setRateValue(BigDecimal rateValue) {
		this.rateValue = rateValue;
	}

	public OffsetDateTime getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(OffsetDateTime validFrom) {
		this.validFrom = validFrom;
	}

	public OffsetDateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(OffsetDateTime validTo) {
		this.validTo = validTo;
	}
}

