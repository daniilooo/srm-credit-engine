# Prompt — Orquestrador — Validação — v0.5.0 Settlement Flow

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/settlement-flow
```

A implementação da etapa `v0.5.0-settlement-flow` foi concluída localmente.

Agora sua missão é revisar a implementação como orquestrador, acionando mentalmente os subagents de requisitos, arquitetura, backend, QA e DevOps.

## Subagents a consultar

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_qa_qualidade.md
agents/agent_devops_especialista.md
```

## Checklist obrigatório

Valide se a implementação cumpre:

1. Settlement Flow implementado.
2. Fluxo é transacional.
3. Não há liquidação parcial.
4. Busca recebível por ID.
5. Valida recebível existente.
6. Valida recebível pendente/elegível.
7. Impede dupla liquidação.
8. Respeita `UNIQUE(receivable_id)`.
9. Usa Pricing Engine para valor presente.
10. Usa Currency Engine somente quando necessário.
11. Aplica câmbio somente no final.
12. Persiste snapshot cambial no `Settlement`.
13. Persiste `Settlement`.
14. Atualiza `Receivable` para `SETTLED`.
15. Cria `OutboxEvent`.
16. `Settlement`, `Receivable` e `OutboxEvent` ocorrem na mesma transação.
17. Não usa `double` ou `float`.
18. Mantém `BigDecimal`.
19. Não criou frontend.
20. Não criou relatório analítico.
21. Não criou outbox dispatcher.
22. Não criou integração externa de câmbio.
23. Não alterou Pricing Engine indevidamente.
24. Não alterou Currency Engine indevidamente.
25. JaCoCo continua com mínimo de 90%.
26. `./mvnw clean verify` passa.
27. `scripts/pre-push.sh` passa.
28. Backend sobe com Actuator `UP`.
29. ADR/README atualizados, se necessário.
30. `.git/hooks` não foi versionado.
31. Relatório JaCoCo não foi commitado.

## Comandos de validação

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

## Responda com

1. Resultado da revisão por subagent.
2. Problemas encontrados.
3. Correções obrigatórias antes do commit.
4. Correções opcionais.
5. Confirmação de critérios de aceite.
6. Sugestão de divisão de commits.
7. Sugestão de mensagem de commit.
8. Sugestão de título de PR.
9. Riscos restantes.
10. Próxima etapa após merge/tag.

## Condição

Não implemente novas features. Apenas revise e proponha correções necessárias.
