# Prompts do Orquestrador — v1.0.0 Final Release

Este pacote contém os prompts da etapa `v1.0.0-final-release`.

## Objetivo

Executar a revisão final do SRM Credit Engine antes da entrega, garantindo que o projeto esteja:

- executável;
- documentado;
- auditável;
- sem secrets versionados;
- sem arquivos soltos;
- com README e AI_USAGE consistentes;
- com Docker Compose validado;
- com backend/frontend/observabilidade verificáveis;
- com histórico de milestones e tags organizado.

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/final-release-polish
```

## Tag final futura

Após merge na `main`:

```bash
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Final release for SRM Credit Engine technical challenge"
git push origin v1.0.0
```
