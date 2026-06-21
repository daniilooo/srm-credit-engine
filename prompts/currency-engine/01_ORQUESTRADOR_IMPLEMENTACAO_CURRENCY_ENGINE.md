# Prompt — Orquestrador — Implementação — v0.4.0 Currency Engine

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/currency-engine
```

A etapa planejada é:

```text
v0.4.0-currency-engine
```

## Status

O plano técnico da etapa foi revisado e aprovado.

Agora coordene os subagents necessários e implemente a etapa de forma sequencial e controlada.

## Subagents disponíveis

Consulte e use como referência:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_qa_qualidade.md
agents/agent_devops_especialista.md
```

Você deve atuar como orquestrador e só aplicar decisões consolidadas.

## Decisões humanas aprovadas

1. Implementar o Currency Engine.
2. Suportar inicialmente BRL e USD.
3. Usar `BigDecimal` para taxa de câmbio.
4. Não usar `double` ou `float`.
5. Validar que taxa de câmbio seja positiva.
6. Validar que moeda origem e moeda destino sejam diferentes.
7. Implementar busca da taxa mais recente por par de moedas.
8. Não aplicar conversão cambial no Pricing Engine nesta etapa.
9. Não persistir liquidação nesta etapa.
10. Não criar frontend nesta etapa.
11. Não criar endpoint REST nesta etapa.
12. Manter `./mvnw clean verify` passando.
13. Manter cobertura mínima de 90% no JaCoCo.
14. Manter `scripts/pre-push.sh` funcional.
15. Atualizar ADR se houver decisão arquitetural relevante.
16. Atualizar README se houver novo comando ou instrução relevante.
17. Não versionar `.git/hooks`.
18. Não commitar relatório JaCoCo.
19. Não alterar Docker Compose sem necessidade.
20. Não criar GitHub Actions nesta etapa.

## Sequência obrigatória de implementação

Implemente em passos pequenos:

1. Revisar rapidamente o schema atual e entidades existentes de moeda/câmbio.
2. Confirmar se nova migration Flyway é necessária.
3. Criar/ajustar interfaces e services do Currency Engine.
4. Criar/ajustar repository/gateway necessário.
5. Implementar validações de taxa e par de moedas.
6. Implementar busca da taxa mais recente.
7. Criar testes unitários.
8. Criar testes de service/repository, se aplicável.
9. Atualizar ADR/README, se necessário.
10. Rodar `./mvnw clean verify`.
11. Rodar `scripts/pre-push.sh`.
12. Validar que backend ainda sobe e Actuator responde `UP`.

## Regras técnicas obrigatórias

- Usar `BigDecimal`.
- Não usar `double`.
- Não usar `float`.
- Não criar controller REST.
- Não criar frontend.
- Não implementar settlement.
- Não implementar conversão final no Pricing Engine.
- Não reduzir cobertura JaCoCo.
- Não remover testes para passar build.
- Não fazer grandes alterações fora do escopo.
- Não duplicar lógica já existente.

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
```

```bash
curl http://localhost:8080/actuator/health
```

Resultado esperado:

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

## Ao final da implementação, responda com

1. Arquivos criados/alterados.
2. Decisões técnicas tomadas.
3. Se houve ou não migration Flyway.
4. Como o Currency Engine foi separado do Pricing Engine.
5. Como a taxa mais recente é buscada.
6. Como as validações foram implementadas.
7. Testes criados.
8. Resultado do `./mvnw clean verify`.
9. Resultado do `scripts/pre-push.sh`.
10. Checklist de aceite.
11. Sugestão de commits Conventional Commits.
12. Próxima etapa sugerida.

## Não fazer

- Não abrir PR.
- Não criar tag.
- Não fazer push.
- Não implementar REST controller.
- Não implementar frontend.
- Não implementar settlement flow.
