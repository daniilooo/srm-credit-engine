# ADR 0002 — Estratégia de Flyway

## Contexto

O projeto usa PostgreSQL e JPA com `ddl-auto: validate`, então o schema deve ser versionado explicitamente.

## Decisão

Manter `V1__create_initial_schema.sql` imutável como migration já aplicada no ambiente existente, preservando a compatibilidade com a base local.

O schema financeiro completo será introduzido em `V2__create_initial_schema.sql`, contendo:
- tabelas;
- constraints;
- foreign keys;
- índices;
- seeds iniciais de `currencies` e `receivable_types`.

## Consequências

- A evolução do schema fica rastreável sem quebrar bancos já migrados.
- O banco é a fonte de verdade para integridade estrutural.
- O bootstrap do ambiente local e futuro CI fica previsível.
- A base nova recebe o schema completo via V2, enquanto bases antigas permanecem válidas com V1.


