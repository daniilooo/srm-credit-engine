# Prompt — Orquestrador — Planejamento — v0.8.0 Frontend Operator Panel

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/frontend-operator-panel
```

Milestone:

```text
v0.8.0-frontend-operator-panel
```

## Contexto

Etapas concluídas:

```text
v0.2.0-domain-model
v0.3.0-pricing-engine
v0.4.0-currency-engine
v0.5.0-settlement-flow
v0.6.0-rest-api
v0.7.0-reporting-api
```

A API REST já expõe:

```text
POST /api/v1/pricing/simulations
POST /api/v1/exchange-rates
GET  /api/v1/exchange-rates/latest
POST /api/v1/settlements
GET  /api/v1/reports/settlements
```

Agora devemos implementar o **Painel do Operador** no frontend Angular.

## Subagents disponíveis

Consulte e simule a análise dos subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_frontend_especialista.md
agents/agent_backend_especialista.md
agents/agent_qa_qualidade.md
agents/agent_devops_especialista.md
```

## Objetivo

Planejar a implementação do painel Angular com:

1. Simulação de pricing.
2. Registro de taxa de câmbio.
3. Consulta de taxa de câmbio mais recente.
4. Liquidação de recebível.
5. Extrato de liquidação com filtros:
   - período;
   - cedente;
   - moeda;
   - paginação server-side.

## Regras obrigatórias

1. Não alterar backend sem necessidade explícita.
2. Não alterar regras de negócio.
3. Não criar autenticação/autorização.
4. Não criar backend-for-frontend.
5. Não criar integração externa.
6. Não alterar Docker Compose sem aprovação.
7. Criar camada de services Angular para chamadas HTTP.
8. Criar modelos/interfaces TypeScript para requests/responses.
9. Separar UI Components de services.
10. Usar Reactive Forms.
11. Usar RxJS de forma controlada.
12. Usar tabela com paginação server-side para extrato.
13. Não hardcodar URLs espalhadas nos componentes.
14. Criar environment com baseUrl da API.
15. Tratar loading, success e error states.
16. Não usar `any` sem justificativa.
17. Manter build Angular passando.
18. Manter lint/test, se configurado.
19. Manter documentação mínima no README, se necessário.

## Telas candidatas

```text
/frontend
├── dashboard ou shell
├── pricing-simulation
├── exchange-rates
├── settlement
└── settlement-report
```

## Componentes candidatos

- `PricingSimulationComponent`
- `ExchangeRateFormComponent`
- `LatestExchangeRateComponent`
- `SettlementFormComponent`
- `SettlementReportComponent`
- `AppShellComponent`, se necessário
- componentes compartilhados para feedback/erro/loading, se viável

## Services candidatos

- `PricingApiService`
- `ExchangeRateApiService`
- `SettlementApiService`
- `ReportingApiService`

## Rotas candidatas

```text
/pricing
/exchange-rates
/settlements
/reports/settlements
```

## A resposta deve conter

1. Resumo da etapa.
2. Análise por subagent.
3. Decisão consolidada.
4. Escopo incluído.
5. Escopo fora da etapa.
6. Rotas/telas propostas.
7. Componentes propostos.
8. Services propostos.
9. Models TypeScript propostos.
10. Estratégia de forms e validação.
11. Estratégia de HTTP/error handling.
12. Estratégia de paginação server-side.
13. Estratégia de UX/loading/success/error.
14. Estratégia de testes, se aplicável.
15. Riscos técnicos.
16. Critérios de aceite.
17. Checklist de validação.
18. Sugestão de commits.
19. Sugestão de tag futura.
20. Pontos que exigem aprovação humana.

## Modo obrigatório

Nesta primeira resposta:

- não implemente código;
- não altere arquivos;
- gere apenas plano técnico consolidado;
- simule a análise dos subagents;
- liste pontos de aprovação humana;
- aguarde aprovação explícita.

Finalize exatamente com:

```text
Aguardando aprovação para iniciar a implementação da etapa v0.8.0-frontend-operator-panel.
```
