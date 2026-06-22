# SRM Credit Engine

Plataforma de cessão de crédito multimoedas para simulação, precificação e liquidação de recebíveis financeiros.

---

## Visão Geral

O **SRM Credit Engine** é um sistema de backoffice financeiro construído como desafio técnico. Ele permite que operadores de mesa financeira:

- Cadastrem e consultem taxas de câmbio entre BRL e USD
- Simulem o valor presente de recebíveis com base em taxa, prazo e tipo
- Liquidem recebíveis de forma transacional e auditável
- Consultem o extrato analítico de liquidações com filtros e paginação

---

## Contexto de Negócio — Cessão de Crédito

Em operações de **FIDC (Fundo de Investimento em Direitos Creditórios)** e **cessão de crédito**, um cedente transfere recebíveis a um fundo ou comprador. O valor pago hoje (valor presente) é menor que o valor de face do título, refletindo o custo do dinheiro no tempo (taxa de desconto) e o risco do cedente.

O SRM Credit Engine implementa o motor central dessa operação:

```
Recebível (valor de face, vencimento, tipo)
  → Precificação (taxa base + spread por tipo)
  → Conversão cambial (se moeda de pagamento ≠ moeda do título)
  → Liquidação (persistência transacional com snapshot cambial)
  → Auditoria (Outbox Pattern para eventos rastreáveis)
```

---

## Funcionalidades Implementadas

| Funcionalidade | Status |
|---|---|
| Cadastro e consulta de taxas de câmbio (BRL/USD) | ✅ Implementado |
| Simulação de precificação (Duplicata e Cheque Pré-Datado) | ✅ Implementado |
| Liquidação transacional com snapshot cambial | ✅ Implementado |
| Proteção contra dupla liquidação (3 barreiras) | ✅ Implementado |
| Extrato analítico com filtros e paginação | ✅ Implementado |
| Painel Angular para operador de mesa | ✅ Implementado |
| Dockerização completa com secrets | ✅ Implementado |
| Observabilidade com Prometheus (8 métricas de negócio) | ✅ Implementado |
| OpenAPI/Swagger | ✅ Implementado |
| GitHub Actions CI/CD | ❌ Não implementado |
| Grafana | ❌ Não implementado |
| Dispatcher do Outbox | ❌ Não implementado (eventos persistidos, publicação não) |

---

## Stack Técnica

### Backend
| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| Spring Data JPA | (incluso no Boot) |
| Spring Validation | (incluso no Boot) |
| PostgreSQL driver | (incluso no Boot) |
| Flyway | (incluso no Boot) |
| springdoc-openapi | 3.0.3 |
| Micrometer + Prometheus registry | (incluso no Boot) |
| JUnit 5 + Mockito | (incluso no Boot test) |
| JaCoCo | 0.8.10 |

### Frontend
| Tecnologia | Versão |
|---|---|
| Angular | 20.1.0 |
| TypeScript | (incluso no Angular) |
| Angular Reactive Forms | (incluso no Angular) |

### Infraestrutura
| Tecnologia | Versão |
|---|---|
| PostgreSQL | 16 Alpine |
| Nginx | Alpine |
| Prometheus | latest |
| Docker Compose | v2 |
| Maven (build Docker) | 3.9 Eclipse Temurin 21 Alpine |
| Node (build Docker) | 22 Alpine |

---

## Arquitetura

O projeto é um **monólito modular em camadas**, com separação explícita de responsabilidades:

```
interfaces/rest   → Controllers, DTOs, tratamento de exceções HTTP
application       → Casos de uso, commands, orquestração de transações
domain            → Entidades, strategies de precificação, serviços de domínio
infrastructure    → JPA, JdbcTemplate, configurações, observabilidade
reporting         → Queries analíticas com SQL nativo (NamedParameterJdbcTemplate)
```

### Por que não Microserviços

O escopo do desafio não exige serviços independentes. A liquidação financeira exige **transação ACID** envolvendo três operações atômicas — em microserviços, isso exigiria sagas distribuídas. O monólito modular preserva consistência, reduz complexidade operacional e demonstra maturidade arquitetural por **evitar prematuridade**.

