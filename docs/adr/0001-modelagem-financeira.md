# ADR 0001 — Modelagem Financeira

## Contexto

A etapa `v0.2.0-domain-model` precisa estabelecer a fundação do domínio financeiro do SRM Credit Engine sem expor APIs ou implementar o motor de precificação.

## Decisão

A modelagem inicial será baseada em entidades JPA para:
- `Assignor` como pessoa jurídica;
- `Currency` com catálogo persistido;
- `ReceivableType` com catálogo persistido;
- `ExchangeRate` com histórico de taxa;
- `Receivable` como título financeiro;
- `Settlement` como registro da liquidação;
- `OutboxEvent` como evento técnico persistido.

Os valores financeiros usarão `BigDecimal`, os identificadores usarão `UUID`, o vencimento será `LocalDate` e timestamps de auditoria usarão `OffsetDateTime`.

## Consequências

- O domínio fica pronto para expansão sem introduzir complexidade desnecessária.
- O schema do banco passa a ser a fonte de verdade estrutural.
- A liquidação passa a armazenar snapshot cambial nas próprias colunas de `settlements`.

