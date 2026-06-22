# Prompt — Orquestrador — PR — v0.6.0 REST API

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Branch:

```text
feature/rest-api
```

Destino:

```text
main
```

Milestone:

```text
v0.6.0-rest-api
```

## Missão

Gerar descrição profissional de Pull Request.

## A descrição deve conter

1. Summary.
2. Milestone.
3. Changes.
4. REST Endpoints.
5. Request/Response Contracts.
6. Validation.
7. Error Handling.
8. OpenAPI/Swagger.
9. Tests.
10. Validation commands.
11. Commits.
12. Out of scope.
13. Notes.

## Endpoints esperados

```text
POST /api/v1/pricing/simulations
POST /api/v1/exchange-rates
GET  /api/v1/exchange-rates/latest
POST /api/v1/settlements
```

## Regras importantes

- controllers em `interfaces/rest`;
- DTOs dedicados;
- entidades JPA não expostas;
- Bean Validation;
- `GlobalExceptionHandler`;
- contrato padrão de erro;
- OpenAPI/Swagger;
- BigDecimal preservado;
- sem double/float;
- sem frontend;
- sem relatório analítico;
- sem outbox dispatcher;
- sem integração externa.

## Validações esperadas

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

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Título sugerido

```text
feat: expose credit engine REST API
```

Gere a descrição pronta para copiar e colar no GitHub.
