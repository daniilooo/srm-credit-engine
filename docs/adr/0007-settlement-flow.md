# ADR 0007 — Settlement Flow Design

## Status

Aceito

## Contexto

A milestone v0.5.0 implementa o fluxo transacional de liquidação financeira, integrando Pricing Engine, Currency Engine, Settlement, Receivable e Outbox em uma única transação ACID. É o primeiro ponto de integração completa do sistema.

## Decisão

### Transação única ACID

Toda a liquidação ocorre em `@Transactional` no `SettleReceivableUseCase`:
1. Salvar `Settlement` (status = CONFIRMED)
2. Atualizar `Receivable.status` → SETTLED
3. Salvar `OutboxEvent` (status = PENDING)

Se qualquer etapa falhar, a transação é revertida por completo — sem liquidação parcial.

### Ordenação das operações

Settlement é salvo **antes** do Receivable. Isso ativa a constraint `UNIQUE(receivable_id)` do banco como primeira barreira de proteção. O `@Version` do `Receivable` age como segunda barreira via optimistic locking.

### Estratégia de dupla liquidação

Três barreiras em cascata:
- Verificação de `receivable.status == REGISTERED` (aplicação)
- `UNIQUE(receivable_id)` em settlements (banco)
- `@Version` em Receivable (ORM/optimistic locking)

Não há bloqueio pessimista (`SELECT FOR UPDATE`) nesta milestone, conforme ADR 0003.

### Cálculo do prazo em meses

Prazo calculado por dias corridos divididos por 30 com arredondamento para cima:
`termInMonths = (int) ((days + 29) / 30)`

`ChronoUnit.MONTHS.between()` foi explicitamente descartado por imprecisão em cenários de liquidação intra-mês.

### Câmbio aplicado apenas no final

Valor presente é calculado primeiro (Pricing Engine). Câmbio é aplicado por último:
`settledAmount = presentValue × rateValue, scale(4, HALF_EVEN)`

Não há câmbio dentro do Pricing Engine.

### Mesma moeda — taxa identidade

Quando moeda de pagamento = moeda do recebível, o Currency Engine não é consultado. O snapshot cambial é preenchido com rate = 1, ambos os códigos = paymentCurrencyCode, capturedAt = now. Isso garante que o Settlement sempre tenha snapshot preenchido (campos NOT NULL no schema).

### Snapshot cambial imutável

O `capturedAt` do `ExchangeRateResult` (= `createdAt` da taxa no banco) é persistido como `exchangeRateUsedAt` no Settlement. Uma vez salvo, o snapshot não é recalculado.

### Nenhuma migration Flyway

O schema V1 já contém:
- `settlements.exchange_rate_base_currency_code` NOT NULL
- `settlements.exchange_rate_quote_currency_code` NOT NULL
- `settlements.exchange_rate_value NUMERIC(19,10)` NOT NULL
- `settlements.exchange_rate_used_at` NOT NULL
- `UNIQUE(receivable_id)` em settlements
- `ck_settlements_settled_amount_positive`

### Wiring de beans via Configuration

`PricingSimulationService` e `ExchangeRateLookupService` são registrados como `@Bean` em classes `@Configuration` em `infrastructure/config/`. O domínio não recebe anotações Spring.

### OutboxEvent

Payload JSON montado com `String.formatted()` sem dependência de Jackson na camada de aplicação. Dispatcher real fica para milestone futura.

## Alternativas consideradas

| Alternativa | Motivo de não adoção |
|-------------|---------------------|
| `ChronoUnit.MONTHS.between()` | Comportamento indesejado em liquidações intra-mês |
| Bloqueio pessimista (`SELECT FOR UPDATE`) | Adicionaria overhead desnecessário nesta milestone |
| Settlement com status PENDING → CONFIRMED | Sem sentido em fluxo síncrono-transacional |
| Jackson no payload da Outbox | Adiciona dependência desnecessária na camada de aplicação |
| Não preencher snapshot quando mesma moeda | Incompatível com constraints NOT NULL do schema |

## Consequências

- A liquidação é atômica e auditável.
- O snapshot cambial permite futura reconciliação sem recalcular taxas.
- A Outbox está pronta para receber um dispatcher assíncrono em milestone futura.
- O Pricing Engine e o Currency Engine permanecem sem dependências entre si.
