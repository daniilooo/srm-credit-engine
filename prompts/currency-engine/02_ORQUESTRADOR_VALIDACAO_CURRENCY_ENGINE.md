# Prompt — Orquestrador — Validação — v0.4.0 Currency Engine

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/currency-engine
```

A implementação da etapa `v0.4.0-currency-engine` foi concluída localmente.

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

1. Currency Engine implementado.
2. Suporte inicial a BRL e USD.
3. Uso de `BigDecimal`.
4. Nenhum uso de `double` ou `float`.
5. Validação de taxa positiva.
6. Validação de moeda origem diferente de moeda destino.
7. Busca da taxa mais recente por par de moedas.
8. Currency Engine separado do Pricing Engine.
9. Pricing Engine não foi alterado indevidamente.
10. Nenhum endpoint REST criado.
11. Nenhum frontend criado.
12. Nenhum settlement flow criado.
13. Nenhuma conversão final aplicada no Pricing Engine.
14. JaCoCo continua com mínimo de 90%.
15. `./mvnw clean verify` passa.
16. `scripts/pre-push.sh` passa.
17. Backend sobe com Actuator `UP`.
18. ADR/README atualizados, se necessário.
19. `.git/hooks` não foi versionado.
20. Relatório JaCoCo não foi commitado.

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