> Para cenários de escala extrema, o projeto poderia evoluir para arquitetura orientada a eventos com filas, CQRS, read replicas e workers assíncronos. O `outbox_events` é o ponto de extensão central para essa evolução.

Documentação completa: [`docs/architecture/overview.md`](docs/architecture/overview.md)

---

## Estrutura do Projeto

```
srm-credit-engine/
├── backend/                        # Spring Boot REST API
│   ├── src/main/java/
│   │   └── br/com/srm/creditengine/
│   │       ├── application/        # Casos de uso
│   │       ├── domain/             # Entidades e regras de negócio
│   │       ├── infrastructure/     # JPA, config, observabilidade
│   │       ├── interfaces/rest/    # Controllers e DTOs
│   │       └── reporting/          # Relatórios com SQL nativo
│   ├── src/main/resources/
│   │   └── db/migration/           # Migrações Flyway
│   ├── secrets/                    # Secrets locais (não versionados)
│   │   ├── db_user.example
│   │   └── db_password.example
│   └── Dockerfile
├── frontend/                       # Angular 20 SPA
│   ├── src/app/
│   │   ├── core/                   # Models e Services HTTP
│   │   └── features/               # Componentes lazy-loaded
│   ├── nginx.conf
│   └── Dockerfile
├── infra/
│   └── prometheus/
│       └── prometheus.yml
├── docs/                           # Documentação completa
│   ├── adr/                        # Architecture Decision Records (0001–0008)
│   ├── architecture/               # Visão geral arquitetural
│   ├── c4/                         # Diagramas C4 (Mermaid)
│   ├── er/                         # Diagrama ER (Mermaid)
│   ├── api/                        # Documentação de endpoints
│   ├── docker/                     # Guia Docker Compose
│   ├── observability/              # Métricas e Prometheus
│   ├── git/                        # Estratégia de branching
│   └── validation/                 # Checklist final
├── agents/                         # Personas dos agentes de IA
├── prompts/                        # Prompts por milestone
├── scripts/
│   └── pre-push.sh                 # Hook local de qualidade
├── docker-compose.yml
├── .env.example
├── AI_USAGE.md
└── README.md
```

---

## Como Rodar com Docker Compose

### Pré-requisitos

- Docker 24+ e Docker Compose v2
- 4 GB de RAM disponível

### 1. Clonar e configurar

```bash
git clone <url-do-repositório>
cd srm-credit-engine

# Copiar arquivo de configuração
cp .env.example .env

# Criar os secrets do banco de dados (use qualquer valor local)
echo "srm" > backend/secrets/db_user
echo "srm_password" > backend/secrets/db_password
```

### 2. Subir a stack

```bash
docker compose up --build
```

A primeira execução baixará imagens e compilará o projeto — pode levar de 5 a 10 minutos.

### 3. Validar

```bash
# Backend
curl http://localhost:8080/actuator/health
# Esperado: {"groups":["liveness","readiness"],"status":"UP"}

# Frontend (via Nginx)
curl -s -o /dev/null -w "%{http_code}" http://localhost:4200
# Esperado: 200

# API via proxy do Nginx
curl http://localhost:4200/api/v1/reports/settlements
# Esperado: 200 com JSON de extrato
```

### Portas

| Serviço | URL local |
|---|---|
| Painel Angular | http://localhost:4200 |
| REST API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Prometheus | http://localhost:9090 |

Guia completo com troubleshooting: [`docs/docker/running-with-docker.md`](docs/docker/running-with-docker.md)

---

## Como Criar `.env` e Secrets

O arquivo `.env` contém apenas configurações **não sensíveis**:

```
POSTGRES_DB=srm_credit_engine
POSTGRES_PORT=5432
BACKEND_PORT=8080
FRONTEND_PORT=4200
```

As credenciais do banco de dados são gerenciadas por **Docker Secrets** (arquivos em `backend/secrets/`), nunca por variáveis de ambiente. Os arquivos `db_user` e `db_password` são ignorados pelo `.gitignore`. Os arquivos `.example` mostram o formato esperado.

