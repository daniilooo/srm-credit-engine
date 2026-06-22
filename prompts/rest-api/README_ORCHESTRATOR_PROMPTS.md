# Prompts do Orquestrador — SRM Credit Engine — v0.6.0 REST API

Este pacote contém prompts para a etapa **v0.6.0-rest-api** do projeto SRM Credit Engine.

## Estrutura sugerida

```text
srm-credit-engine/
├── agents/
├── prompts/
│   └── rest-api/
│       ├── README_ORCHESTRATOR_PROMPTS.md
│       ├── 00_ORQUESTRADOR_PLANO_REST_API.md
│       ├── 01_ORQUESTRADOR_IMPLEMENTACAO_REST_API.md
│       ├── 02_ORQUESTRADOR_VALIDACAO_REST_API.md
│       └── 03_ORQUESTRADOR_PR_REST_API.md
```

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/rest-api
```

## Milestone

```text
v0.6.0-rest-api
```

## Ordem de uso

1. `00_ORQUESTRADOR_PLANO_REST_API.md`
2. Revisar plano.
3. Aprovar ajustes humanos.
4. `01_ORQUESTRADOR_IMPLEMENTACAO_REST_API.md`
5. `02_ORQUESTRADOR_VALIDACAO_REST_API.md`
6. Commit, push e PR.
7. `03_ORQUESTRADOR_PR_REST_API.md`

## Validações

```bash
cd backend
./mvnw clean verify
```

```bash
cd ..
./scripts/pre-push.sh
```

```bash
docker compose up -d
cd backend
./mvnw spring-boot:run
curl http://localhost:8080/actuator/health
```

## Tag futura

Após merge na `main`:

```bash
git checkout main
git pull origin main
git tag -a v0.6.0-rest-api -m "Expose REST API for credit engine flows"
git push origin v0.6.0-rest-api
```
