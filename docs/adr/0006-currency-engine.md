# ADR 0006 — Currency Engine Design

## Status

Aceito

## Contexto

A plataforma SRM Credit Engine precisa registrar e consultar taxas de câmbio para suportar futura liquidação multimoedas. Na milestone v0.4.0, o objetivo é implementar um motor de câmbio que:

- suporte BRL e USD inicialmente;
- trate pares de moedas como direcionais (BRL → USD ≠ USD → BRL);
- proveja a taxa mais recente para um par;
- valide entradas de negócio antes de persistir;
- prepare o contrato de snapshot para uso futuro na liquidação.

## Decisão

### Separação de camadas

O Currency Engine segue a mesma separação domain/application/infrastructure já estabelecida no Pricing Engine:

- **domain/currency**: entidades JPA existentes (`Currency`, `ExchangeRate`, `CurrencyCode`) + novos serviços e contratos de domínio sem dependência de Spring.
- **application/currency**: casos de uso que orquestram repositórios e o serviço de domínio.
- **infrastructure/persistence/jpa**: repositórios Spring Data que implementam as portas de domínio.

### Porta de domínio `ExchangeRateProvider`

Interface em `domain/currency` que expõe `findLatest(CurrencyCode base, CurrencyCode quote): Optional<ExchangeRateResult>`. O `ExchangeRateRepository` a implementa via método `default`, evitando um adaptador intermediário desnecessário para o escopo atual.

### Busca da taxa mais recente

Usa método derivado do Spring Data sem JPQL manual:

```
findFirstByBaseCurrency_CodeAndQuoteCurrency_CodeOrderByValidFromDescCreatedAtDesc
```

Critério primário: `validFrom DESC`. Critério secundário em empate: `createdAt DESC`. O índice `idx_exchange_rates_pair_valid_from` já suporta essa ordenação. `valid_to` é ignorado nesta milestone.

### Pares direcionais

BRL → USD e USD → BRL são tratados como pares distintos. Não há inversão automática de taxa. Essa decisão mantém o modelo simples e auditável.

### `ExchangeRateResult`

Record Java imutável com `capturedAt` (= `createdAt` da taxa encontrada) para permitir que a liquidação futura persista um snapshot imutável da taxa usada, sem reprocessamento.

### Validações de negócio

- Taxa deve ser `> 0` (verificado via `compareTo`; nunca `==` ou `equals`).
- Moeda base e moeda destino não podem ser iguais.
- Ambas as validações ocorrem antes de qualquer chamada ao banco (fail fast).
- O banco tem constraints redundantes (`ck_exchange_rates_rate_value_positive`, `ck_exchange_rates_distinct_currencies`) como segunda linha de defesa.

### Precisão financeira

`BigDecimal` em toda a cadeia. Nenhum `double` ou `float`. Sem arredondamento aplicado no Currency Engine — a taxa é armazenada e retornada com a precisão exata do input (schema: `NUMERIC(19,10)`).

### Nenhuma migration Flyway

O schema `V1__create_initial_schema.sql` já contém as tabelas `currencies` e `exchange_rates` com todas as constraints e índices necessários, além dos seeds de BRL e USD.

## Alternativas consideradas

| Alternativa | Motivo de não adoção |
|-------------|---------------------|
| Adaptador intermediário `ExchangeRateRepositoryAdapter` | Adiciona complexidade sem ganho real no escopo atual |
| JPQL com `LIMIT 1` | Evitado conforme decisão humana; método derivado Spring Data é mais expressivo |
| Inversão automática de taxa | Ambiguidade em mercados com spread bid/ask; evitada por clareza |
| `valid_to` na lógica de busca | Fora de escopo nesta milestone |

## Consequências

- O Pricing Engine permanece completamente independente do Currency Engine.
- O contrato `ExchangeRateResult` com `capturedAt` permite que a milestone de settlement use a taxa sem nova consulta ao banco.
- Novos pares de moedas podem ser adicionados com inclusão de novos valores no enum `CurrencyCode` e seed de `currencies`.
