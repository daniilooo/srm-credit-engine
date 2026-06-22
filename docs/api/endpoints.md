# Documentação de Endpoints REST — SRM Credit Engine

Base URL: `http://localhost:8080`

Documentação interativa: `http://localhost:8080/swagger-ui/index.html`

Todos os endpoints aceitam e retornam `Content-Type: application/json`.

---

## 1. Câmbio

### POST /api/v1/exchange-rates

Registra uma taxa de câmbio para um par de moedas direcional.

**Pares suportados:** `BRL→USD` e `USD→BRL` (direcionais e distintos).

**Request:**
```json
{
  "baseCurrency": "BRL",
  "quoteCurrency": "USD",
  "rateValue": 5.2500000000,
  "validFrom": "2026-06-22T00:00:00Z"
}
```

| Campo | Tipo | Validação |
|---|---|---|
| `baseCurrency` | `CurrencyCode` (`BRL` ou `USD`) | Obrigatório |
| `quoteCurrency` | `CurrencyCode` (`BRL` ou `USD`) | Obrigatório, diferente de `baseCurrency` |
| `rateValue` | `BigDecimal` | Obrigatório, > 0 |
| `validFrom` | `OffsetDateTime` (ISO-8601) | Obrigatório |

**Response 201 Created:**
```json
{
  "baseCurrencyCode": "BRL",
  "quoteCurrencyCode": "USD",
  "rateValue": 5.2500000000,
  "validFrom": "2026-06-22T00:00:00Z",
  "capturedAt": "2026-06-22T00:00:00Z"
}
```

**Errors:**
- `400 Bad Request` — campos inválidos ou ausentes
- `422 Unprocessable Entity` — moedas iguais, taxa ≤ 0 ou moeda não cadastrada

---

### GET /api/v1/exchange-rates/latest

Busca a taxa de câmbio mais recente para um par direcional.

**Ordenação:** `valid_from DESC`, com `created_at DESC` como critério de desempate.

**Query params:**

| Parâmetro | Tipo | Exemplo |
|---|---|---|
| `base` | `CurrencyCode` | `BRL` |
| `quote` | `CurrencyCode` | `USD` |

**Exemplo:**
```
GET /api/v1/exchange-rates/latest?base=BRL&quote=USD
```

**Response 200 OK:**
```json
{
  "baseCurrencyCode": "BRL",
  "quoteCurrencyCode": "USD",
  "rateValue": 5.2500000000,
  "validFrom": "2026-06-22T00:00:00Z",
  "capturedAt": "2026-06-22T15:30:00Z"
}
```

**Errors:**
- `400 Bad Request` — parâmetros ausentes ou moeda inválida
- `404 Not Found` — nenhuma taxa cadastrada para o par informado

---

## 2. Precificação

### POST /api/v1/pricing/simulations

Calcula o valor presente de um recebível. Não persiste nenhum dado.

O prazo em meses é calculado pelo controller como `ceil(dias / 30)` a partir de `dueDate`.

**Request:**
```json
{
  "faceValue": 10000.00,
  "dueDate": "2026-09-22",
  "receivableType": "DUPLICATA",
  "baseTaxMonthly": 0.0150
}
```

| Campo | Tipo | Validação |
|---|---|---|
| `faceValue` | `BigDecimal` | Obrigatório, > 0 |
| `dueDate` | `LocalDate` (yyyy-MM-dd) | Obrigatório |
| `receivableType` | `String` | `DUPLICATA` ou `CHEQUE_PRE_DATADO` |
| `baseTaxMonthly` | `BigDecimal` | Obrigatório, > 0 |

**Fórmulas por tipo de recebível:**

| Tipo | Spread adicional | Fórmula do valor presente |
|---|---|---|
| `DUPLICATA` | 0,4% a.m. | `VP = VF / (1 + taxa + spread)^n` |
| `CHEQUE_PRE_DATADO` | 1,0% a.m. | `VP = VF / (1 + taxa + spread)^n` |

**Response 200 OK:**
```json
{
  "presentValue": 9561.23,
  "appliedTax": 0.0150,
  "appliedSpread": 0.0040,
  "termInMonths": 3
}
```

**Errors:**
- `400 Bad Request` — campos inválidos, tipo de recebível desconhecido

---

## 3. Liquidação

### POST /api/v1/settlements

Efetua a liquidação de um recebível de forma transacional e idempotente.

A taxa de câmbio é capturada como snapshot imutável no momento da liquidação.

**Request:**
```json
{
  "receivableId": "550e8400-e29b-41d4-a716-446655440000",
  "paymentCurrencyCode": "USD",
  "baseTaxMonthly": 0.0150
}
```

| Campo | Tipo | Validação |
|---|---|---|
| `receivableId` | `UUID` | Obrigatório |
| `paymentCurrencyCode` | `CurrencyCode` (`BRL` ou `USD`) | Obrigatório |
| `baseTaxMonthly` | `BigDecimal` | Obrigatório, > 0 |

**Response 201 Created:**
```json
{
  "settlementId": "7f000001-9999-0000-0000-000000000001"
}
```

**Errors:**

| Status | Situação |
|---|---|
| `400 Bad Request` | Campos inválidos ou ausentes |
| `404 Not Found` | Recebível não encontrado |
| `409 Conflict` | Recebível já liquidado (dupla liquidação) |
| `422 Unprocessable Entity` | Recebível com status inválido, vencimento no passado ou taxa de câmbio não cadastrada |

---

## 4. Extrato Analítico

### GET /api/v1/reports/settlements

Consulta liquidações com filtros opcionais e paginação server-side.

Implementado com `NamedParameterJdbcTemplate` e SQL nativo para performance em volumes elevados.

**Query params (todos opcionais):**

| Parâmetro | Tipo | Exemplo | Padrão |
|---|---|---|---|
| `from` | `LocalDate` (yyyy-MM-dd) | `2026-06-01` | — |
| `to` | `LocalDate` (yyyy-MM-dd) | `2026-06-30` | — |
| `assignorId` | `UUID` | `550e8400-...` | — |
| `currency` | `CurrencyCode` | `BRL` | — |
| `page` | `int` | `0` | `0` |
| `size` | `int` (1–100) | `20` | `20` |

**Exemplo:**
```
GET /api/v1/reports/settlements?from=2026-06-01&to=2026-06-30&currency=BRL&page=0&size=20
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "settlementId": "7f000001-9999-0000-0000-000000000001",
      "receivableId": "550e8400-e29b-41d4-a716-446655440000",
      "assignorId": "...",
      "assignorName": "Empresa XYZ Ltda",
      "settledAmount": 1925.00,
      "paymentCurrencyCode": "USD",
      "exchangeRateValue": 5.2500000000,
      "settledAt": "2026-06-22T15:45:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3
}
```

**Errors:**
- `400 Bad Request` — `from` posterior a `to`, page negativo, size fora de 1–100

---

## Endpoints de Infraestrutura

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Status da aplicação (`UP`/`DOWN`) com probes de liveness/readiness |
| `GET /actuator/metrics` | Lista de todos os IDs de métricas disponíveis |
| `GET /actuator/prometheus` | Métricas no formato Prometheus (texto) |
| `GET /swagger-ui/index.html` | Interface interativa OpenAPI/Swagger |
| `GET /v3/api-docs` | Especificação OpenAPI em JSON |
