# Prompt — Orquestrador — PR — v0.5.0 Settlement Flow

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos finalizando a branch:

```text
feature/settlement-flow
```

Destino do PR:

```text
main
```

Milestone:

```text
v0.5.0-settlement-flow
```

## Sua missão

Gerar uma descrição profissional de Pull Request para a etapa Settlement Flow.

## A descrição deve conter

1. Summary.
2. Milestone.
3. Changes.
4. Settlement Flow.
5. Transactional Consistency.
6. Pricing/Currency Integration.
7. Outbox.
8. Tests.
9. Validation.
10. Commits.
11. Out of scope.
12. Notes.

## Informações da etapa

A etapa implementa o fluxo de liquidação transacional do SRM Credit Engine, integrando:

```text
Receivable + Pricing Engine + Currency Engine + Settlement + Outbox
```

Regras importantes:

- fluxo transacional;
- sem liquidação parcial;
- impede dupla liquidação;
- usa Pricing Engine;
- usa Currency Engine somente se necessário;
- aplica câmbio no final;
- persiste snapshot cambial;
- cria Settlement;
- atualiza Receivable para SETTLED;
- cria OutboxEvent na mesma transação;
- não usa `double` ou `float`;
- mantém BigDecimal;
- mantém JaCoCo 90%;
- mantém `scripts/pre-push.sh`;
- backend deve seguir respondendo `UP` no Actuator.

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

## Título sugerido do PR

```text
feat: implement transactional settlement flow
```

## Formato

Gere a descrição pronta para copiar e colar no GitHub.
