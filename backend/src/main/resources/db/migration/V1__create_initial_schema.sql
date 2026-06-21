CREATE TABLE assignors (
	id UUID PRIMARY KEY,
	legal_name VARCHAR(200) NOT NULL,
	trade_name VARCHAR(200),
	document_number VARCHAR(20) NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT uk_assignors_document_number UNIQUE (document_number)
);

CREATE TABLE currencies (
	id UUID PRIMARY KEY,
	code VARCHAR(3) NOT NULL,
	name VARCHAR(100) NOT NULL,
	symbol VARCHAR(10),
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT uk_currencies_code UNIQUE (code)
);

CREATE TABLE receivable_types (
	id UUID PRIMARY KEY,
	code VARCHAR(50) NOT NULL,
	name VARCHAR(120) NOT NULL,
	description VARCHAR(500),
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT uk_receivable_types_code UNIQUE (code)
);

CREATE TABLE exchange_rates (
	id UUID PRIMARY KEY,
	base_currency_id UUID NOT NULL,
	quote_currency_id UUID NOT NULL,
	rate_value NUMERIC(19,10) NOT NULL,
	valid_from TIMESTAMP WITH TIME ZONE NOT NULL,
	valid_to TIMESTAMP WITH TIME ZONE,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_exchange_rates_base_currency FOREIGN KEY (base_currency_id) REFERENCES currencies (id),
	CONSTRAINT fk_exchange_rates_quote_currency FOREIGN KEY (quote_currency_id) REFERENCES currencies (id),
	CONSTRAINT ck_exchange_rates_rate_value_positive CHECK (rate_value > 0),
	CONSTRAINT ck_exchange_rates_distinct_currencies CHECK (base_currency_id <> quote_currency_id),
	CONSTRAINT uk_exchange_rates_pair_valid_from UNIQUE (base_currency_id, quote_currency_id, valid_from)
);

CREATE TABLE receivables (
	id UUID PRIMARY KEY,
	version BIGINT NOT NULL,
	assignor_id UUID NOT NULL,
	receivable_type_id UUID NOT NULL,
	currency_id UUID NOT NULL,
	external_reference VARCHAR(100) NOT NULL,
	face_value NUMERIC(19,4) NOT NULL,
	due_date DATE NOT NULL,
	status VARCHAR(30) NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_receivables_assignor FOREIGN KEY (assignor_id) REFERENCES assignors (id),
	CONSTRAINT fk_receivables_receivable_type FOREIGN KEY (receivable_type_id) REFERENCES receivable_types (id),
	CONSTRAINT fk_receivables_currency FOREIGN KEY (currency_id) REFERENCES currencies (id),
	CONSTRAINT ck_receivables_face_value_positive CHECK (face_value > 0),
	CONSTRAINT uk_receivables_assignor_external_reference UNIQUE (assignor_id, external_reference)
);

CREATE TABLE settlements (
	id UUID PRIMARY KEY,
	version BIGINT NOT NULL,
	receivable_id UUID NOT NULL,
	assignor_id UUID NOT NULL,
	payment_currency_id UUID NOT NULL,
	status VARCHAR(30) NOT NULL,
	settled_amount NUMERIC(19,4) NOT NULL,
	exchange_rate_base_currency_code VARCHAR(3) NOT NULL,
	exchange_rate_quote_currency_code VARCHAR(3) NOT NULL,
	exchange_rate_value NUMERIC(19,10) NOT NULL,
	exchange_rate_used_at TIMESTAMP WITH TIME ZONE NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_settlements_receivable FOREIGN KEY (receivable_id) REFERENCES receivables (id),
	CONSTRAINT fk_settlements_assignor FOREIGN KEY (assignor_id) REFERENCES assignors (id),
	CONSTRAINT fk_settlements_payment_currency FOREIGN KEY (payment_currency_id) REFERENCES currencies (id),
	CONSTRAINT ck_settlements_settled_amount_positive CHECK (settled_amount > 0),
	CONSTRAINT ck_settlements_exchange_rate_value_positive CHECK (exchange_rate_value > 0),
	CONSTRAINT uk_settlements_receivable_id UNIQUE (receivable_id)
);

CREATE TABLE outbox_events (
	id UUID PRIMARY KEY,
	aggregate_type VARCHAR(120) NOT NULL,
	aggregate_id UUID NOT NULL,
	event_type VARCHAR(120) NOT NULL,
	payload JSONB NOT NULL,
	status VARCHAR(30) NOT NULL,
	correlation_id VARCHAR(120),
	attempts INTEGER NOT NULL DEFAULT 0,
	error_message VARCHAR(1000),
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	processed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_receivables_assignor_id ON receivables (assignor_id);
CREATE INDEX idx_receivables_currency_id ON receivables (currency_id);
CREATE INDEX idx_receivables_due_date ON receivables (due_date);
CREATE INDEX idx_receivables_assignor_currency_due_date ON receivables (assignor_id, currency_id, due_date);
CREATE INDEX idx_exchange_rates_pair_valid_from ON exchange_rates (base_currency_id, quote_currency_id, valid_from DESC);
CREATE INDEX idx_settlements_assignor_id ON settlements (assignor_id);
CREATE INDEX idx_settlements_payment_currency_id ON settlements (payment_currency_id);
CREATE INDEX idx_settlements_created_at ON settlements (created_at);
CREATE INDEX idx_settlements_assignor_currency_created_at ON settlements (assignor_id, payment_currency_id, created_at);
CREATE INDEX idx_outbox_events_status_created_at ON outbox_events (status, created_at);
CREATE INDEX idx_outbox_events_aggregate ON outbox_events (aggregate_type, aggregate_id);
CREATE INDEX idx_outbox_events_correlation_id ON outbox_events (correlation_id);

INSERT INTO currencies (id, code, name, symbol, created_at, updated_at)
VALUES
	('00000000-0000-0000-0000-000000000001', 'BRL', 'Real Brasileiro', 'R$', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('00000000-0000-0000-0000-000000000002', 'USD', 'United States Dollar', '$', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO receivable_types (id, code, name, description, created_at, updated_at)
VALUES
	('00000000-0000-0000-0000-000000000101', 'DUPLICATA', 'Duplicata', 'Recebível originado de duplicata mercantil.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('00000000-0000-0000-0000-000000000102', 'CHEQUE_PRE_DATADO', 'Cheque Pré-datado', 'Recebível originado de cheque pré-datado.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
