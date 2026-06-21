package br.com.srm.creditengine.domain.receivable;

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
@Table(name = "receivable_types", uniqueConstraints = {
		@UniqueConstraint(name = "uk_receivable_types_code", columnNames = "code")
})
public class ReceivableType extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "code", nullable = false, length = 50)
	private ReceivableTypeCode code;

	@Column(name = "name", nullable = false, length = 120)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ReceivableTypeCode getCode() {
		return code;
	}

	public void setCode(ReceivableTypeCode code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

