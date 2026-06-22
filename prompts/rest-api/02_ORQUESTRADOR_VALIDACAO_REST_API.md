# Prompt — Orquestrador — Validação — v0.6.0 REST API

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/rest-api
```

A implementação da etapa `v0.6.0-rest-api` foi concluída localmente.

## Validar

Acione mentalmente os subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_qa_qualidade.md
agents/agent_devops_especialista.md
```

## Checklist obrigatório

1. REST API implementada.
2. Controllers em `interfaces/rest`.
3. DTOs de request/response.
4. Entidades JPA não expostas.
5. Bean Validation usado.
6. `GlobalExceptionHandler` criado.
7. Contrato padrão de erro criado.
8. Endpoint de pricing simulation criado.
9. Endpoint de exchange rate register criado.
10. Endpoint de latest exchange rate criado.
11. Endpoint de settlement criado.
12. OpenAPI/Swagger funcionando.
13. Sem frontend.
14. Sem relatório analítico.
15. Sem outbox dispatcher.
16. Sem integração externa.
17. Sem autenticação/autorização.
18. Pricing Engine sem alteração indevida.
19. Currency Engine sem alteração indevida.
20. Settlement Flow sem alteração indevida.
21. Sem migration Flyway desnecessária.
22. Sem `double` ou `float`.
23. BigDecimal preservado.
24. JaCoCo >= 90%.
25. `./mvnw clean verify` passa.
26. `scripts/pre-push.sh` passa.
27. Backend sobe com Actuator `UP`.
28. Swagger UI acessível.
29. README/ADR atualizados, se necessário.
30. `.git/hooks` não versionado.
31. Relatório JaCoCo não commitado.

## Comandos

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

## Responda com

1. Resultado da revisão por subagent.
2. Problemas encontrados.
3. Correções obrigatórias.
4. Correções opcionais.
5. Confirmação de critérios de aceite.
6. Sugestão de divisão de commits.
7. Sugestão de título do PR.
8. Riscos restantes.
9. Próxima etapa após merge/tag.
