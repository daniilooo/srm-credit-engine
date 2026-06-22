# Postmortem — Métrica de Liquidação com Nome Inesperado no Prometheus

> **SIMULAÇÃO DOCUMENTAL**
> Este postmortem é fictício e serve como exemplo de preenchimento do [`postmortem-template.md`](./postmortem-template.md).
> O incidente, os nomes e as datas são hipotéticos. O comportamento técnico descrito (sufixo `_created` reservado pelo Prometheus Client 1.x) é real e está documentado em [`docs/observability/backend-observability.md`](../observability/backend-observability.md).

---

## Cabeçalho

| Campo | Valor |
|---|---|
| **ID do incidente** | `INC-20260415-001` |
| **Título** | Métrica `settlements.created.total` exposta como `settlements_total` no Prometheus após release v0.10.0 |
| **Severidade** | SEV-3 |
| **Data de início** | 2026-04-15 14:03 |
| **Data de resolução** | 2026-04-15 14:50 |
| **Duração total** | 47 minutos |
| **Incident Commander** | Danilo Franco |
| **Owner do postmortem** | Danilo Franco |
| **Data do postmortem** | 2026-04-16 |

---

## Resumo Executivo

Após a release da milestone v0.10.0-backend-observability, o counter de liquidações bem-sucedidas apareceu no Prometheus com o nome `settlements_total` em vez do esperado `settlements_created_total`. Dashboards e alertas configurados com o nome antigo deixaram de funcionar silenciosamente. Nenhuma liquidação foi afetada — o problema era exclusivamente de observabilidade. O incidente foi resolvido em 47 minutos com a documentação do comportamento esperado e atualização dos dashboards afetados.

---

## Impacto

### Sistemas afetados

- Prometheus (scrape do backend SRM Credit Engine)
- Dashboards de operações com painel "Total de Liquidações"
- Alertas configurados com `rate(settlements_created_total[5m]) > 0`

### Usuários afetados

- 2 operadores de mesa do time de operações com acesso ao dashboard
- Nenhum cliente final afetado

### Operações afetadas

- Dashboards de monitoramento de liquidações: dados zerados durante 47 minutos
- Alertas de liquidação: silenciados durante 47 minutos (não disparariam mesmo com volume normal)

### SLO violado?

- Disponibilidade: **Não** — sistema funcionando normalmente
- Latência P99: **Não** — liquidações respondendo normalmente
- Taxa de erro: **Não** — zero erros de liquidação durante o período

### Perda de dados?

**Não.** Todas as liquidações foram processadas e persistidas normalmente. O problema foi exclusivamente na nomenclatura da métrica de observabilidade.

---

## Timeline

| Hora (BRT) | Evento | Ator |
|---|---|---|
| 13:55 | Deploy da v0.10.0-backend-observability concluído | CI/CD automatizado |
| 14:03 | Operador percebe que painel "Total de Liquidações" está zerado no Grafana | Operador A |
| 14:05 | Verifica se o backend está saudável: `curl /actuator/health` retorna `UP` | Operador A |
| 14:06 | Incidente declarado — INC-20260415-001 — SEV-3 | Danilo Franco (IC) |
| 14:08 | Verificação: `curl /actuator/metrics/settlements.created.total` retorna valor correto | Tech Lead |
| 14:12 | Verificação: `curl /actuator/prometheus | grep settlement` mostra `settlements_total`, não `settlements_created_total` | Tech Lead |
| 14:15 | Causa raiz identificada: sufixo `_created` reservado pelo Prometheus Client 1.x (OpenMetrics) | Tech Lead |
| 14:18 | Decisão: não revert — o comportamento é documentado, não um bug; solução é atualizar dashboards e documentar | Incident Commander |
| 14:22 | Dashboards atualizados para usar `settlements_total` | Operador B |
| 14:30 | Alertas Prometheus atualizados para o novo nome | DevOps |
| 14:45 | Validação: dashboards mostrando dados corretos, alertas funcionando | Operador A |
| 14:50 | Incidente encerrado — SEV-3 resolvido | Danilo Franco (IC) |

---

## Causa Raiz

### Causa imediata

O counter `settlements.created.total` foi registrado no Micrometer com o segmento `created` no nome. O Prometheus Client 1.x, ao exportar métricas no formato OpenMetrics, **remove o sufixo `_created`** de nomes de counters porque esse sufixo é reservado para timestamps de criação de counters (`_created` é um campo especial do formato OpenMetrics). O resultado é que `settlements.created.total` (com `.` convertido para `_`) se torna `settlements_total` no Prometheus, e não `settlements_created_total` como os dashboards esperavam.

### Causas contribuintes

1. **Falta de teste automatizado** validando os nomes das métricas expostas pelo Prometheus
2. **Convenção de nomeação** seguiu padrão `<agregado>.<ação>.total` sem verificar conflito com sufixos reservados do OpenMetrics
3. **Dashboards configurados antes da release** assumiram o nome `settlements_created_total` sem smoke test de validação pós-deploy
4. **Documentação do Prometheus Client 1.x** sobre o comportamento do sufixo `_created` não estava no radar durante a implementação da v0.10.0

### O que NÃO foi a causa

