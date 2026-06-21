package br.com.srm.creditengine.domain.settlement;

import br.com.srm.creditengine.domain.assignor.Assignor;
import br.com.srm.creditengine.domain.common.AuditableEntity;
import br.com.srm.creditengine.domain.currency.Currency;
import br.com.srm.creditengine.domain.receivable.Receivable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlements")
public class Settlement extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "receivable_id", nullable = false, unique = true)
	private Receivable receivable;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "assignor_id", nullable = false)
	private Assignor assignor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "payment_currency_id", nullable = false)
	private Currency paymentCurrency;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private SettlementStatus status;

	@Column(name = "settled_amount", nullable = false, precision = 19, scale = 4)
	private BigDecimal settledAmount;

	@Column(name = "exchange_rate_base_currency_code", nullable = false, length = 3)
	private String exchangeRateBaseCurrencyCode;

	@Column(name = "exchange_rate_quote_currency_code", nullable = false, length = 3)
	private String exchangeRateQuoteCurrencyCode;

	@Column(name = "exchange_rate_value", nullable = false, precision = 19, scale = 10)
	private BigDecimal exchangeRateValue;

	@Column(name = "exchange_rate_used_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime exchangeRateUsedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Receivable getReceivable() {
		return receivable;
	}

	public void setReceivable(Receivable receivable) {
		this.receivable = receivable;
	}

	public Assignor getAssignor() {
		return assignor;
	}

	public void setAssignor(Assignor assignor) {
		this.assignor = assignor;
	}

	public Currency getPaymentCurrency() {
		return paymentCurrency;
	}

	public void setPaymentCurrency(Currency paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}

	public SettlementStatus getStatus() {
		return status;
	}

	public void setStatus(SettlementStatus status) {
		this.status = status;
	}

	public BigDecimal getSettledAmount() {
		return settledAmount;
	}

	public void setSettledAmount(BigDecimal settledAmount) {
		this.settledAmount = settledAmount;
	}

	public String getExchangeRateBaseCurrencyCode() {
		return exchangeRateBaseCurrencyCode;
	}

	public void setExchangeRateBaseCurrencyCode(String exchangeRateBaseCurrencyCode) {
		this.exchangeRateBaseCurrencyCode = exchangeRateBaseCurrencyCode;
	}

	public String getExchangeRateQuoteCurrencyCode() {
		return exchangeRateQuoteCurrencyCode;
	}

	public void setExchangeRateQuoteCurrencyCode(String exchangeRateQuoteCurrencyCode) {
		this.exchangeRateQuoteCurrencyCode = exchangeRateQuoteCurrencyCode;
	}

	public BigDecimal getExchangeRateValue() {
		return exchangeRateValue;
	}

	public void setExchangeRateValue(BigDecimal exchangeRateValue) {
		this.exchangeRateValue = exchangeRateValue;
	}

	public OffsetDateTime getExchangeRateUsedAt() {
		return exchangeRateUsedAt;
	}

	public void setExchangeRateUsedAt(OffsetDateTime exchangeRateUsedAt) {
		this.exchangeRateUsedAt = exchangeRateUsedAt;
	}
}

