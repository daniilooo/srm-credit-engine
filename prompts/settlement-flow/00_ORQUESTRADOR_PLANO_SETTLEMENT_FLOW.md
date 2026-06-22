# Prompt — Orquestrador — Planejamento — v0.5.0 Settlement Flow

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Você é o orquestrador dos agentes do projeto **SRM Credit Engine**.

## Contexto

Estamos trabalhando na branch:

```text
feature/settlement-flow
```

Esta branch representa a milestone:

```text
v0.5.0-settlement-flow
```

Etapas anteriores concluídas:

```text
v0.2.0-domain-model
v0.3.0-pricing-engine
v0.4.0-currency-engine
```

A etapa `v0.2.0-domain-model` entregou:

- schema relacional inicial;
- entidades JPA;
- enums;
- constraints financeiras;
- índices;
- `Receivable`;
- `Settlement`;
- `OutboxEvent`;
- validação com `ddl-auto: validate`.

A etapa `v0.3.0-pricing-engine` entregou:

- motor de precificação com Strategy Pattern;
- `BigDecimal`;
- arredondamento explícito;
- testes unitários;
- JaCoCo com cobertura mínima de 90%;
- script `scripts/pre-push.sh`.

A etapa `v0.4.0-currency-engine` entregou:

- Currency Engine backend-only;
- busca de taxa mais recente por par direcional;
- suporte inicial a BRL/USD;
- `ExchangeRateResult`;
- validações de taxa e par;
- testes unitários;
- ADR de Currency Engine.

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

Planejar a implementação do **Settlement Flow** transacional do SRM Credit Engine.

Esta etapa deve integrar:

```text
Receivable + Pricing Engine + Currency Engine + Settlement + Outbox
```

O objetivo é criar um fluxo backend transacional para liquidar um recebível com segurança financeira.

## Regras de negócio

1. Buscar o recebível por ID.
2. Validar que o recebível existe.
3. Validar que o recebível está elegível para liquidação.
4. Validar que o recebível ainda não foi liquidado.
5. Calcular valor presente usando o Pricing Engine.
6. Aplicar conversão cambial somente se a moeda de pagamento for diferente da moeda do recebível.
7. Buscar taxa de câmbio usando o Currency Engine somente quando necessário.
8. Aplicar câmbio no final do cálculo.
9. Persistir snapshot da taxa usada no `Settlement`.
10. Persistir `Settlement`.
11. Atualizar `Receivable` para `SETTLED`.
12. Criar registro em `outbox_events`.
13. Tudo deve ocorrer na mesma transação ACID.
14. Não pode existir liquidação parcial.
15. Deve impedir dupla liquidação.
16. Deve respeitar `UNIQUE(receivable_id)`.
17. Deve respeitar controle de concorrência otimista com `@Version`, se já existente.
18. Não usar `double` ou `float`.
19. Usar `BigDecimal` em toda a cadeia financeira.
20. Manter JaCoCo com cobertura mínima de 90%.
21. Manter `./mvnw clean verify` passando.
22. Manter `scripts/pre-push.sh` funcionando.

## Fora do escopo desta milestone

Não implementar:

- frontend;
- relatório analítico;
- dashboard;
- dispatcher real da outbox;
- integração externa de câmbio;
- autenticação/autorização;
- mensageria real;
- cache;
- GitHub Actions.

Endpoints REST podem ser planejados, mas só devem ser implementados se houver aprovação explícita. A recomendação inicial é manter esta milestone backend-only.

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

1. Resumo da etapa `v0.5.0-settlement-flow`.
2. Análise do Analista de Requisitos.
3. Análise do Arquiteto de Sistemas.
4. Análise do Backend Especialista.
5. Análise do QA / Qualidade.
6. Análise do DevOps Especialista.
7. Decisão consolidada do orquestrador.
8. Escopo incluído.
9. Escopo explicitamente fora desta etapa.
10. Fluxo transacional proposto.
11. Classes candidatas.
12. Use cases candidatos.
13. Repositórios necessários.
14. Estratégia de concorrência.
15. Estratégia de idempotência e dupla liquidação.
16. Estratégia de snapshot cambial.
17. Estratégia de outbox.
18. Estratégia de precisão financeira com `BigDecimal`.
19. Estratégia de testes unitários.
20. Estratégia de testes de integração, se necessário.
21. Riscos técnicos.
22. Critérios de aceite.
23. Checklist de validação local.
24. Sugestão de commits Conventional Commits.
25. Sugestão de tag semântica futura.
26. Perguntas ou pontos que exigem aprovação humana.

## Pontos que exigem atenção especial

Avalie cuidadosamente:

- se deve haver nova migration ou se o schema atual já suporta o fluxo;
- se `Settlement` já tem todos os campos necessários para snapshot cambial;
- se `OutboxEvent` já tem payload suficiente;
- se o estado do `Receivable` deve ser atualizado antes ou depois do `Settlement`;
- se a criação do `Settlement`, alteração do `Receivable` e criação do `OutboxEvent` estão na mesma transação;
- se o fluxo lida corretamente com concorrência e dupla liquidação;
- se o câmbio é aplicado somente no final.

## Condição de parada

Ao final da resposta, use exatamente:

```text
Aguardando aprovação para iniciar a implementação da etapa v0.5.0-settlement-flow.
```