- Não foi falha no scrape do Prometheus — o job `srm-credit-engine-backend` estava `up = 1`
- Não foi problema de rede ou conectividade entre Prometheus e backend
- Não foi bug no cálculo ou persistência de liquidações — a lógica de negócio estava correta
- Não foi um bug do Micrometer — o ID interno `settlements.created.total` estava correto e acessível via `/actuator/metrics`

---

## Detecção

### Como foi detectado?

Operador de mesa percebeu o painel "Total de Liquidações" zerado no dashboard ao fazer a verificação de rotina após o deploy.

### Tempo até detecção (TTD)

8 minutos após o deploy (13:55 → 14:03).

### Poderia ter sido detectado antes?

Sim. Um **smoke test pós-deploy** que verificasse os nomes de métricas no endpoint `/actuator/prometheus` teria detectado o problema imediatamente após o deploy, antes que qualquer operador fosse impactado.

---

## Resposta

### O que foi feito para conter o incidente?

1. Incidente declarado com SEV-3 às 14:06
2. Saúde do sistema verificada — backend `UP`, liquidações funcionando
3. Diagnóstico focado em observabilidade (não em dados)
4. Causa raiz identificada em 9 minutos (14:06 → 14:15)
5. Decisão de **não reverter** o código — o comportamento é determinístico e documentado
6. Dashboards e alertas atualizados para o nome real da métrica (`settlements_total`)
7. Documentação atualizada em `docs/observability/backend-observability.md` com nota sobre o sufixo

### Decisão técnica tomada

**Não fazer revert.** O comportamento do sufixo `_created` é determinístico no Prometheus Client 1.x — qualquer renomeação do counter teria que evitar o segmento `_created` no nome. A decisão foi documentar o comportamento como esperado e atualizar os dashboards. Uma renomeação futura (ex: `settlements.completed.total`) poderá ser feita na próxima release regular, sem urgência.

---

## O Que Funcionou

- Detecção rápida: 8 minutos após o deploy
- Diagnóstico preciso: `/actuator/metrics` vs. `/actuator/prometheus` permitiu isolar o problema em minutos
- Decisão correta de não reverter: evitou impacto desnecessário em uma funcionalidade operacional
- Comunicação clara ao time: todos sabiam o que estava acontecendo e qual era a decisão

---

## O Que Não Funcionou

- **Sem smoke test pós-deploy** para validar nomes de métricas — o problema poderia ter sido detectado antes de qualquer operador perceber
- **Dashboards configurados antes da release** sem sincronização com a nomeação real das métricas
- **8 minutos de discussão** sobre se seria um bug ou comportamento esperado — falta de documentação prévia do comportamento do OpenMetrics

---

## Ações Corretivas

| # | Ação | Owner | Prazo | Status |
|---|---|---|---|---|
| 1 | Adicionar nota clara na documentação de observabilidade sobre o sufixo `_created` e o nome Prometheus real | Danilo Franco | 2026-04-16 | **Concluído** (em `backend-observability.md`) |
| 2 | Criar smoke test pós-deploy que valide os nomes das métricas expostas no `/actuator/prometheus` | Tech Lead | 2026-04-30 | Pendente |
| 3 | Adicionar checklist de release com verificação de nomes de métricas antes de atualizar dashboards | DevOps | 2026-04-22 | Pendente |
| 4 | Avaliar renomeação do counter para `settlements.completed.total` na próxima release menor | Tech Lead | 2026-05-15 | Pendente |
| 5 | Documentar convenção de nomeação de métricas que evite conflito com sufixos reservados OpenMetrics | Danilo Franco | 2026-04-30 | Pendente |

---

## Follow-up

- [x] Documentação de observabilidade atualizada com nota sobre `settlements_total`
- [ ] Smoke test pós-deploy criado como issue no backlog
- [ ] Checklist de release atualizado
- [x] Postmortem compartilhado com o time
- [x] ADR 0010 criada documentando a estratégia de Git para crise
- [ ] Convenção de nomeação de métricas documentada no próximo sprint

---

## Assinatura

| Papel | Nome | Data |
|---|---|---|
| Incident Commander | Danilo Franco | 2026-04-16 |
| Tech Lead | Danilo Franco | 2026-04-16 |
| Owner do Postmortem | Danilo Franco | 2026-04-16 |

---

## Lições Aprendidas

> Seção adicional ao template padrão — incluir quando o incidente gerar aprendizados de ampla aplicabilidade.

1. **O nome interno no Micrometer e o nome exposto no Prometheus podem divergir.** Sempre verificar o endpoint `/actuator/prometheus` após adicionar novas métricas — não apenas `/actuator/metrics`.

2. **Dashboards devem ser parte do processo de release.** Se a release adiciona novas métricas, os dashboards afetados devem ser revisados antes (ou imediatamente após) o deploy.

3. **Decidir não reverter também é uma decisão válida.** Quando o comportamento é determinístico e a solução é atualizar a camada de observabilidade (não o código), o revert introduziria instabilidade desnecessária.

4. **Smoke tests de observabilidade são tão importantes quanto smoke tests de funcionalidade.** Um sistema onde as métricas não refletem a realidade é operacionalmente cego — e isso é crítico em domínio financeiro.
