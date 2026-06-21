# Prompt — Orquestrador — Planejamento — v0.4.0 Currency Engine

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Você é o orquestrador dos agentes do projeto **SRM Credit Engine**.

## Contexto

Estamos trabalhando na branch:

```text
feature/currency-engine
```

Esta branch representa a milestone:

```text
v0.4.0-currency-engine
```

Etapas anteriores concluídas:

```text
v0.2.0-domain-model
v0.3.0-pricing-engine
```

A etapa `v0.2.0-domain-model` entregou:

- schema relacional inicial;
- entidades JPA;
- enums;
- constraints financeiras;
- índices;
- estrutura de Outbox;
- validação com `ddl-auto: validate`.

A etapa `v0.3.0-pricing-engine` entregou:

- motor de precificação com Strategy Pattern;
- uso de `BigDecimal`;
- arredondamento explícito com `RoundingMode.HALF_EVEN`;
- testes unitários;
- JaCoCo com cobertura mínima de 90%;
- script `scripts/pre-push.sh`.

## Subagents disponíveis

Consulte, acione e coordene os seguintes subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_qa_qualidade.md
agents/agent_devops_especialista.md
```

Você deve simular a contribuição de cada subagent, consolidar conflitos e apresentar uma decisão técnica final.

## Objetivo da etapa

Planejar a implementação do **Currency Engine** do SRM Credit Engine.

O Currency Engine deve armazenar e prover taxas de câmbio entre moedas, inicialmente suportando BRL e USD.

Nesta etapa, o objetivo é preparar o serviço de domínio/aplicação para:

- registrar taxa de câmbio manualmente;
- consultar taxa de câmbio mais recente;
- consultar taxa entre moeda de origem e moeda de destino;
- validar pares de moedas;
- preparar futura conversão cambial usada na liquidação.

## Regras de negócio

1. O sistema deve suportar inicialmente BRL e USD.
2. Taxas de câmbio devem usar `BigDecimal`.
3. Não usar `double` ou `float`.
4. A taxa deve ser sempre positiva.
5. A moeda de origem e destino não podem ser iguais.
6. Deve existir uma forma de obter a taxa mais recente para um par de moedas.
7. O Currency Engine deve ser separado do Pricing Engine.
8. Conversão cambial não deve ser aplicada no Pricing Engine nesta etapa.
9. A taxa encontrada futuramente será usada como snapshot na liquidação.
10. Nesta etapa, não criar liquidação.
11. Nesta etapa, não criar frontend.
12. Endpoints REST podem ser planejados, mas só devem ser implementados se aprovado explicitamente.
13. O foco inicial deve ser serviço de aplicação + domínio + testes.
14. Manter JaCoCo com cobertura mínima de 90%.
15. Manter `./mvnw clean verify` passando.
16. Manter `scripts/pre-push.sh` funcionando.
17. Não reduzir cobertura para passar build.
18. Não usar `double` ou `float`.
19. Atualizar ADR se houver decisão arquitetural relevante.
20. Atualizar README se houver novo comando ou nova decisão de uso.

## Modo obrigatório

Nesta primeira resposta:

- não implemente código;
- não altere arquivos;
- não gere patch final;
- gere apenas plano técnico consolidado;
- simule a análise colaborativa dos subagents;
- liste pontos de decisão humana;
- aguarde aprovação explícita.

## A resposta deve conter

1. Resumo da etapa `v0.4.0-currency-engine`.
2. Análise do Analista de Requisitos.
3. Análise do Arquiteto de Sistemas.
4. Análise do Backend Especialista.
5. Análise do QA / Qualidade.
6. Análise do DevOps Especialista.
7. Decisão consolidada do orquestrador.
8. Escopo incluído.
9. Escopo explicitamente fora desta etapa.
10. Proposta de design do Currency Engine.
11. Classes candidatas.
12. Interfaces candidatas.
13. Pacotes Java sugeridos.
14. Estratégia de persistência das taxas.
15. Estratégia de busca da taxa mais recente.
16. Estratégia de validação de moedas.
17. Estratégia de precisão com `BigDecimal`.
18. Estratégia de testes unitários.
19. Estratégia de testes de repository/service, se necessário.
20. Riscos técnicos.
21. Critérios de aceite.
22. Checklist de validação local.
23. Sugestão de commits Conventional Commits.
24. Sugestão de tag semântica futura.
25. Perguntas ou pontos que exigem aprovação humana.

## Diretrizes de design esperadas

Avalie como proposta inicial:

```text
domain/currency/
├── CurrencyConversionException.java
├── ExchangeRateProvider.java
├── ExchangeRateLookupService.java
├── ExchangeRateRegisterService.java
└── ExchangeRateResult.java
```

E, se fizer sentido:

```text
application/currency/
├── RegisterExchangeRateUseCase.java
└── FindLatestExchangeRateUseCase.java
```

O design deve respeitar:

- separação entre domínio e aplicação;
- baixo acoplamento com JPA;
- uso de `BigDecimal`;
- busca da taxa mais recente por par de moedas;
- facilidade de testes;
- preparação para futura liquidação;
- preparação para futura API manual/mock de câmbio.

## Condição de parada

Ao final da resposta, use exatamente:

```text
Aguardando aprovação para iniciar a implementação da etapa v0.4.0-currency-engine.
```
