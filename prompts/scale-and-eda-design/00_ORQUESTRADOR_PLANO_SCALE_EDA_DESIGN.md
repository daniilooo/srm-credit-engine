# Prompt — Orquestrador — Planejamento — v0.12.0 Scale & EDA Design

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/scale-and-eda-design
```

Milestone:

```text
v0.12.0-scale-and-eda-design
```

## Idioma obrigatório

Toda documentação deve ser escrita em **português do Brasil**.

Manter termos técnicos consolidados em inglês quando fizer sentido, como Event-Driven Architecture, CQRS, Outbox Pattern, Kafka, Consumer, Producer, Worker, Backpressure, Circuit Breaker, Read Replica, Partitioning, Sharding, Idempotency, Observability e Horizontal Scaling.

## Contexto

O projeto já possui:

- domínio financeiro;
- pricing engine com Strategy Pattern;
- currency engine;
- settlement flow transacional;
- REST API;
- reporting API com SQL nativo;
- frontend Angular;
- Docker Compose full stack;
- Prometheus;
- documentação completa da etapa v0.11.0;
- tabela `outbox_events` já criada como ponto de evolução.

Agora precisamos criar uma etapa de design Staff/Principal para explicar como a solução evoluiria para alto volume e arquitetura orientada a eventos.

## Objetivo

Planejar documentação para:

1. arquitetura para 1 milhão de transações/minuto;
2. evolução de modular monolith para arquitetura orientada a eventos;
3. uso futuro do Outbox Pattern;
4. desenho de workers assíncronos;
5. idempotência;
6. particionamento;
7. CQRS;
8. read replicas;
9. backpressure;
10. retry e dead-letter queue;
11. observabilidade em escala;
12. trade-offs de escala;
13. roadmap evolutivo sem reescrever o domínio.

## Regras obrigatórias

- Não alterar código de produção.
- Não alterar backend.
- Não alterar frontend.
- Não alterar Docker Compose.
- Não alterar migrations Flyway.
- Não implementar Kafka real.
- Não implementar fila real.
- Não criar worker real.
- Não criar CQRS real.
- Não criar novos endpoints.
- Não documentar como implementado algo que é apenas proposta futura.
- Usar claramente termos como “proposta”, “evolução futura”, “design futuro” e “não implementado nesta versão”.
- Usar Mermaid para diagramas.
- Documentar trade-offs e riscos.
- Documentar plano incremental de evolução.

## Subagents disponíveis

Consulte e simule análise dos subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_devops_especialista.md
agents/agent_qa_qualidade.md
```

## Arquivos candidatos

Criar ou atualizar:

```text
docs/scale/one-million-transactions.md
docs/eda/event-driven-evolution.md
docs/eda/outbox-pattern-evolution.md
docs/eda/idempotency-and-retries.md
docs/eda/cqrs-reporting-evolution.md
docs/observability/observability-at-scale.md
docs/adr/0009-scale-and-eda-design.md
README.md
AI_USAGE.md
```

Antes de criar, verificar se já existe documento equivalente.

## Conteúdo esperado — docs/scale/one-million-transactions.md

Documentar:

1. objetivo do design;
2. premissas de carga;
3. gargalos do desenho atual;
4. estratégia de escalabilidade horizontal;
5. separação entre comando e leitura;
6. ingestão assíncrona de recebíveis;
7. liquidação assíncrona;
8. particionamento por cedente/moeda/data;
9. cache de taxas de câmbio;
10. read replicas para relatórios;
11. bulk processing;
12. controle de backpressure;
13. observabilidade;
14. estratégia de degradação;
15. riscos.

## Conteúdo esperado — docs/eda/event-driven-evolution.md

Documentar:

1. estado atual: modular monolith transacional;
2. por que não começar com microserviços;
3. quando evoluir para EDA;
4. eventos de domínio candidatos:
   - `ReceivableRegistered`;
   - `ExchangeRateRegistered`;
   - `SettlementRequested`;
   - `SettlementCompleted`;
   - `SettlementFailed`;
   - `ReportRequested`.
5. produtores e consumidores futuros;
6. diagrama Mermaid de fluxo EDA;
7. consistência eventual;
8. idempotência;
9. versionamento de eventos;
10. DLQ;
11. observabilidade;
12. trade-offs.

## Conteúdo esperado — docs/eda/outbox-pattern-evolution.md

Documentar:

1. tabela `outbox_events` existente;
2. por que ela foi criada já no MVP;
3. como ela permitiria publicar eventos sem perder atomicidade;
4. fluxo:
   - transação grava settlement;
   - transação grava outbox;
   - publisher assíncrono lê outbox;
   - publica no broker;
   - marca evento como publicado.
5. diagrama Mermaid sequenceDiagram;
6. campos relevantes;
7. idempotência;
8. retry;
9. DLQ;
10. limpeza/retention.

## Conteúdo esperado — docs/eda/idempotency-and-retries.md

Documentar:

1. por que idempotência é crítica em liquidação financeira;
2. chave idempotente recomendada;
3. proteção atual contra dupla liquidação;
4. evolução futura com idempotency keys;
5. retries seguros;
6. exponential backoff;
7. DLQ;
8. compensação;
9. auditoria;
10. riscos.

## Conteúdo esperado — docs/eda/cqrs-reporting-evolution.md

Documentar:

1. reporting atual com SQL nativo;
2. por que separar leitura no futuro;
3. modelo CQRS proposto;
4. projections;
5. read model;
6. read replicas;
7. eventual consistency;
8. atualização de projeções por eventos;
9. filtros e paginação em alto volume;
10. riscos e trade-offs.

## Conteúdo esperado — docs/observability/observability-at-scale.md

Documentar:

1. observabilidade atual;
2. métricas adicionais futuras;
3. SLI/SLO propostos;
4. tracing distribuído;
5. logs estruturados;
6. dashboards;
7. alertas;
8. métricas de fila;
9. métricas de DLQ;
10. métricas de latência por fluxo;
11. correlação por traceId/correlationId.

## ADR 0009

Criar:

```text
docs/adr/0009-scale-and-eda-design.md
```

Deve registrar:

- status: proposto ou aceito;
- contexto;
- decisão;
- por que a etapa é documental;
- por que manter modular monolith agora;
- como evoluir para EDA;
- consequências;
- alternativas consideradas.

## README e AI_USAGE

Atualizar apenas se necessário:

- README: adicionar link para nova documentação de escala/EDA.
- AI_USAGE: adicionar milestone v0.12.0 e como a IA apoiou o design.

## Resposta esperada

Responder com:

1. resumo da etapa;
2. análise por subagent;
3. decisão consolidada;
4. escopo incluído;
5. escopo fora;
6. arquivos a criar/alterar;
7. plano por documento;
8. riscos;
9. critérios de aceite;
10. checklist de validação;
11. commits sugeridos;
12. pontos de aprovação humana.

Nesta primeira resposta não implemente arquivos. Gere apenas plano técnico e aguarde aprovação explícita.

Finalize exatamente com:

```text
Aguardando aprovação para iniciar a implementação da etapa v0.12.0-scale-and-eda-design.
```
