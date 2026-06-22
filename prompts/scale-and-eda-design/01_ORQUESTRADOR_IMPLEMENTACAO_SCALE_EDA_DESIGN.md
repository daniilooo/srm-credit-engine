# Prompt — Orquestrador — Implementação — v0.12.0 Scale & EDA Design

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/scale-and-eda-design
```

Milestone:

```text
v0.12.0-scale-and-eda-design
```

## Status

O plano técnico foi aprovado. Agora implemente apenas documentação.

## Idioma obrigatório

Toda documentação deve ser escrita em **português do Brasil**.

## Regras obrigatórias

- Não alterar código de produção.
- Não alterar backend.
- Não alterar frontend.
- Não alterar Docker Compose.
- Não alterar migrations Flyway.
- Não alterar testes.
- Não implementar Kafka, filas, workers, CQRS ou microsserviços reais.
- Não documentar design futuro como se estivesse implementado.
- Usar termos claros: “proposta futura”, “evolução”, “não implementado nesta versão”.
- Não incluir secrets reais.
- Usar Mermaid para diagramas.

## Sequência de implementação

1. Inspecionar documentação atual:
   - README.md;
   - AI_USAGE.md;
   - docs/architecture/overview.md;
   - docs/adr/;
   - docs/observability/;
   - docs/validation/final-checklist.md.
2. Criar diretórios, se necessário:
   - docs/scale/
   - docs/eda/
3. Criar `docs/scale/one-million-transactions.md`.
4. Criar `docs/eda/event-driven-evolution.md`.
5. Criar `docs/eda/outbox-pattern-evolution.md`.
6. Criar `docs/eda/idempotency-and-retries.md`.
7. Criar `docs/eda/cqrs-reporting-evolution.md`.
8. Criar `docs/observability/observability-at-scale.md`.
9. Criar `docs/adr/0009-scale-and-eda-design.md`.
10. Atualizar `README.md` com links para os novos documentos.
11. Atualizar `AI_USAGE.md` adicionando a milestone v0.12.0.
12. Atualizar `docs/validation/final-checklist.md`, se fizer sentido, com checklist de documentação de escala.
13. Revisar links relativos.
14. Rodar validações básicas.

## Conteúdo obrigatório

### docs/scale/one-million-transactions.md

Incluir:

- premissas;
- gargalos do desenho atual;
- estratégia para 1 milhão de transações/minuto;
- ingestão assíncrona;
- liquidação assíncrona;
- particionamento;
- cache de FX;
- read replicas;
- CQRS;
- backpressure;
- bulk processing;
- idempotência;
- observabilidade;
- riscos;
- roadmap.

### docs/eda/event-driven-evolution.md

Incluir:

- estado atual;
- por que não microserviços agora;
- quando evoluir;
- eventos candidatos;
- producers;
- consumers;
- Mermaid flowchart;
- consistência eventual;
- DLQ;
- versionamento de eventos;
- trade-offs.

### docs/eda/outbox-pattern-evolution.md

Incluir:

- tabela `outbox_events` existente;
- motivo da criação no MVP;
- fluxo transacional;
- publisher futuro;
- sequência Mermaid;
- retries;
- idempotência;
- DLQ;
- retention.

### docs/eda/idempotency-and-retries.md

Incluir:

- criticidade em domínio financeiro;
- proteção atual;
- idempotency key futura;
- retries seguros;
- exponential backoff;
- DLQ;
- compensação;
- auditoria.

### docs/eda/cqrs-reporting-evolution.md

Incluir:

- reporting atual;
- SQL nativo atual;
- proposta CQRS;
- projections;
- read model;
- read replicas;
- eventual consistency;
- paginação e filtros em alto volume;
- trade-offs.

### docs/observability/observability-at-scale.md

Incluir:

- observabilidade atual;
- métricas futuras;
- SLI/SLO;
- tracing;
- logs estruturados;
- dashboards;
- alertas;
- métricas de filas;
- métricas de DLQ;
- correlationId/traceId.

### docs/adr/0009-scale-and-eda-design.md

Incluir:

- status;
- contexto;
- decisão;
- por que documental;
- manter modular monolith agora;
- evolução para EDA;
- consequências;
- alternativas consideradas.

## Validações

Rodar:

```bash
git status --short
git diff --name-only
docker compose config
git status --short | grep -E "\\.env$|db_user$|db_password$|target|dist|node_modules"
```

O último comando não deve retornar nada.

## Resposta final esperada

Responder com:

1. arquivos criados/alterados;
2. documentos de escala criados;
3. documentos EDA criados;
4. documentação de Outbox;
5. documentação de idempotência/retries;
6. documentação CQRS;
7. documentação de observabilidade em escala;
8. ADR criada;
9. README/AI_USAGE atualizados;
10. comandos de validação;
11. confirmação de que código de produção não foi alterado;
12. confirmação de que nenhum segredo real foi incluído;
13. commits sugeridos.

Não faça push. Não crie tag.
