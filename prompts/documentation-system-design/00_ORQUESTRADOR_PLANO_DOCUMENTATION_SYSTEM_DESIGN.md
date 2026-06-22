# Prompt — Orquestrador — Planejamento — v0.11.0 Documentation & System Design

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/documentation-system-design
```

Milestone:

```text
v0.11.0-documentation-system-design
```

## Idioma obrigatório

Toda documentação criada ou atualizada deve ser escrita em **português do Brasil**.

Manter em inglês apenas termos técnicos consolidados, como Docker Compose, Spring Boot, Angular, Prometheus, Strategy Pattern, Outbox Pattern, REST API, Backend, Frontend, Healthcheck, Secrets e Modular Monolith.

## Contexto

Etapas concluídas:

```text
v0.2.0-domain-model
v0.3.0-pricing-engine
v0.4.0-currency-engine
v0.5.0-settlement-flow
v0.6.0-rest-api
v0.7.0-reporting-api
v0.8.0-frontend-operator-panel
v0.9.0-full-docker-compose
v0.10.0-backend-observability
```

O projeto já possui backend, frontend, dockerização completa e observabilidade com Prometheus. Agora precisamos consolidar a documentação para avaliação Sênior/Staff.

## Subagents disponíveis

Consulte e simule a análise dos subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_frontend_especialista.md
agents/agent_devops_especialista.md
agents/agent_qa_qualidade.md
```

## Objetivo

Planejar a criação/revisão de:

1. `README.md`;
2. `AI_USAGE.md`;
3. C4 Context;
4. C4 Container;
5. ER Diagram;
6. documentação Docker;
7. documentação Prometheus/observabilidade;
8. documentação de endpoints;
9. fluxos de negócio;
10. estratégia Git/branching;
11. justificativa arquitetural;
12. trade-offs;
13. checklist final.

## Regras obrigatórias

- Não alterar código de produção.
- Não alterar backend.
- Não alterar frontend.
- Não alterar Docker Compose.
- Não alterar migrations Flyway.
- Não criar feature funcional.
- Usar Mermaid para diagramas.
- Não inventar funcionalidades inexistentes.
- Documentar limitações reais.
- Documentar trade-offs.
- Documentar secrets e `.env` corretamente.
- Documentar fluxo de liquidação.
- Documentar estratégia Git e tags SemVer.
- Documentar uso de IA no `AI_USAGE.md`.

## Ferramentas/modelos de IA que devem aparecer no AI_USAGE.md

Documentar explicitamente:

```text
- GitHub Copilot
- OpenAI Codex
- Claude
- GPT
```

Explicar que foram usados para planejamento técnico, prompts de agentes, revisão de arquitetura, apoio de implementação, investigação de erros, testes, documentação e análise crítica de trade-offs.

Deixar claro que as decisões finais foram revisadas e aprovadas por humano.

## Modelo de desenvolvimento assistido por IA

O `AI_USAGE.md` deve explicar que foram criados **subagents especializados**:

- analista de requisitos;
- arquiteto de sistemas;
- backend especialista;
- frontend especialista;
- DevOps especialista;
- QA/qualidade;
- orquestrador.

Explicar que o **agent orquestrador** consolidava a visão dos subagents, propunha plano técnico, identificava riscos, validava escopo e gerava relatório ao final.

Documentar o fluxo usado em cada etapa:

```text
1. Definição da milestone.
2. Criação de prompt específico da etapa.
3. Claude lê/usa o agent orquestrador.
4. Claude gera plano técnico consolidado.
5. Plano passa por aprovação humana.
6. Após aprovação, Claude implementa.
7. Claude gera relatório de implementação.
8. Humano revisa arquivos, escopo e git status.
9. Commits são organizados por responsabilidade.
10. PR é aberto.
11. Tag SemVer é criada somente após merge na main.
```

## Justificativa da arquitetura

Incluir no README, em `docs/architecture/overview.md` e, se fizer sentido, em um ADR.

A arquitetura adotada foi um **modular monolith em camadas**, com separação:

```text
- interfaces/rest
- application
- domain
- infrastructure
- reporting
```

Justificar:

1. O desafio não exigia microserviços reais.
2. Microserviços adicionariam complexidade operacional desnecessária.
3. O domínio financeiro exige consistência transacional.
4. A liquidação não pode ocorrer parcialmente.
5. O modular monolith mantém ACID e simplicidade operacional.
6. A separação em camadas melhora testes, leitura e evolução.
7. `domain` concentra regras de negócio e evita acoplamento com HTTP/banco/frameworks.
8. `application` orquestra casos de uso e transações.
9. `infrastructure` concentra JPA, JDBC, Docker, configuração e observabilidade.
10. `interfaces/rest` expõe contratos HTTP sem vazar entidades JPA.
11. `reporting` separa leitura analítica com SQL otimizado do fluxo transacional.
12. O projeto deixa pontos de evolução para EDA futura via `outbox_events`.

## Trade-off obrigatório

Documentar explicitamente:

```text
A escolha por modular monolith prioriza consistência, simplicidade, rastreabilidade e menor custo operacional para o escopo do desafio. Para cenários de escala extrema, o projeto poderia evoluir para uma arquitetura orientada a eventos, com filas, workers assíncronos, CQRS, read replicas e particionamento.
```

## Pontos obrigatórios na documentação

Garantir menção a:

- `BigDecimal`;
- Strategy Pattern no Pricing Engine;
- transação ACID no Settlement Flow;
- optimistic locking;
- proteção contra dupla liquidação;
- snapshot da taxa de câmbio;
- SQL nativo com `NamedParameterJdbcTemplate`;
- Docker Compose full stack;
- secrets por arquivo em vez de credenciais no `.env`;
- Prometheus;
- métricas técnicas e métricas de negócio;
- Angular consumindo REST API;
- AI_USAGE com análise crítica.

## Arquivos candidatos

```text
README.md
AI_USAGE.md
docs/architecture/overview.md
docs/c4/context.md
docs/c4/container.md
docs/er/er-diagram.md
docs/api/endpoints.md
docs/docker/running-with-docker.md
docs/observability/backend-observability.md
docs/git/branching-strategy.md
docs/validation/final-checklist.md
docs/adr/00XX-documentation-system-design.md
```

Antes de criar novos arquivos, verificar se já existem equivalentes para atualizar em vez de duplicar.

## Resposta esperada

Responder com:

1. resumo da etapa;
2. análise por subagent;
3. decisão consolidada;
4. escopo incluído;
5. escopo fora;
6. arquivos a criar/alterar;
7. estrutura de documentação;
8. plano para README;
9. plano para AI_USAGE;
10. plano para C4;
11. plano para ER;
12. plano para Docker;
13. plano para observabilidade;
14. plano para Git/branching;
15. plano para justificativa arquitetural;
16. plano para modelo de desenvolvimento com IA;
17. riscos;
18. critérios de aceite;
19. checklist;
20. commits sugeridos;
21. pontos de aprovação humana.

Nesta primeira resposta não implemente arquivos. Gere apenas plano técnico e aguarde aprovação explícita.

Finalize exatamente com:

```text
Aguardando aprovação para iniciar a implementação da etapa v0.11.0-documentation-system-design.
```