---

## Desenvolvimento Local (sem Docker)

### Backend

Pré-requisito: PostgreSQL 16 rodando localmente com usuário `srm` e banco `srm_credit_engine`.

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm start
```

O `npm start` usa `ng serve --proxy-config proxy.conf.json`, encaminhando `/api` para `localhost:8080`.

---

## Como Rodar os Testes

```bash
cd backend
./mvnw clean verify
```

Isso executa:
- Todos os 106 testes unitários
- Relatório JaCoCo em `target/site/jacoco/`
- Verificação de cobertura mínima (90% de linhas) — falha o build se não atingida

### Pre-push Hook

Para instalar o hook local que bloqueia push com build/testes/cobertura quebrados:

```bash
chmod +x scripts/pre-push.sh
cp scripts/pre-push.sh .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

---

## Endpoints da API

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/exchange-rates` | Registrar taxa de câmbio |
| `GET` | `/api/v1/exchange-rates/latest?base=BRL&quote=USD` | Consultar taxa mais recente |
| `POST` | `/api/v1/pricing/simulations` | Simular precificação |
| `POST` | `/api/v1/settlements` | Liquidar recebível |
| `GET` | `/api/v1/reports/settlements` | Extrato analítico (paginado) |

Documentação completa com exemplos de request/response: [`docs/api/endpoints.md`](docs/api/endpoints.md)

Documentação interativa: `http://localhost:8080/swagger-ui/index.html`

---

## Fluxo de Liquidação

```
POST /api/v1/settlements
  → Validar recebível (existe, status=REGISTERED, vencimento futuro)
  → Calcular prazo em meses (teto)
  → Calcular valor presente (Pricing Engine — Strategy por tipo)
  → Buscar taxa de câmbio (se moeda diferente)
  → Aplicar câmbio: settledAmount = presentValue × rateValue (HALF_EVEN)
  → Persistir Settlement (UNIQUE(receivable_id) ativado)
  → Atualizar Receivable.status = SETTLED (@Version incrementado)
  → Persistir OutboxEvent (status=PENDING)
  → Commit transacional — tudo ou nada
  → 201 Created {settlementId}
```

Diagrama de sequência completo: [`docs/architecture/overview.md`](docs/architecture/overview.md)

---

## Observabilidade

O backend expõe métricas via Micrometer + Prometheus registry.

