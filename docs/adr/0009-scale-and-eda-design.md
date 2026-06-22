# ADR 0009 — Design de Escalabilidade e Arquitetura Orientada a Eventos

**Status:** Aceito

**Data:** 2026-06-22

**Autores:** Equipe SRM Credit Engine

---

## Contexto

O SRM Credit Engine foi construído como um monólito modular síncrono ao longo de 10 etapas (v0.2.0 a v0.11.0). O sistema entrega:

- Precificação de recebíveis com Strategy Pattern
- Motor de câmbio com pares direcionais
- Liquidação transacional ACID com três barreiras contra dupla liquidação
- Relatórios analíticos com SQL nativo
- Painel Angular para operadores de mesa
- Observabilidade com Micrometer + Prometheus
- Documentação completa de system design

O projeto já possui a tabela `outbox_events` no schema financeiro, criada desde a v0.2.0 como ponto de extensão deliberado para evolução assíncrona futura.

Esta etapa (v0.12.0) tem como objetivo demonstrar **maturidade arquitetural Staff/Principal**: como o sistema evoluiria de forma incremental para suportar alto volume, Event-Driven Architecture (EDA), CQRS e resiliência operacional.

---

## Decisão

**A etapa v0.12.0-scale-and-eda-design é exclusivamente documental.**

Nenhuma linha de código de produção foi alterada. Nenhuma feature nova foi implementada. O conjunto de documentos criados descreve a **evolução futura proposta** do sistema, não o estado atual.

### Por que esta etapa é documental

1. **O sistema atual funciona e é correto.** Alterar o código de produção sem necessidade introduziria risco de regressão sem benefício imediato.

2. **Escala requer evidência de necessidade.** Kafka, CQRS, read replicas e workers assíncronos aumentam significativamente a complexidade operacional. Implementá-los antes de ter volume real seria over-engineering.

3. **O design importa tanto quanto o código.** Demonstrar que se sabe *quando* e *como* evoluir uma arquitetura — e por que não fazer antes — é sinal de maturidade de engenharia.

4. **O Outbox Pattern já está implementado como ponto de extensão.** A `outbox_events` garante que a transição para EDA pode ser feita sem alterar o domínio — apenas adicionando o publisher assíncrono.

---

## Por Que Manter o Modular Monolith Agora

| Critério | Modular Monolith | Microserviços / EDA |
|---|---|---|
| Consistência transacional | ACID nativo — uma transação, um banco | Requer sagas distribuídas, compensação, idempotência |
| Complexidade operacional | 1 processo, 1 banco, 1 pipeline | N serviços, N bancos, broker, service mesh, tracing |
| Volume atual | Escopo do desafio — sem evidência de gargalo | Custo alto sem necessidade comprovada |
| Debug e observabilidade | Stack trace linear, simples | Tracing distribuído obrigatório |
| Custo de infra | Baixo | Alto |
| Adequação ao escopo | Alta — regras bem conhecidas e delimitadas | Baixa — fragmentaria domínio coeso desnecessariamente |

> **A separação em camadas (`domain`, `application`, `infrastructure`, `interfaces/rest`, `reporting`) garante que o domínio não está acoplado ao framework. Isso é o que torna a evolução futura possível — não o deploy em processos separados.**

---

## Como Evoluir para EDA

O roadmap incremental aprovado nesta etapa:

| Fase | Entrega | Pré-requisito |
|---|---|---|
| **Fase 1** | Cache de FX (Caffeine/Redis) + Read Replica para relatórios | Configuração — sem alteração de domínio |
| **Fase 2** | Outbox Publisher + Kafka | `outbox_events` já existe; adicionar worker de publicação |
| **Fase 3** | Workers de liquidação assíncrona + idempotency key | Kafka em produção e estável |
| **Fase 4** | CQRS completo com read model dedicado + Projections | Workers funcionando; volume justificando |

### Ponto de extensão central: `outbox_events`

A tabela `outbox_events` já é populada na mesma transação da liquidação. Para evoluir para EDA:

1. **Nenhuma alteração no domínio** (`SettleReceivableUseCase` já grava o evento)
2. **Adicionar apenas** o Outbox Publisher (novo componente assíncrono)
3. **Adicionar apenas** o broker (Kafka ou RabbitMQ)
4. **Adicionar apenas** consumers por tipo de evento

---

## Documentos Criados

| Documento | Descrição |
|---|---|
| `docs/scale/one-million-transactions.md` | Design de escalabilidade horizontal para 1M tx/min |
| `docs/eda/event-driven-evolution.md` | Evolução para EDA com eventos de domínio e topologia |
| `docs/eda/outbox-pattern-evolution.md` | Outbox Publisher, fluxo transacional e retention |
| `docs/eda/idempotency-and-retries.md` | Idempotência, DLQ e estratégia de retry |
| `docs/eda/cqrs-reporting-evolution.md` | CQRS, projeções e read model para relatórios |
| `docs/observability/observability-at-scale.md` | SLI/SLO, tracing, logs estruturados e alertas |

---

## Consequências

### Positivas

- Documentação completa de design evolutivo para avaliação Staff/Principal
- Decisões de design explícitas e justificadas — reduz "surpresas" para novos membros do time
- Roadmap incremental aprovado — pode ser executado por fase, sem big-bang
- Zero risco de regressão — código de produção não foi alterado

### Negativas / Trade-offs Aceitos

- Os documentos descrevem intenção, não realidade — requerem atualização ao longo da execução do roadmap
- O roadmap pode ficar desatualizado se o time mudar de direção
- Complexidade da EDA proposta é real — não deve ser subestimada na execução

---

## Alternativas Consideradas

### Alternativa 1 — Implementar Kafka e Workers Reais

**Rejeitada.** Aumentaria drasticamente a complexidade do projeto sem evidência de necessidade de volume. Quebraria o critério de foco no escopo do desafio. Introduziria risco de regressão no sistema funcionando.

### Alternativa 2 — Não Criar Documentação de Escala

**Rejeitada.** Deixaria a avaliação de maturidade arquitetural incompleta. O projeto demonstraria domínio de implementação, mas não de design evolutivo — competência distinta e esperada em nível Staff/Principal.

### Alternativa 3 — Migrar para Microserviços

**Rejeitada.** O domínio financeiro exige consistência transacional. Liquidação não pode ser parcial. Microserviços exigiriam sagas distribuídas com complexidade desproporcional ao benefício para o escopo atual. Ver ADR 0001 e `docs/architecture/overview.md`.

### Alternativa 4 — CQRS Real com Read Model Persistido

**Rejeitada para esta etapa.** A camada `reporting` já está separada e pronta para evolução. Implementar CQRS real sem volume que o justifique seria prematuridade. A proposta documental é suficiente para demonstrar a visão.
