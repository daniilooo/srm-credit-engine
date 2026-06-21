# ADR 0003 — Concorrência e Dupla Liquidação

## Contexto

A liquidação financeira não pode ocorrer duas vezes para o mesmo recebível.

## Decisão

Aplicar a seguinte estratégia inicial:
- `@Version` em `Receivable`;
- `@Version` em `Settlement`;
- `UNIQUE(receivable_id)` em `settlements`;
- futura liquidação em transação ACID.

Não será usado bloqueio pessimista nesta etapa.

## Consequências

- A aplicação ganha proteção contra corrida e duplicidade.
- O banco se torna a última barreira de integridade.
- A implementação futura da liquidação deverá respeitar a transação única.

