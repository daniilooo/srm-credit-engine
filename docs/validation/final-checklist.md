# Checklist Final de Critérios de Aceite — SRM Credit Engine

Checklist verificável da entrega completa do projeto.

---

## Arquitetura

- [x] Modular monolith em camadas com fronteiras explícitas
- [x] Domínio (`domain`) isolado — sem dependência de Spring ou JPA nas regras de negócio
- [x] Casos de uso na camada `application` com `@Transactional`
- [x] Relatórios separados em camada `reporting` com SQL nativo
- [x] Outbox Pattern implementado (dispatcher não implementado — ver Limitações)
- [x] Justificativa arquitetural documentada em `docs/architecture/overview.md`
- [x] ADRs de 0001 a 0008 cobrindo todas as decisões relevantes

---

## Backend

### Fundação
- [x] Java 21
- [x] Spring Boot 4.1.0
- [x] PostgreSQL 16 como banco de dados
- [x] Flyway para versionamento do schema (V1 — schema completo)
- [x] `spring.jpa.hibernate.ddl-auto=validate` (schema nunca criado pelo Hibernate)

### Precisão Financeira
- [x] `BigDecimal` para todos os valores monetários, taxas e câmbio
- [x] Nenhum uso de `double` ou `float` em cálculos financeiros
- [x] Arredondamento com `RoundingMode.HALF_EVEN` em cálculos cambiais
- [x] `NUMERIC(19,10)` para taxas de câmbio no banco
- [x] `NUMERIC(19,4)` para valores financeiros no banco

### Precificação
- [x] Strategy Pattern: `MercantileDuplicatePricingStrategy` e `PostDatedCheckPricingStrategy`
- [x] `ReceivablePricingStrategyResolver` resolve a estratégia pelo código do tipo
- [x] Spread diferenciado por tipo (Duplicata: 0,4% a.m.; Cheque: 1,0% a.m.)
- [x] Câmbio aplicado após o cálculo do valor presente (não dentro do Pricing Engine)

### Liquidação
- [x] Fluxo transacional ACID com `@Transactional`
- [x] Três barreiras contra dupla liquidação (status check + UNIQUE + @Version)
- [x] Snapshot cambial imutável persistido no Settlement
- [x] OutboxEvent salvo na mesma transação
- [x] Sem liquidação parcial — rollback completo em caso de falha

### Câmbio
- [x] Pares BRL→USD e USD→BRL tratados como distintos e direcionais
- [x] Taxa mais recente por `valid_from DESC`, `created_at DESC`
- [x] Validação de taxa positiva e moedas distintas
- [x] Exceção clara (`CurrencyConversionException`) quando par não cadastrado

### API e Documentação
- [x] 5 endpoints REST versionados em `/api/v1`
- [x] Bean Validation em todos os request bodies
- [x] Tratamento global de exceções com status HTTP adequados
- [x] OpenAPI/Swagger via springdoc em `/swagger-ui/index.html`

### Relatórios
- [x] `NamedParameterJdbcTemplate` com SQL nativo para relatórios
- [x] Filtros dinâmicos por período, cedente e moeda
- [x] Paginação server-side (page/size com limite de 100)

### Qualidade
- [x] JUnit 5 + Mockito para testes unitários
- [x] 106 testes passando
- [x] Cobertura JaCoCo ≥ 90% em linhas
- [x] `./mvnw clean verify` passando
- [x] `scripts/pre-push.sh` passando

---

## Frontend

- [x] Angular 20.1.0 com componentes standalone
- [x] `signal()` para estado reativo (loading, resultado, erro)
- [x] `inject()` para injeção de dependência
- [x] `@if` / `@for` como blocos de controle de fluxo (Angular 17+)
- [x] `ReactiveFormsModule` com validações nos 4 formulários
- [x] 4 rotas com lazy loading: precificação, câmbio, liquidação, extrato
- [x] 4 serviços HTTP alinhados com os contratos do backend
- [x] Proxy de desenvolvimento para CORS (`proxy.conf.json`)
- [x] Build de produção (`npm run build`) passando

---

## Infraestrutura

