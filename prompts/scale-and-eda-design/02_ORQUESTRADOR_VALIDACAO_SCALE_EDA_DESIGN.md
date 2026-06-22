# Prompt — Orquestrador — Validação — v0.12.0 Scale & EDA Design

Use `agents/agent_orquestrador.md`.

Valide a documentação da etapa `v0.12.0-scale-and-eda-design`.

## Checklist

1. Documentação em português.
2. `docs/scale/one-million-transactions.md` criado.
3. `docs/eda/event-driven-evolution.md` criado.
4. `docs/eda/outbox-pattern-evolution.md` criado.
5. `docs/eda/idempotency-and-retries.md` criado.
6. `docs/eda/cqrs-reporting-evolution.md` criado.
7. `docs/observability/observability-at-scale.md` criado.
8. `docs/adr/0009-scale-and-eda-design.md` criado.
9. README atualizado com links.
10. AI_USAGE atualizado com v0.12.0.
11. Documentos deixam claro que EDA/CQRS/Kafka/workers são evolução futura.
12. Nenhum código de produção alterado.
13. Nenhuma migration alterada.
14. Nenhum Docker Compose alterado, salvo se explicitamente aprovado.
15. Nenhum segredo real versionado.
16. Diagramas Mermaid coerentes.
17. Trade-offs documentados.
18. Riscos documentados.
19. Roadmap incremental documentado.

## Comandos

```bash
git status --short
git diff --name-only
docker compose config
git status --short | grep -E "\\.env$|db_user$|db_password$|target|dist|node_modules"
```

O último comando não deve retornar nada.

## Responda com

1. resultado por subagent;
2. problemas encontrados;
3. correções obrigatórias;
4. correções opcionais;
5. critérios de aceite;
6. commits sugeridos;
7. título de PR sugerido;
8. riscos restantes;
9. próxima etapa.