### Endpoints de saúde

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```

### Métricas de negócio (8 métricas)

| Tipo | Métrica Prometheus | Descrição |
|---|---|---|
| Counter | `pricing_simulations_total` | Simulações executadas |
| Counter | `exchange_rates_registered_total` | Taxas de câmbio registradas |
| Counter | `settlements_total` | Liquidações criadas com sucesso |
| Counter | `settlements_failed_total` | Tentativas de liquidação com falha |
| Counter | `reports_settlement_queries_total` | Consultas ao extrato |
| Timer | `pricing_simulation_duration_seconds` | Latência de precificação |
| Timer | `settlement_execution_duration_seconds` | Latência de liquidação |
| Timer | `report_settlement_query_duration_seconds` | Latência de relatório |

Documentação completa: [`docs/observability/backend-observability.md`](docs/observability/backend-observability.md)

---

## Segurança e Secrets

- Credenciais do banco nunca em variáveis de ambiente — sempre Docker Secrets por arquivo
- `.env` não contém nenhum segredo — apenas portas e nome do banco
- `backend/secrets/db_user` e `backend/secrets/db_password` são ignorados pelo `.gitignore`
- Flyway executa com as credenciais lidas pelo `docker-entrypoint.sh` de `/run/secrets/`

---

## Estratégia Git

- **Modelo:** GitHub Flow simplificado — `main` estável, branches `feature/<milestone>`
- **Commits:** Conventional Commits (`feat`, `test`, `docs`, `build`, `infra`, `fix`)
- **Tags:** SemVer criadas após merge na `main` (nunca antes)
- **Pre-push:** Hook local bloqueia build/testes/cobertura quebrados

Guia completo: [`docs/git/branching-strategy.md`](docs/git/branching-strategy.md)

---

## Milestones e Tags

| Tag | Entrega |
|---|---|
| `v0.2.0-domain-model` | Schema financeiro e entidades JPA |
| `v0.3.0-pricing-engine` | Motor de precificação com Strategy Pattern |
| `v0.4.0-currency-engine` | Motor de câmbio com pares direcionais |
| `v0.5.0-settlement-flow` | Liquidação transacional ACID |
| `v0.6.0-rest-api` | API REST com OpenAPI/Swagger |
| `v0.7.0-reporting-api` | Extrato analítico com SQL nativo |
| `v0.8.0-frontend-operator-panel` | Painel Angular 20 standalone |
| `v0.9.0-full-docker-compose` | Dockerização completa com secrets |
| `v0.10.0-backend-observability` | Micrometer + Prometheus |
| `v0.11.0-documentation-system-design` | Documentação e system design |
| `v0.12.0-scale-and-eda-design` | Design de escalabilidade e EDA (Staff/Principal) |
| `v0.13.0-crisis-management-git-simulation` | Gestão de crise, incident response e Git recovery |

---

## Decisões Técnicas Principais

| Decisão | Justificativa |
|---|---|
| `BigDecimal` para todos os valores financeiros | Precisão decimal exata — `double` não é confiável para dinheiro |
| `RoundingMode.HALF_EVEN` | Arredondamento bancário (IEEE 754) — minimiza viés acumulado |
| Strategy Pattern no Pricing Engine | Extensível — novo tipo de recebível = nova classe, sem alterar existentes |
| UNIQUE(receivable_id) em settlements | Barreira de banco contra dupla liquidação concorrente |
| `@Version` em Receivable e Settlement | Optimistic locking — sem lock pessimista desnecessário |
| Snapshot cambial no Settlement | Auditoria imutável — a taxa do momento da liquidação fica gravada para sempre |
| NamedParameterJdbcTemplate nos relatórios | SQL nativo com filtros dinâmicos eficientes sem N+1 de ORM |
| Docker Secrets em vez de env vars | Credenciais montadas como arquivo, não visíveis em `docker inspect` |
| Proxy Nginx em vez de CORS no backend | Elimina configuração de CORS; frontend e API parecem mesma origem |

---

## Limitações Conhecidas

- **Sem autenticação:** acesso aberto para fins do desafio
- **Apenas BRL e USD:** outras moedas não cadastradas no seed
- **Apenas 2 tipos de recebível:** DUPLICATA e CHEQUE_PRE_DATADO
- **Taxas manuais:** sem integração com API externa de câmbio
- **Dispatcher do Outbox não implementado:** eventos são persistidos mas não publicados
- **Sem GitHub Actions:** CI/CD não configurado
- **Sem Grafana:** Prometheus configurado; visualização não
- **Sem Resilience4j:** planejado, não implementado
- **Sem Testcontainers:** testes usam mocks; banco real não coberto por testes de integração

---

## Próximos Passos

1. Implementar dispatcher do Outbox (worker ou CDC com Debezium)
2. Adicionar autenticação (JWT ou OAuth2/OIDC)
3. Configurar GitHub Actions CI/CD (build + testes + push de imagem)
4. Adicionar Grafana com dashboard de negócio
5. Implementar Testcontainers para testes de integração com banco real
6. Integrar com API externa de cotação (ex: Open Exchange Rates)
7. Adicionar rate limiting e proteção contra abuso
8. Evoluir relatórios com CQRS e read replica

---

## Gestão de Crise e Simulação Git

Documentação de incident response, git revert, hotfix, cherry-pick e postmortem — demonstração de maturidade operacional Sênior/Staff.

| Documento | Descrição |
|---|---|
| [`docs/crisis-management/incident-response-playbook.md`](docs/crisis-management/incident-response-playbook.md) | Playbook completo de resposta a incidentes |
| [`docs/crisis-management/git-revert-simulation.md`](docs/crisis-management/git-revert-simulation.md) | Simulação de `git revert` com timeline do incidente |
| [`docs/crisis-management/hotfix-and-cherry-pick.md`](docs/crisis-management/hotfix-and-cherry-pick.md) | Estratégias de hotfix e cherry-pick com decisão |
| [`docs/crisis-management/postmortem-template.md`](docs/crisis-management/postmortem-template.md) | Template reutilizável de postmortem |
| [`docs/crisis-management/example-postmortem-observability-metric.md`](docs/crisis-management/example-postmortem-observability-metric.md) | Exemplo preenchido — incidente de métrica Prometheus |
| [`docs/adr/0010-crisis-management-git-strategy.md`](docs/adr/0010-crisis-management-git-strategy.md) | ADR 0010 — estratégia Git para emergências |

---

## Design de Escalabilidade e EDA

Documentação arquitetural de como o SRM Credit Engine evoluiria para alto volume e arquitetura orientada a eventos — demonstração de maturidade Staff/Principal. **Proposta futura — não implementado.**

| Documento | Descrição |
|---|---|
| [`docs/scale/one-million-transactions.md`](docs/scale/one-million-transactions.md) | Design para 1 milhão de transações/minuto |
| [`docs/eda/event-driven-evolution.md`](docs/eda/event-driven-evolution.md) | Evolução para Event-Driven Architecture |
| [`docs/eda/outbox-pattern-evolution.md`](docs/eda/outbox-pattern-evolution.md) | Outbox Publisher, fluxo transacional e retention |
| [`docs/eda/idempotency-and-retries.md`](docs/eda/idempotency-and-retries.md) | Idempotência, DLQ e estratégia de retry |
| [`docs/eda/cqrs-reporting-evolution.md`](docs/eda/cqrs-reporting-evolution.md) | CQRS, projeções e read model para relatórios |
| [`docs/observability/observability-at-scale.md`](docs/observability/observability-at-scale.md) | SLI/SLO, tracing distribuído e alertas em escala |
| [`docs/adr/0009-scale-and-eda-design.md`](docs/adr/0009-scale-and-eda-design.md) | ADR 0009 — justificativa da etapa documental |

---

## Documentação Completa

| Documento | Descrição |
|---|---|
| [`docs/architecture/overview.md`](docs/architecture/overview.md) | Arquitetura em camadas, justificativa e fluxo de liquidação |
| [`docs/c4/context.md`](docs/c4/context.md) | Diagrama C4 Contexto |
| [`docs/c4/container.md`](docs/c4/container.md) | Diagrama C4 Container |
| [`docs/er/er-diagram.md`](docs/er/er-diagram.md) | Diagrama ER com as 7 tabelas |
| [`docs/api/endpoints.md`](docs/api/endpoints.md) | Todos os endpoints com exemplos |
| [`docs/docker/running-with-docker.md`](docs/docker/running-with-docker.md) | Setup Docker completo |
| [`docs/observability/backend-observability.md`](docs/observability/backend-observability.md) | Métricas e Prometheus (implementado) |
| [`docs/observability/observability-at-scale.md`](docs/observability/observability-at-scale.md) | Observabilidade em escala (proposta futura) |
| [`docs/git/branching-strategy.md`](docs/git/branching-strategy.md) | Branches, commits e tags |
| [`docs/validation/final-checklist.md`](docs/validation/final-checklist.md) | Checklist de critérios de aceite |
| [`docs/crisis-management/`](docs/crisis-management/) | Playbook, simulação Git, postmortem e hotfix |
| [`docs/adr/`](docs/adr/) | ADRs 0001–0010 (todas as decisões arquiteturais) |
| [`AI_USAGE.md`](AI_USAGE.md) | Uso de IA com análise crítica |

---

## Uso de IA

Este projeto foi desenvolvido com apoio de ferramentas de IA (Claude, GPT, GitHub Copilot, OpenAI Codex) usando um modelo estruturado com subagentes especializados e aprovação humana em cada etapa.

Detalhes completos, decisões aceitas, decisões rejeitadas e análise crítica: [`AI_USAGE.md`](AI_USAGE.md)
