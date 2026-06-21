package br.com.srm.creditengine.domain.receivable;

import br.com.srm.creditengine.domain.assignor.Assignor;
import br.com.srm.creditengine.domain.common.AuditableEntity;
import br.com.srm.creditengine.domain.currency.Currency;
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
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "receivables")
public class Receivable extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "assignor_id", nullable = false)
	private Assignor assignor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "receivable_type_id", nullable = false)
	private ReceivableType receivableType;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "currency_id", nullable = false)
	private Currency currency;

	@Column(name = "external_reference", nullable = false, length = 100)
	private String externalReference;

	@Column(name = "face_value", nullable = false, precision = 19, scale = 4)
	private BigDecimal faceValue;

	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private ReceivableStatus status;

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

	public Assignor getAssignor() {
		return assignor;
	}

	public void setAssignor(Assignor assignor) {
		this.assignor = assignor;
	}

	public ReceivableType getReceivableType() {
		return receivableType;
	}

	public void setReceivableType(ReceivableType receivableType) {
		this.receivableType = receivableType;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public BigDecimal getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(BigDecimal faceValue) {
		this.faceValue = faceValue;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public ReceivableStatus getStatus() {
		return status;
	}

	public void setStatus(ReceivableStatus status) {
		this.status = status;
	}
}

