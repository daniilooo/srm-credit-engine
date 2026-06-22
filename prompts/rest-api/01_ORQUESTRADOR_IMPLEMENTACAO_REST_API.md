# Prompt — Orquestrador — Implementação — v0.6.0 REST API

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/rest-api
```

Milestone:

```text
v0.6.0-rest-api
```

## Status

O plano técnico foi aprovado. Agora implemente de forma sequencial e controlada.

## Subagents disponíveis

Consulte e use como referência:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_qa_qualidade.md
agents/agent_devops_especialista.md
```

## Decisões esperadas

1. Implementar REST API backend-only.
2. Controllers em `interfaces/rest`.
3. DTOs dedicados de request/response.
4. Não expor entidades JPA.
5. Bean Validation.
6. `GlobalExceptionHandler` com `@RestControllerAdvice`.
7. Contrato padrão de erro.
8. OpenAPI/Swagger.
9. Endpoints:
   - `POST /api/v1/pricing/simulations`
   - `POST /api/v1/exchange-rates`
   - `GET /api/v1/exchange-rates/latest`
   - `POST /api/v1/settlements`
10. Não criar frontend.
11. Não criar relatório analítico.
12. Não criar outbox dispatcher.
13. Não criar integração externa de câmbio.
14. Não criar autenticação/autorização.
15. Não criar migration sem necessidade.
16. Manter BigDecimal.
17. Não usar `double` ou `float`.
18. Manter JaCoCo >= 90%.
19. Manter `./mvnw clean verify` passando.
20. Manter `scripts/pre-push.sh` funcional.

## Sequência obrigatória

1. Revisar pacote `interfaces/rest`.
2. Revisar dependência OpenAPI/Swagger no `pom.xml`.
3. Se `springdoc-openapi` não existir, propor/incluir dependência.
4. Criar contrato padrão de erro.
5. Criar `GlobalExceptionHandler`.
6. Criar DTOs e controller de pricing simulation.
7. Criar DTOs e controller de exchange rates.
8. Criar DTOs e controller de settlement.
9. Garantir Bean Validation.
10. Garantir que entidades JPA não vazem.
11. Adicionar anotações OpenAPI, se útil.
12. Criar testes de controller.
13. Atualizar README/ADR, se necessário.
14. Rodar `./mvnw clean verify`.
15. Rodar `scripts/pre-push.sh`.
16. Validar Actuator.
17. Validar Swagger UI.

## Exemplos de contratos

### Pricing

```json
{
  "faceValue": "10000.00",
  "dueDate": "2026-12-31",
  "receivableType": "MERCANTILE_DUPLICATE",
  "baseTaxMonthly": "0.0100"
}
```

### Exchange Rate

```json
{
  "baseCurrency": "BRL",
  "quoteCurrency": "USD",
  "rateValue": "0.2000000000",
  "validFrom": "2026-06-21T10:00:00Z"
}
```

### Settlement

```json
{
  "receivableId": "uuid",
  "paymentCurrencyCode": "BRL",
  "baseTaxMonthly": "0.0100"
}
```

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

Validar Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Ao final, responda com

1. Arquivos criados/alterados.
2. Decisões técnicas.
3. Se houve alteração no `pom.xml`.
4. Se houve migration Flyway.
5. Endpoints implementados.
6. DTOs criados.
7. Tratamento global de erros.
8. OpenAPI/Swagger.
9. Testes criados.
10. Resultado do `./mvnw clean verify`.
11. Resultado do `scripts/pre-push.sh`.
12. Checklist de aceite.
13. Sugestão de commits.
14. Próxima etapa sugerida.

Não faça push, não abra PR e não crie tag.
