# Idempotência e Estratégia de Retries

> Este documento descreve a proteção atual contra operações duplicadas e o design evolutivo para suporte a idempotência explícita com chave de cliente e retries seguros. As seções de **proposta futura** descrevem designs ainda não implementados.

---

## Por Que Idempotência É Crítica em Liquidação Financeira

Uma liquidação financeira é uma operação com efeito colateral irreversível: transfere valor, altera o status do recebível e gera registro de auditoria. Executar a mesma liquidação duas vezes significa:

- Cobrar duplamente o cedente
- Gerar dois registros de settlement para o mesmo recebível
- Inconsistência nos relatórios de posição

Em sistemas distribuídos, operações podem ser repetidas por:

- **Timeout do cliente** → cliente retenta sem saber se a primeira request chegou
- **Retry automático de proxy** → Nginx ou load balancer retenta em caso de falha de rede
- **Consumer de fila** → mensagem entregue mais de uma vez (at-least-once delivery)
- **Falha do publisher** → evento publicado, mas status no banco não atualizado → republica

---

## Proteção Atual Contra Dupla Liquidação

O sistema atual implementa **três barreiras** de proteção:

### Barreira 1 — Verificação de Status no Domínio

```java
// Em SettleReceivableUseCase
if (!receivable.getStatus().equals(ReceivableStatus.REGISTERED)) {
    throw new ReceivableAlreadySettledException(receivable.getId());
}
```

Se o recebível já foi liquidado (`status = SETTLED`), a segunda tentativa é rejeitada imediatamente com exceção de domínio.

### Barreira 2 — Constraint UNIQUE no Banco

```sql
-- V1__create_initial_schema.sql
CONSTRAINT uk_settlements_receivable_id UNIQUE (receivable_id)
```

Mesmo que dois threads ultrapassem a verificação de status simultaneamente (race condition), o banco rejeita a segunda `INSERT` com `ConstraintViolationException`.

### Barreira 3 — Optimistic Locking com @Version

```java
// Receivable.java
@Version
private Long version;
```

A segunda transação concorrente que tenta atualizar o mesmo `Receivable` receberá `OptimisticLockException`, pois a versão no banco já foi incrementada pela primeira transação.

**Resultado das 3 barreiras:** dupla liquidação é impossível no estado atual — seja por request sequencial ou por concorrência.

---

## Limitação Atual — Sem Idempotency Key Explícita

O sistema atual **não aceita uma chave de idempotência do cliente** no request. Isso significa:

- Se o cliente faz POST e recebe timeout → não sabe se a liquidação foi criada
- Se faz uma segunda requisição com os mesmos dados → recebe erro (mas não sabe por quê)
- Não há como distinguir "request duplicado" de "novo request com mesmos dados"

---

## Idempotency Key — Proposta Futura

> **Não implementado nesta versão.**

A evolução proposta adiciona um campo `idempotency_key` ao request de liquidação:

### Request com Idempotency Key

```json
POST /api/v1/settlements
X-Idempotency-Key: "client-generated-uuid-abc-123"

{
  "receivableId": "uuid",
  "paymentCurrencyCode": "USD"
}
```

### Comportamento esperado

```
Request 1: X-Idempotency-Key: "abc-123"
  → Processa normalmente → 201 Created {settlementId}
  → Persiste: idempotency_key="abc-123", result={...}

Request 2 (duplicado): X-Idempotency-Key: "abc-123"
  → Encontra "abc-123" na tabela de idempotency
  → Retorna o mesmo resultado que o Request 1 → 200 OK {settlementId}
  (sem reprocessar — resultado em cache)

Request 3 (diferente): X-Idempotency-Key: "xyz-456"
  → Processa normalmente → novo settlementId
```

### Schema da tabela de idempotência (proposta futura)

```sql
CREATE TABLE idempotency_keys (
    idempotency_key VARCHAR(255) PRIMARY KEY,
    operation       VARCHAR(100) NOT NULL,
    response_status INTEGER NOT NULL,
    response_body   JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at      TIMESTAMPTZ NOT NULL
);
```

**Retention:** chaves expiram após 24h. Após expirar, o mesmo `idempotency_key` pode gerar nova operação.

---

## Retries Seguros

Um retry é seguro quando a operação é idempotente. Com as barreiras atuais, retries do mesmo `receivableId` são seguros — a segunda tentativa retorna erro controlado.

