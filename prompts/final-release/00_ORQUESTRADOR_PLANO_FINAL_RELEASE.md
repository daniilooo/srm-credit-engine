# Prompt — Orquestrador — Planejamento — v1.0.0 Final Release

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/final-release-polish
```

Milestone:

```text
v1.0.0-final-release
```

## Idioma obrigatório

Toda documentação criada ou atualizada deve ser escrita em **português do Brasil**.

## Contexto

O projeto passou pelas milestones:

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
v0.11.0-documentation-system-design
v0.12.0-scale-and-eda-design
v0.13.0-crisis-management-git-simulation
v0.14.0-ci-cd-pipeline
```

Agora precisamos fechar a entrega final `v1.0.0`.

## Objetivo

Planejar a revisão final do projeto, cobrindo:

1. validação completa de execução;
2. validação completa de documentação;
3. limpeza de arquivos soltos;
4. verificação de secrets;
5. verificação de tags/milestones;
6. revisão do README;
7. revisão do AI_USAGE;
8. revisão dos links de documentação;
9. revisão do pipeline CI;
10. checklist final de entrega;
11. preparação para tag final `v1.0.0`.

## Subagents disponíveis

Consulte e simule análise dos subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_frontend_especialista.md
agents/agent_devops_especialista.md
agents/agent_qa_qualidade.md
```

## Regras obrigatórias

- Não criar nova feature funcional.
- Não alterar regras de negócio.
- Não alterar schema/migrations.
- Não criar secrets reais.
- Não fazer deploy real.
- Não criar tag.
- Não fazer push.
- Não mascarar limitações conhecidas.
- Documentar apenas o que está implementado.
- Se encontrar problema funcional, reportar antes de alterar.
- Priorizar correções pequenas de documentação, links, typos, consistência e checklist.
- Validar se não há arquivos untracked indevidos.

## Arquivos candidatos

Atualizar apenas se necessário:

```text
README.md
AI_USAGE.md
docs/validation/final-checklist.md
docs/adr/0012-final-release.md
docs/release/v1.0.0-final-release-notes.md
```

Também avaliar se existe necessidade de criar:

```text
docs/release/
```

## Checklist de planejamento

Analisar:

1. README:
   - contém instruções completas?
   - links funcionam?
   - milestones estão atualizadas?
   - CI/CD está documentado?
   - limitações estão honestas?
2. AI_USAGE:
   - cita todas as ferramentas usadas?
   - explica subagents/orquestrador?
   - inclui v0.14.0?
   - inclui v1.0.0 se fizer sentido?
3. Docs:
   - C4;
   - ER;
   - API;
   - Docker;
   - Observability;
   - Scale/EDA;
   - Crisis;
   - CI/CD;
   - ADRs.
4. Git:
   - branch limpa?
   - sem untracked indevido?
   - sem prompts antigos soltos?
   - sem secrets?
5. Execução:
   - docker compose config;
   - docker compose up -d --build;
   - backend health;
   - frontend HTTP 200;
   - Swagger;
   - Prometheus;
   - backend tests;
   - frontend build/test;
   - pipeline CI.
6. Release:
   - criar release notes;
   - ADR final se fizer sentido;
   - script da tag v1.0.0.

## Resposta esperada

Responder com:

1. resumo da etapa;
2. análise por subagent;
3. decisão consolidada;
4. escopo incluído;
5. escopo fora;
6. arquivos a criar/alterar;
7. plano de validação local;
8. plano de revisão documental;
9. plano de limpeza;
10. plano de release notes;
11. riscos;
12. critérios de aceite;
13. checklist de validação;
14. commits sugeridos;
15. pontos de aprovação humana.

Nesta primeira resposta não implemente arquivos. Gere apenas plano técnico e aguarde aprovação explícita.

Finalize exatamente com:

```text
Aguardando aprovação para iniciar a implementação da etapa v1.0.0-final-release.
```
