# Prompt — Orquestrador — Planejamento — v0.6.0 REST API

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/rest-api
```

Milestone:

```text
v0.6.0-rest-api
```

## Contexto

Etapas concluídas:

```text
v0.2.0-domain-model
v0.3.0-pricing-engine
v0.4.0-currency-engine
v0.5.0-settlement-flow
```

A etapa `v0.5.0-settlement-flow` entregou o fluxo transacional:

```text
Receivable -> Pricing Engine -> Currency Engine -> Settlement -> OutboxEvent
```

Agora devemos planejar a API REST do SRM Credit Engine, com validação, tratamento global de erros e documentação OpenAPI/Swagger.

## Subagents disponíveis

Consulte e simule a análise dos subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_qa_qualidade.md
agents/agent_devops_especialista.md
```

## Objetivo

Planejar endpoints REST para expor os fluxos já implementados:

```text
POST /api/v1/pricing/simulations
POST /api/v1/exchange-rates
GET  /api/v1/exchange-rates/latest?base=BRL&quote=USD
POST /api/v1/settlements
```

Endpoints de consulta adicionais só devem entrar se já houver use case/repository adequado.

## Regras obrigatórias

1. Não implementar código ainda.
2. Não alterar arquivos ainda.
3. Gerar apenas o plano técnico.
4. Não criar frontend.
5. Não criar relatório analítico.
6. Não criar outbox dispatcher.
7. Não criar integração externa de câmbio.
8. Não criar autenticação/autorização.
9. Não alterar Pricing Engine indevidamente.
10. Não alterar Currency Engine indevidamente.
11. Não alterar Settlement Flow indevidamente.
12. Não criar migration Flyway sem necessidade.
13. Criar DTOs em `interfaces/rest`.
14. Não expor entidades JPA diretamente.
15. Usar Bean Validation.
16. Criar tratamento global com `@RestControllerAdvice`.
17. Criar contrato padrão de erro.
18. Usar OpenAPI/Swagger.
19. Manter `BigDecimal`.
20. Não usar `double` ou `float`.
21. Manter JaCoCo >= 90%.
22. Manter `./mvnw clean verify` passando.
23. Manter `scripts/pre-push.sh` funcional.

## Planejar tratamento de erros

Mapear exceções para HTTP status:

```text
400 Bad Request
404 Not Found
409 Conflict
422 Unprocessable Entity, se fizer sentido
500 Internal Server Error
```

Contrato sugerido:

```json
{
  "timestamp": "2026-06-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Mensagem clara",
  "path": "/api/v1/settlements"
}
```

## A resposta deve conter

1. Resumo da etapa.
2. Análise por subagent.
3. Decisão consolidada.
4. Escopo incluído.
5. Escopo fora da etapa.
6. Endpoints candidatos.
7. DTOs candidatos.
8. Estratégia de validação.
9. Estratégia de tratamento global de erros.
10. Estratégia OpenAPI/Swagger.
11. Estratégia de testes.
12. Riscos técnicos.
13. Critérios de aceite.
14. Checklist de validação.
15. Sugestão de commits.
16. Sugestão de tag futura.
17. Pontos que exigem aprovação humana.

Finalize exatamente com:

```text
Aguardando aprovação para iniciar a implementação da etapa v0.6.0-rest-api.
```
