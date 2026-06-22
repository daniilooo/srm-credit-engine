# Prompt — Orquestrador — Validação — v0.11.0 Documentation & System Design

Use `agents/agent_orquestrador.md`.

Valide a documentação da etapa `v0.11.0-documentation-system-design`.

## Checklist

1. README em português atualizado.
2. AI_USAGE em português criado/atualizado.
3. AI_USAGE menciona GitHub Copilot, OpenAI Codex, Claude e GPT.
4. AI_USAGE explica subagents.
5. AI_USAGE explica agent orquestrador.
6. AI_USAGE explica fluxo: prompt, plano, aprovação humana, implementação, relatório, revisão, commit, PR e tag.
7. C4 Context criado/atualizado.
8. C4 Container criado/atualizado.
9. ER Diagram criado/atualizado.
10. Justificativa da arquitetura documentada.
11. Trade-off do modular monolith documentado.
12. Docker Compose documentado.
13. Secrets documentados corretamente.
14. Observabilidade/Prometheus documentada.
15. Endpoints documentados.
16. Estratégia Git/branching documentada.
17. Fluxos de negócio documentados.
18. Limitações conhecidas documentadas.
19. Próximos passos documentados.
20. Links relativos funcionam.
21. Mermaid coerente.
22. Nenhum código de produção alterado.
23. Nenhuma migration alterada.
24. Nenhum segredo real documentado/versionado.
25. `.env`, `db_user`, `db_password`, `target`, `dist` e `node_modules` não aparecem no status.

## Pontos obrigatórios

Verificar se aparecem:

- `BigDecimal`;
- Strategy Pattern;
- transação ACID;
- optimistic locking;
- proteção contra dupla liquidação;
- snapshot cambial;
- `NamedParameterJdbcTemplate`;
- Docker Compose full stack;
- secrets por arquivo;
- Prometheus;
- métricas técnicas e de negócio;
- Angular;
- uso crítico e controlado de IA.

## Comandos

```bash
git status --short
git diff --name-only
docker compose config
git status --short | grep -E "\\.env$|db_user$|db_password$|target|dist|node_modules"
```

O último comando não deve retornar nada.

## Responda com

1. Resultado por subagent.
2. Problemas encontrados.
3. Correções obrigatórias.
4. Correções opcionais.
5. Confirmação dos critérios de aceite.
6. Sugestão de commits.
7. Sugestão de título de PR.
8. Riscos restantes.
9. Próxima etapa.
