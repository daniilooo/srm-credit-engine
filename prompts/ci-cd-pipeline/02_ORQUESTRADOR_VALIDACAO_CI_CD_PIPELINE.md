# Prompt — Orquestrador — Validação — v0.14.0 CI/CD Pipeline

Use `agents/agent_orquestrador.md`.

Valide a etapa `v0.14.0-ci-cd-pipeline`.

## Checklist

1. `.github/workflows/ci.yml` criado.
2. Workflow executa em PR para main.
3. Workflow executa em push para main.
4. Job backend configurado com Java 21.
5. Job backend executa `./mvnw -B clean verify`.
6. Job frontend configurado com Node compatível.
7. Job frontend executa `npm ci`.
8. Job frontend executa build.
9. Job frontend executa testes headless.
10. Job docker executa `docker compose config`.
11. Job docker executa `docker compose build`.
12. Job de security check valida secrets rastreados.
13. Não há secrets reais no repositório.
14. Não há deploy real.
15. Não há publicação de imagem.
16. Não há alteração de regras de negócio.
17. `docs/ci-cd/github-actions.md` criado.
18. `docs/adr/0011-ci-cd-pipeline.md` criado.
19. README atualizado.
20. AI_USAGE atualizado.
21. Checklist final atualizado.

## Comandos locais

```bash
git status --short
git diff --name-only
docker compose config
git status --short | grep -E "\\.env$|db_user$|db_password$|target|dist|node_modules"
```

Quando possível:

```bash
cd backend && ./mvnw clean verify
```

```bash
cd frontend && npm ci && npm run build && npm test -- --watch=false --browsers=ChromeHeadless
```

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
