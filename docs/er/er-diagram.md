# Diagrama Entidade-Relacionamento — SRM Credit Engine

Diagrama gerado a partir do schema real definido em `V1__create_initial_schema.sql`.

```mermaid
erDiagram
    ASSIGNORS {
        uuid id PK
        varchar legal_name
        varchar trade_name
        varchar document_number UK
        timestamptz created_at
        timestamptz updated_at
    }

    CURRENCIES {
        uuid id PK
        varchar code UK
        varchar name
        varchar symbol
        timestamptz created_at
        timestamptz updated_at
    }

    RECEIVABLE_TYPES {
        uuid id PK
        varchar code UK
        varchar name
        varchar description
        timestamptz created_at
        timestamptz updated_at
    }

    EXCHANGE_RATES {
        uuid id PK
        uuid base_currency_id FK
        uuid quote_currency_id FK
        numeric_19_10 rate_value
        timestamptz valid_from
        timestamptz valid_to
        timestamptz created_at
        timestamptz updated_at
    }

    RECEIVABLES {
        uuid id PK
        bigint version
        uuid assignor_id FK
        uuid receivable_type_id FK
        uuid currency_id FK
        varchar external_reference
        numeric_19_4 face_value
        date due_date
        varchar status
        timestamptz created_at
        timestamptz updated_at
    }

    SETTLEMENTS {
        uuid id PK
        bigint version
        uuid receivable_id FK_UK
        uuid assignor_id FK
        uuid payment_currency_id FK
        varchar status
        numeric_19_4 settled_amount
        varchar exchange_rate_base_currency_code
        varchar exchange_rate_quote_currency_code
        numeric_19_10 exchange_rate_value
        timestamptz exchange_rate_used_at
        timestamptz created_at
        timestamptz updated_at
    }

    OUTBOX_EVENTS {
        uuid id PK
        varchar aggregate_type
        uuid aggregate_id
        varchar event_type
        jsonb payload
        varchar status
        varchar correlation_id
        integer attempts
        varchar error_message
        timestamptz created_at
        timestamptz processed_at
    }

    CURRENCIES ||--o{ EXCHANGE_RATES : "base_currency_id"
    CURRENCIES ||--o{ EXCHANGE_RATES : "quote_currency_id"
    ASSIGNORS ||--o{ RECEIVABLES : "assignor_id"
    CURRENCIES ||--o{ RECEIVABLES : "currency_id"
    RECEIVABLE_TYPES ||--o{ RECEIVABLES : "receivable_type_id"
    RECEIVABLES ||--|| SETTLEMENTS : "receivable_id (UNIQUE)"
    ASSIGNORS ||--o{ SETTLEMENTS : "assignor_id"
    CURRENCIES ||--o{ SETTLEMENTS : "payment_currency_id"
    SETTLEMENTS ||--o{ OUTBOX_EVENTS : "aggregate_id (lógico)"
```

## Observações do Schema

### Precisão financeira

| Coluna | Tipo | Propósito |
|---|---|---|
| `exchange_rates.rate_value` | `NUMERIC(19,10)` | 10 casas decimais para taxas de câmbio |
| `receivables.face_value` | `NUMERIC(19,4)` | 4 casas decimais para valores de face |
| `settlements.settled_amount` | `NUMERIC(19,4)` | 4 casas decimais para valores liquidados |
| `settlements.exchange_rate_value` | `NUMERIC(19,10)` | Snapshot da taxa usada na liquidação |

### Constraints financeiras

- `ck_exchange_rates_rate_value_positive` — taxa de câmbio > 0
- `ck_exchange_rates_distinct_currencies` — moeda base ≠ moeda cotação
- `uk_exchange_rates_pair_valid_from` — par direcional único por data de vigência
- `ck_receivables_face_value_positive` — valor de face > 0
- `uk_receivables_assignor_external_reference` — referência única por cedente
- `ck_settlements_settled_amount_positive` — valor liquidado > 0
- `uk_settlements_receivable_id` — **barreira de dupla liquidação** (um recebível → um settlement)

### Snapshot cambial no Settlement

As colunas `exchange_rate_base_currency_code`, `exchange_rate_quote_currency_code`, `exchange_rate_value` e `exchange_rate_used_at` armazenam um snapshot imutável da taxa de câmbio no momento da liquidação. Isso garante rastreabilidade e auditoria sem depender de recálculo futuro.

Quando moeda de pagamento = moeda do recebível, o snapshot é preenchido com `rate_value = 1` e ambos os códigos iguais ao da moeda de pagamento.

### Optimistic Locking

`receivables.version` e `settlements.version` são gerenciados pelo JPA via `@Version`. Qualquer tentativa concorrente de liquidar o mesmo recebível resultará em `OptimisticLockException`, prevenindo dupla liquidação por concorrência.

### Outbox Pattern

`outbox_events` armazena eventos técnicos gerados na mesma transação da liquidação. O campo `aggregate_id` referencia o UUID do `Settlement`. O dispatcher assíncrono — responsável por publicar os eventos — **não foi implementado nesta versão** (ver Limitações).

### Dados de referência (seed)

O schema inclui seed inicial via `INSERT`:
- Moedas: `BRL` (Real Brasileiro) e `USD` (United States Dollar)
- Tipos de recebível: `DUPLICATA` e `CHEQUE_PRE_DATADO`
