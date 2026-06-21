# Prompt — Orquestrador — PR — v0.4.0 Currency Engine

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos finalizando a branch:

```text
feature/currency-engine
```

Destino do PR:

```text
main
```

Milestone:

```text
v0.4.0-currency-engine
```

## Sua missão

Gerar uma descrição profissional de Pull Request para a etapa Currency Engine.

## A descrição deve conter

1. Summary.
2. Milestone.
3. Changes.
4. Currency Engine.
5. Tests.
6. Validation.
7. Commits.
8. Out of scope.
9. Notes.

## Informações da etapa

A etapa implementa o Currency Engine do SRM Credit Engine, responsável por registrar e consultar taxas de câmbio entre BRL e USD.

Regras importantes:

- Taxas usam `BigDecimal`.
- Não usa `double` ou `float`.
- Taxa deve ser positiva.
- Moeda origem e destino não podem ser iguais.
- Busca taxa mais recente por par de moedas.
- Não implementa settlement.
- Não implementa frontend.
- Não implementa endpoint REST.
- Não implementa conversão cambial no Pricing Engine.
- Mantém JaCoCo 90%.
- Mantém `scripts/pre-push.sh`.
- Backend deve seguir respondendo `UP` no Actuator.

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
feat: implement currency exchange engine
```

## Formato

Gere a descrição pronta para copiar e colar no GitHub.