Com `idempotency_key` (proposta futura), retries são explicitamente seguros: o cliente reutiliza a mesma chave e recebe o mesmo resultado.

### Regras para retries seguros

| Regra | Motivo |
|---|---|
| Usar o mesmo `idempotency_key` no retry | Evita criar operação diferente |
| Não usar `receivableId` como chave de idempotência | Semanticamente diferente: "mesmo recebível" ≠ "mesmo request" |
| Respeitar `Retry-After` quando receber `429` ou `503` | Evitar thundering herd |
| Não retentar erros de validação (`400`, `422`) | São falhas permanentes — retry não vai resolver |
| Retentar apenas erros transientes (`408`, `500`, `502`, `503`, `504`) | São falhas temporárias |

---

## Exponential Backoff com Jitter

Retries sem delay causam thundering herd: todos os clientes retentam ao mesmo tempo, sobrecarregando o servidor que acabou de recuperar.

**Fórmula recomendada:**

```
delay = min(base * 2^attempt, max_delay) + random_jitter
```

| Tentativa | Base | Delay (sem jitter) | Delay (com jitter ±20%) |
|---|---|---|---|
| 1 | 500ms | 500ms | 400ms – 600ms |
| 2 | 500ms | 1.000ms | 800ms – 1.200ms |
| 3 | 500ms | 2.000ms | 1.600ms – 2.400ms |
| 4 | 500ms | 4.000ms | 3.200ms – 4.800ms |
| 5 | 500ms | 8.000ms (cap: 5s) | 4.000ms – 5.000ms |

Após a 5ª tentativa sem sucesso: registrar como falha definitiva, alertar operações.

---

## Dead-Letter Queue (DLQ)

Quando um consumer de fila falha após N retries, a mensagem vai para a DLQ.

**Fluxo:**

```
Evento → Consumer → Falha
  → Retry 1 (delay: 1s)
  → Retry 2 (delay: 2s)
  → Retry 3 (delay: 4s)
  → Retry 4 (delay: 8s)
  → Retry 5 (delay: 16s)
  → DLQ
    → Alertar time de operações
    → Worker de compensação analisa e decide: reprocessar ou compensar
```

**Campos na mensagem DLQ:**

```json
{
  "originalEvent": { ... },
  "originalTopic": "settlement-events",
  "failureReason": "SettlementRepository: connection timeout",
  "failedAt": "2026-06-22T14:00:00Z",
  "attempts": 5
}
```

---

## Compensação

Quando uma operação falha de forma definitiva após todos os retries e não pode ser reprocessada, é necessária uma operação de compensação.

| Cenário | Compensação |
|---|---|
| Settlement criado, mas notificação ao cedente falhou | Reenviar notificação (idempotente) |
| Settlement criado, mas projeção de relatório não atualizada | Reprocessar evento de projeção |
| Settlement com Exchange Rate desatualizado | Criar settlement de ajuste (operação humana) |
| Evento publicado no Kafka, mas Settlement não criado | Impossível no modelo atual — transação garante ambos ou nenhum |

---

## Auditoria

Toda operação de retry, falha e compensação deve ser auditável:

| Campo | Localização | Uso |
|---|---|---|
| `outbox_events.attempts` | PostgreSQL | Número de tentativas de publicação |
| `outbox_events.error_message` | PostgreSQL | Último erro do publisher |
| `outbox_events.status` | PostgreSQL | `PENDING`, `PUBLISHED`, `FAILED` |
| `outbox_events.processed_at` | PostgreSQL | Momento da publicação bem-sucedida |
| `correlationId` no log | Logs estruturados | Rastreamento cross-sistema |
| `traceId` no header HTTP | OpenTelemetry (futuro) | Trace end-to-end |

---

## Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| Cliente não usa idempotency key | Alta | Alto | Documentar obrigatoriedade; validar no servidor |
| Chave de idempotência expirada | Média | Médio | TTL de 24h; alertar quando expirar durante operação longa |
| Consumer em DLQ não alertado | Média | Alto | Métrica `dlq.depth > 0` gera alerta imediato |
| Retry storm após recovery | Alta | Alto | Exponential backoff + jitter obrigatórios |
| Compensação incorreta | Baixa | Crítico | Aprovação humana para compensações que envolvam valor financeiro |
