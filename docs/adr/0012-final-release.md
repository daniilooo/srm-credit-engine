# ADR 0012 — Decisão de Fechamento de Escopo — v1.0.0

**Status:** Aceito

**Data:** 2026-06-22

**Autores:** Equipe SRM Credit Engine

---

## Contexto

O SRM Credit Engine chegou à v0.14.0-ci-cd-pipeline com 13 milestones entregues cobrindo:

- domínio financeiro completo (schema, precificação, câmbio, liquidação, relatórios)
- API REST com OpenAPI/Swagger
- frontend Angular para operador de mesa
- dockerização completa com Docker Secrets
- observabilidade com Prometheus (8 métricas de negócio)
- documentação técnica completa (C4, ER, ADRs, system design, gestão de crise, EDA/escala)
- pipeline CI/CD com GitHub Actions

O projeto cobre o escopo do desafio técnico. Novos itens identificados durante o desenvolvimento (Grafana, Testcontainers, Resilience4j, dispatcher do Outbox, autenticação, API externa de câmbio, registry Docker) foram conscientemente excluídos do escopo.

---

## Decisão

**Congelar o escopo funcional em v1.0.0.** Nenhuma nova feature funcional será implementada nesta etapa. A v1.0.0 é uma release de **revisão, limpeza e fechamento** — garantindo que a documentação reflita fielmente o que está implementado, sem mascarar limitações conhecidas.

---

## Por Que Congelar o Escopo Funcional em v1.0.0

### 1. O desafio técnico está completo

Os critérios centrais do desafio — cessão de crédito, precificação, câmbio, liquidação transacional, extrato analítico, frontend, Docker, observabilidade — estão todos implementados e funcionando.

### 2. Adicionar features sem critério de aceite definido cria risco

Cada nova feature em um projeto financeiro exige:
- regras de negócio claras e aprovadas
- testes cobrindo cenários positivos e negativos
- documentação consistente

Adicionar Grafana, Resilience4j ou Testcontainers sem um escopo aprovado introduziria código incompleto ou inconsistências entre código e documentação.

### 3. Documentação honesta é mais valiosa que feature list inflada

Documentar limitações conhecidas (sem autenticação, sem Grafana, sem dispatcher) com honestidade demonstra maturidade de engenharia. Um projeto que declara claramente o que não faz é mais confiável do que um projeto que tenta cobrir tudo superficialmente.

### 4. Separação clara entre o implementado e o planejado

Os documentos de escala/EDA (v0.12.0), gestão de crise (v0.13.0) e ADRs anteriores já estabeleceram os próximos passos naturais. A v1.0.0 consolida esse estado, sem misturar decisões de produto com documentação de release.

---

## Por Que Priorizar Validação e Documentação

A v1.0.0 foca em:

1. **Consistência**: garantir que README, AI_USAGE, checklist e release notes reflitam o estado real do repositório
2. **Rastreabilidade**: histórico completo de milestones documentado em formato auditável
3. **Limpeza**: remoção de referências obsoletas (ex: "Sem GitHub Actions" após v0.14.0)
4. **Entregabilidade**: o projeto deve poder ser avaliado por um revisor sem ambiguidade sobre o que está ou não implementado

---

## Consequências

### Positivas

- Entrega limpa e auditável — avaliador encontra o que é descrito
- Documentação e código em sincronia — sem promessas não cumpridas
- Histórico de ADRs completo (0001–0012) cobrindo todo o ciclo de vida do projeto
- Release notes formais como artefato de entrega profissional

### Limitações Aceitas

Os seguintes itens foram conscientemente excluídos do escopo da v1.0.0 e documentados como próximos passos naturais:

| Item | Motivo da exclusão |
|---|---|
| Dispatcher do Outbox | Exige infraestrutura de mensageria (Kafka/RabbitMQ) fora do escopo |
| Autenticação (JWT/OAuth2) | Exige Spring Security, configuração de roles e testes específicos |
| Publicação de imagem Docker | Exige registry e secrets no GitHub — além do escopo do desafio |
| Grafana | Prometheus configurado; dashboard é evolução natural após release |
| Testcontainers | Cobertura JaCoCo de 96,9% sem banco real é suficiente para o escopo |
| Resilience4j | Nenhuma dependência externa real no projeto atual |
| API externa de câmbio | Taxas manuais suficientes para demonstrar o domínio |

---

## Próximos Passos Fora da Release

Ver `docs/release/v1.0.0-final-release-notes.md` — seção "Próximos Passos Naturais".

---

## Alternativas Consideradas

### Continuar adicionando features até cobrir todas as limitações

**Rejeitado.** Sem critérios de aceite definidos e sem prazo, cada feature nova cria risco de regressão e inconsistência. A abordagem correta é fechar a release com o que está completo e documentar o restante como roadmap.

### Remover limitações da documentação para parecer mais completo

**Rejeitado.** Mascarar limitações é antiético em documentação técnica e prejudica avaliadores que precisam entender o estado real do sistema. As limitações são documentadas honestamente em `README.md` e `docs/release/v1.0.0-final-release-notes.md`.

### Não criar ADR de fechamento

**Rejeitado.** O ADR 0012 registra formalmente a **decisão de estabilização** — que não é trivial. Decidir o que fica fora do escopo é uma decisão arquitetural tão importante quanto decidir o que entra.
