# Template de Postmortem

> Preencher este template em até 48 horas após a resolução do incidente.
> O postmortem não é para punir — é para entender o que aconteceu e evitar que aconteça de novo.
> Substituir todos os campos `[...]` com as informações reais do incidente.

---

## Cabeçalho

| Campo | Valor |
|---|---|
| **ID do incidente** | `[INC-YYYYMMDD-N]` |
| **Título** | `[Descrição curta do incidente]` |
| **Severidade** | `[SEV-1 / SEV-2 / SEV-3 / SEV-4]` |
| **Data de início** | `[YYYY-MM-DD HH:MM]` |
| **Data de resolução** | `[YYYY-MM-DD HH:MM]` |
| **Duração total** | `[X horas Y minutos]` |
| **Incident Commander** | `[Nome]` |
| **Owner do postmortem** | `[Nome]` |
| **Data do postmortem** | `[YYYY-MM-DD]` |

---

## Resumo Executivo

> Uma ou duas frases descrevendo o que aconteceu, o impacto e como foi resolvido. Deve ser legível por pessoas não técnicas.

`[Ex: No dia X, após a release da versão Y, o sistema Z parou de processar liquidações devido a Z. O incidente foi resolvido em N minutos com rollback/revert/hotfix. Nenhum dado foi perdido.]`

---

## Impacto

### Sistemas afetados

- `[Sistema / componente afetado 1]`
- `[Sistema / componente afetado 2]`

### Usuários afetados

- `[Número ou percentual de usuários afetados]`
- `[Tipo de usuário afetado: operadores de mesa, clientes, etc.]`

### Operações afetadas

- `[Tipo de operação: liquidações, precificação, relatórios, etc.]`
- `[Volume estimado de operações impactadas]`

### SLO violado?

- Disponibilidade: `[Sim/Não — valor medido vs. objetivo de 99.9%]`
- Latência P99: `[Sim/Não — valor medido vs. objetivo de < 500ms]`
- Taxa de erro: `[Sim/Não — valor medido vs. objetivo de < 0.1%]`

### Perda de dados?

`[Sim / Não. Se sim, descrever o que foi perdido e como recuperar.]`

---

## Timeline

| Hora (UTC-3) | Evento | Ator |
|---|---|---|
| `[HH:MM]` | `[Evento inicial — deploy, mudança de config, etc.]` | `[Sistema / Pessoa]` |
| `[HH:MM]` | `[Primeiro sinal do problema — alerta, usuário, log]` | `[Sistema / Pessoa]` |
| `[HH:MM]` | `[Incidente declarado / Incident Commander designado]` | `[Nome]` |
| `[HH:MM]` | `[Diagnóstico concluído — causa raiz identificada]` | `[Nome]` |
| `[HH:MM]` | `[Decisão de contenção tomada]` | `[Nome]` |
| `[HH:MM]` | `[Contenção executada]` | `[Nome]` |
| `[HH:MM]` | `[Validação confirmada — sistema recuperado]` | `[Nome]` |
| `[HH:MM]` | `[Incidente encerrado]` | `[Nome]` |

---

## Causa Raiz

### Causa imediata

> O que tecnicamente causou o incidente? Sem julgamento — apenas o fato.

`[Ex: O counter 'settlements.created.total' foi registrado com sufixo '_created', que é reservado pelo Prometheus Client 1.x no formato OpenMetrics, resultando na exposição da métrica com nome diferente do esperado.]`

### Causas contribuintes

> Fatores que tornaram possível ou agravaram o incidente.

- `[Ex: Falta de testes automatizados que validem os nomes das métricas no Prometheus]`
- `[Ex: Documentação do Prometheus Client 1.x não estava no radar durante a implementação]`
- `[Ex: Alertas não tinham verificação de que a métrica existia antes de usar]`

### O que NÃO foi a causa

> Hipóteses que foram investigadas e descartadas.

- `[Ex: Não foi um problema de configuração do Prometheus — o scrape estava funcionando]`
- `[Ex: Não foi um problema de rede — o endpoint /actuator/prometheus respondia normalmente]`

---

## Detecção

### Como foi detectado?

`[Ex: Alerta de dashboard zerado identificado pelo time de operações às 14:03.]`

### Tempo até detecção (TTD — Time to Detect)

`[Ex: 3 minutos após o deploy — alerta disparou quase imediatamente]`

### Poderia ter sido detectado antes?

`[Sim/Não. Se sim, como: testes de integração, smoke tests pós-deploy, checklist de release.]`

---

## Resposta

### O que foi feito para conter o incidente?

1. `[Ação 1 — ex: Incident Commander designado, equipe notificada]`
2. `[Ação 2 — ex: Causa raiz identificada em 15 minutos]`
3. `[Ação 3 — ex: git revert executado em branch fix/]`
4. `[Ação 4 — ex: PR revisado e mergeado]`
5. `[Ação 5 — ex: Deploy realizado, dashboards normalizados]`

### Decisão técnica tomada

`[Ex: git revert do commit abc1234 — escolhido por ser seguro em branch compartilhada e por não exigir código novo]`

---

## O Que Funcionou

> Reconhecer o que ajudou a resolver o incidente rapidamente.

- `[Ex: Alerta de dashboard zerado disparou em menos de 5 minutos]`
- `[Ex: Causa raiz identificada rapidamente graças ao log estruturado com correlation_id]`
- `[Ex: Processo de PR acelerado já estava documentado no branching-strategy.md]`
- `[Ex: Build local com ./mvnw clean verify identificou regressão antes do PR]`

---

## O Que Não Funcionou

> Honestidade sobre falhas no processo — sem julgamento, sem busca por culpados.

- `[Ex: Não havia smoke test pós-deploy validando os nomes das métricas Prometheus]`
- `[Ex: A decisão entre revert e hotfix levou 8 minutos desnecessários por falta de critério documentado]`
- `[Ex: Comunicação ao time de operações só ocorreu 20 minutos após o incidente ser detectado]`

---

## Ações Corretivas

| # | Ação | Owner | Prazo | Status |
|---|---|---|---|---|
| 1 | `[Ex: Adicionar teste automatizado de validação de nomes de métricas Prometheus]` | `[Nome]` | `[YYYY-MM-DD]` | `Pendente` |
| 2 | `[Ex: Documentar critérios de revert vs. hotfix no playbook]` | `[Nome]` | `[YYYY-MM-DD]` | `Pendente` |
| 3 | `[Ex: Adicionar smoke test pós-deploy ao checklist de release]` | `[Nome]` | `[YYYY-MM-DD]` | `Pendente` |
| 4 | `[Ex: Criar alerta Prometheus para detecção de métricas inesperadas]` | `[Nome]` | `[YYYY-MM-DD]` | `Pendente` |

---

## Follow-up

- [ ] Ações corretivas criadas como issues/cards no backlog
- [ ] Owners designados e prazos acordados
- [ ] Postmortem compartilhado com o time
- [ ] Checklist de release atualizado (se aplicável)
- [ ] ADR criada ou atualizada (se decisão arquitetural foi afetada)
- [ ] Documentação de observabilidade atualizada (se necessário)
- [ ] Incidente registrado no histórico de postmortems

---

## Assinatura

| Papel | Nome | Data |
|---|---|---|
| Incident Commander | `[Nome]` | `[YYYY-MM-DD]` |
| Tech Lead | `[Nome]` | `[YYYY-MM-DD]` |
| Owner do Postmortem | `[Nome]` | `[YYYY-MM-DD]` |
