# Observabilidade — Backend SRM Credit Engine

## Endpoints de Saúde e Monitoramento

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Status geral da aplicação com probes de liveness e readiness |
| `GET /actuator/health/liveness` | Probe de liveness para orquestrador de containers |
| `GET /actuator/health/readiness` | Probe de readiness para orquestrador de containers |
| `GET /actuator/metrics` | Lista de todos os IDs de métricas disponíveis |
| `GET /actuator/prometheus` | Métricas no formato texto Prometheus (OpenMetrics) |

### Exemplo de resposta de saúde

```json
{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}
```

As probes foram habilitadas em `application.yaml`:
```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

---

## Métricas de Negócio

Implementadas em `BusinessMetrics` (`infrastructure/observability/`), registradas via `MeterRegistry` do Micrometer com tag `application=srm-credit-engine`.

### Contadores (Counters)

| ID Micrometer | Nome Prometheus | Descrição |
|---|---|---|
| `pricing.simulations.total` | `pricing_simulations_total` | Total de simulações de precificação executadas com sucesso |
| `exchange.rates.registered.total` | `exchange_rates_registered_total` | Total de taxas de câmbio registradas |
| `settlements.created.total` | `settlements_total` ¹ | Total de liquidações criadas com sucesso |
| `settlements.failed.total` | `settlements_failed_total` | Total de tentativas de liquidação que falharam |
| `reports.settlement.queries.total` | `reports_settlement_queries_total` | Total de consultas válidas ao extrato analítico |

> ¹ **Nota sobre `settlements_total`:** O Prometheus Client 1.x (usado pelo Micrometer com Spring Boot 4.x) reserva `_created` como sufixo de timestamp de criação de contadores no formato OpenMetrics. Por isso, `settlements.created.total` é renderizado como `settlements_total` (com o segmento `_created` removido). O ID interno no Micrometer permanece `settlements.created.total` e é acessível via `/actuator/metrics/settlements.created.total`.

### Timers (Timers)

| ID Micrometer | Nome Prometheus (base) | Descrição |
|---|---|---|
| `pricing.simulation.duration` | `pricing_simulation_duration_seconds` | Tempo de execução de uma simulação de precificação |
| `settlement.execution.duration` | `settlement_execution_duration_seconds` | Tempo de execução do fluxo completo de liquidação |
| `report.settlement.query.duration` | `report_settlement_query_duration_seconds` | Tempo de execução de uma consulta ao extrato analítico |

Cada timer gera automaticamente no Prometheus:
- `*_seconds_count` — número de observações
- `*_seconds_sum` — soma acumulada dos tempos
- `*_seconds_max` — valor máximo no intervalo de coleta (gauge)

---

## Configuração do Prometheus

O arquivo `infra/prometheus/prometheus.yml` configura o scrape:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: "srm-credit-engine-backend"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["backend:8080"]
```

O Prometheus scrape o backend a cada **15 segundos**. No Docker Compose, o container `prometheus` depende do healthcheck do container `backend` estar `healthy` antes de iniciar.

---

## Exemplos de PromQL

```promql
# Total de simulações de precificação
pricing_simulations_total{application="srm-credit-engine"}

# Taxa de liquidações com sucesso por minuto (últimos 5 min)
rate(settlements_total{application="srm-credit-engine"}[5m])

# Taxa de falhas de liquidação
rate(settlements_failed_total{application="srm-credit-engine"}[5m])

# Latência média de liquidação (P50)
histogram_quantile(0.5,
  rate(settlement_execution_duration_seconds_bucket{application="srm-credit-engine"}[5m])
)

# Queries de relatório por minuto
rate(reports_settlement_queries_total{application="srm-credit-engine"}[5m])
```

---

## Consultar métricas individualmente

```bash
# Lista todos os IDs disponíveis
curl http://localhost:8080/actuator/metrics

# Valor atual de um counter específico
curl http://localhost:8080/actuator/metrics/pricing.simulations.total

# Valor atual do timer de liquidação
curl http://localhost:8080/actuator/metrics/settlement.execution.duration
```

---

## Onde as métricas são incrementadas

| Métrica | Classe | Momento |
|---|---|---|
| `pricing.simulations.total` | `PricingSimulationService` | Após cálculo bem-sucedido |
| `pricing.simulation.duration` | `PricingSimulationService` | Medido ao redor do cálculo |
| `exchange.rates.registered.total` | `RegisterExchangeRateUseCase` | Após persistência da taxa |
| `settlements.created.total` | `SettleReceivableUseCase` | Após commit transacional bem-sucedido |
| `settlements.failed.total` | `SettleReceivableUseCase` | Em qualquer exceção durante a liquidação |
| `settlement.execution.duration` | `SettleReceivableUseCase` | Sempre (via `finally`) |
| `reports.settlement.queries.total` | `SettlementReportService` | Após validação da query, antes da execução |
| `report.settlement.query.duration` | `SettlementReportService` | Medido ao redor da execução SQL |

---

## Evoluções Futuras Sugeridas

- **Grafana:** dashboard com painéis de negócio (liquidações por hora, taxa de falhas, latência percentil)
- **OpenTelemetry:** tracing distribuído para rastrear uma requisição de ponta a ponta
- **Alertas Prometheus:** alertar quando `rate(settlements_failed_total[5m]) > 0.1`
- **Logs estruturados:** correlação de logs com `traceId` para diagnóstico em produção
