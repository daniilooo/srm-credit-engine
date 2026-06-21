# ADR 0004 — Outbox Pattern

## Contexto

O projeto deve ficar pronto para uma arquitetura orientada a eventos sem acoplamento prematuro a mensageria real.

## Decisão

Criar a tabela `outbox_events` com campos mínimos para rastreabilidade e futura publicação:
- `id`
- `aggregate_type`
- `aggregate_id`
- `event_type`
- `payload JSONB`
- `status`
- `correlation_id`
- `attempts`
- `error_message`
- `created_at`
- `processed_at`

Nesta etapa, não haverá dispatcher real.

## Consequências

- O domínio passa a registrar eventos de forma persistente.
- O desenho fica pronto para publicação assíncrona futura.
- A rastreabilidade técnica melhora sem adicionar complexidade operacional agora.

