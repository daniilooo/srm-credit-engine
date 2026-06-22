# Prompt — Orquestrador — Implementação — v0.5.0 Settlement Flow

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/settlement-flow
```

A etapa planejada é:

```text
v0.5.0-settlement-flow
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

## Decisões humanas esperadas para implementação

Antes de implementar, respeite as decisões aprovadas no plano.

Se alguma decisão abaixo ainda não tiver sido aprovada, pare e peça confirmação:

1. Implementar settlement flow backend-only.
2. Não criar frontend.
3. Não criar relatório analítico.
4. Não criar dispatcher real da outbox.
5. Não criar integração externa de câmbio.
6. Não criar GitHub Actions.
7. Não usar `double` ou `float`.
8. Usar `BigDecimal`.
9. Usar transação ACID no caso de uso principal.
10. Criar `Settlement`, atualizar `Receivable` e criar `OutboxEvent` na mesma transação.
11. Impedir dupla liquidação com validação + constraint `UNIQUE(receivable_id)`.
12. Usar controle otimista com `@Version` quando disponível.
13. Aplicar câmbio somente no final.
14. Persistir snapshot cambial no `Settlement`.
15. Gerar evento na outbox na mesma transação.
16. Manter JaCoCo >= 90%.
17. Manter `./mvnw clean verify` passando.
18. Manter `scripts/pre-push.sh` funcional.
19. Não versionar `.git/hooks`.
20. Não commitar relatórios JaCoCo.

## Sequência obrigatória de implementação

Implemente em passos pequenos:

1. Revisar entidades atuais: `Receivable`, `Settlement`, `OutboxEvent`, `ExchangeRateResult`, Pricing Engine e Currency Engine.
2. Confirmar se nova migration Flyway é necessária.
3. Criar/ajustar caso de uso principal de liquidação.
4. Criar/ajustar request/result do fluxo de liquidação.
5. Integrar Pricing Engine.
6. Integrar Currency Engine quando houver moeda diferente.
7. Aplicar câmbio no final.
8. Persistir `Settlement`.
9. Atualizar `Receivable` para `SETTLED`.
10. Criar `OutboxEvent`.
11. Garantir transação única.
12. Cobrir com testes unitários.
13. Criar teste de integração somente se necessário e viável.
14. Atualizar ADR/README, se necessário.
15. Rodar `./mvnw clean verify`.
16. Rodar `scripts/pre-push.sh`.
17. Validar que backend ainda sobe e Actuator responde `UP`.

## Regras técnicas obrigatórias

- Não criar controller REST sem aprovação explícita.
- Não criar frontend.
- Não implementar relatórios.
- Não criar outbox dispatcher.
- Não usar `double`.
- Não usar `float`.
- Não alterar Pricing Engine indevidamente.
- Não alterar Currency Engine indevidamente.
- Não reduzir cobertura.
- Não remover testes para passar build.
- Não fazer grandes alterações fora do escopo.
- Não duplicar lógica já existente.
- Não usar transação parcial.
- Não gravar `Settlement` sem atualizar `Receivable`.
- Não atualizar `Receivable` sem gravar `Settlement`.
- Não gravar `Settlement` sem outbox, salvo decisão explícita aprovada.

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
4. Como o fluxo transacional foi implementado.
5. Como a dupla liquidação foi evitada.
6. Como o snapshot cambial foi persistido.
7. Como a outbox foi criada na mesma transação.
8. Testes criados.
9. Resultado do `./mvnw clean verify`.
10. Resultado do `scripts/pre-push.sh`.
11. Checklist de aceite.
12. Sugestão de commits Conventional Commits.
13. Próxima etapa sugerida.

## Não fazer

- Não abrir PR.
- Não criar tag.
- Não fazer push.
- Não implementar frontend.
- Não implementar relatório analítico.
- Não implementar outbox dispatcher.
- Não implementar integração externa.