- [x] Docker Compose com 4 serviços: PostgreSQL, backend, frontend (Nginx), Prometheus
- [x] Multi-stage Dockerfile para backend (Maven → JRE Alpine)
- [x] Multi-stage Dockerfile para frontend (Node → Nginx Alpine)
- [x] Docker Secrets para credenciais do banco (não variáveis de ambiente)
- [x] `.env.example` sem segredos reais
- [x] `.gitignore` cobrindo `.env`, `db_user`, `db_password`, `target/`, `dist/`, `node_modules/`
- [x] Nginx configurado com proxy reverso `/api` → backend e SPA routing
- [x] Healthcheck no PostgreSQL (`pg_isready`)
- [x] Healthcheck no backend (`wget /actuator/health`)
- [x] Prometheus configurado com scrape job do backend

---

## Observabilidade

- [x] Micrometer + `micrometer-registry-prometheus` no backend
- [x] `/actuator/health` com grupos `liveness` e `readiness`
- [x] `/actuator/metrics` e `/actuator/prometheus` expostos
- [x] 5 contadores de negócio registrados
- [x] 3 timers de negócio registrados
- [x] Tag `application=srm-credit-engine` em todas as métricas
- [x] Prometheus scrapando o backend (`up{job="srm-credit-engine-backend"} = 1`)

---

## Documentação

- [x] `README.md` completo com todas as seções
- [x] `AI_USAGE.md` com análise crítica real
- [x] ADRs 0001–0009
- [x] Diagrama C4 Contexto (Mermaid)
- [x] Diagrama C4 Container (Mermaid)
- [x] Diagrama ER com as 7 tabelas (Mermaid)
- [x] Documentação dos 5 endpoints REST
- [x] Guia Docker com setup de secrets
- [x] Documentação de observabilidade e métricas
- [x] Estratégia Git e branching
- [x] Checklist final

---

## Design de Escalabilidade e EDA (v0.12.0)

> Documentação de design evolutivo — proposta futura, não implementado.

- [x] `docs/scale/one-million-transactions.md` criado com premissas, gargalos, estratégias e roadmap
- [x] `docs/eda/event-driven-evolution.md` criado com eventos candidatos, produtores, consumidores e diagrama Mermaid
- [x] `docs/eda/outbox-pattern-evolution.md` criado com fluxo da `outbox_events`, publisher futuro e sequenceDiagram
- [x] `docs/eda/idempotency-and-retries.md` criado com 3 barreiras atuais, idempotency key, backoff e DLQ
- [x] `docs/eda/cqrs-reporting-evolution.md` criado com CQRS, projections, read model e trade-offs
- [x] `docs/observability/observability-at-scale.md` criado com SLI/SLO, tracing, logs estruturados e alertas
- [x] `docs/adr/0009-scale-and-eda-design.md` criado com justificativa da etapa documental e roadmap aprovado
- [x] `README.md` atualizado com seção de Scale/EDA e links
- [x] `AI_USAGE.md` atualizado com milestone v0.12.0
- [x] Todos os documentos em português do Brasil
- [x] Todos os documentos deixam claro que EDA/CQRS/Kafka são proposta futura
- [x] Nenhum código de produção alterado
- [x] Nenhum segredo real incluído

---

## Segurança e Boas Práticas

- [x] Nenhuma credencial versionada
- [x] Nenhum segredo em variável de ambiente no Docker Compose
- [x] Schema com constraints financeiras explícitas
- [x] Sem retorno de stack trace para o cliente
- [x] Validações na entrada (Bean Validation) e no domínio (exceções de negócio)

---

## Limitações Conhecidas

| Item | Status | Observação |
|---|---|---|
| GitHub Actions CI/CD | Não implementado | Planejado, fora do escopo do desafio |
| Grafana | Não implementado | Prometheus configurado; Grafana não |
| Resilience4j | Não implementado | Estava no escopo original, não executado |
| Testcontainers | Não implementado | Testes usam mocks; banco real não coberto por testes |
| Dispatcher do Outbox | Não implementado | Eventos são persistidos mas não publicados |
| Autenticação/autorização | Não implementado | Sem JWT, OAuth2 ou qualquer mecanismo de autenticação |
| Mais de 2 moedas | Não implementado | Apenas BRL e USD cadastrados no seed |
| Mais tipos de recebível | Não implementado | Apenas DUPLICATA e CHEQUE_PRE_DATADO |
| API externa de câmbio | Não implementado | Taxas registradas manualmente |
| Rate limiting | Não implementado | Sem proteção contra abuso de endpoints |
