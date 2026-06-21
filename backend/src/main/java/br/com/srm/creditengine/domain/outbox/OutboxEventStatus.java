package br.com.srm.creditengine.domain.outbox;

public enum OutboxEventStatus {
	PENDING,
	PROCESSING,
	PUBLISHED,
	ERROR
}

