# Uso de Inteligência Artificial — SRM Credit Engine

## Objetivo

Este documento descreve como ferramentas de IA foram utilizadas no desenvolvimento do SRM Credit Engine, quais decisões foram influenciadas por sugestões de IA, quais foram rejeitadas ou revisadas, e como o processo humano de supervisão foi mantido ao longo de todo o desenvolvimento.

O objetivo é ser transparente sobre o papel da IA no projeto — nem omitindo seu uso, nem superestimando sua autonomia.

---

## Ferramentas Utilizadas

| Ferramenta | Uso no projeto |
|---|---|
| **Claude** (Anthropic) | Principal ferramenta de desenvolvimento — geração de planos técnicos, implementação de código Java e TypeScript, revisão de arquitetura, documentação e análise de trade-offs |
| **GPT** (OpenAI) | Consultas pontuais sobre Mermaid, configuração de Docker Secrets e comportamentos específicos do Micrometer/Prometheus |
| **GitHub Copilot** | Autocompletar em IDE para trechos repetitivos (imports, boilerplate de construtores, configurações YAML) |
| **OpenAI Codex** | Apoio inicial na geração de estrutura de diretórios e nomes de classes no início do projeto |

---

## Modelo de Desenvolvimento com Subagentes

O projeto utilizou um modelo estruturado de desenvolvimento assistido por IA, com **agentes especializados** definidos em `agents/`:

### Subagentes Especializados

| Agente | Arquivo | Responsabilidade |
|---|---|---|
| **Analista de Requisitos** | `agend_analista_requisitos.md` | Requisitos funcionais, histórias de usuário, casos de borda, priorização por prazo |
| **Arquiteto de Sistemas** | `agend_arquiteto_sistemas.md` | Decisões arquiteturais, ADRs, diagramas C4, justificativas de trade-off |
| **Backend Especialista** | `agend_backend_especialista.md` | API REST, domínio financeiro, transações, testes, observabilidade |
| **Frontend Especialista** | `agend_frontend_especialista.md` | Angular, componentes standalone, signals, formulários reativos |
| **DevOps Especialista** | `agend_devops_especialista.md` | Docker, Dockerfile, secrets, healthchecks, estratégia de branching |
| **QA/Qualidade** | `agend_qa_qualidade.md` | Estratégia de testes, cenários de borda, cobertura JaCoCo, pirâmide de testes |
| **Orquestrador** | `agend_orquestrador.md` | Consolidação de visões, resolução de conflitos, geração de plano técnico, validação de escopo |

### Papel do Agent Orquestrador

O **Orquestrador** era o ponto de entrada para cada milestone. Ele:

1. Interpretava o objetivo da etapa e classificava a natureza do problema
2. Identificava quais subagentes eram relevantes para cada decisão
3. Consolidava perspectivas potencialmente conflitantes em uma solução coesa
4. Propunha o plano técnico com decisões justificadas
5. Identificava riscos e pontos de atenção
6. Gerava o relatório final de implementação

Exemplo real: para a etapa de observabilidade (v0.10.0), o Orquestrador identificou que o Backend Especialista queria usar `Timer.start()` em cada serviço (importando Micrometer diretamente), o DevOps queria centralizar métricas em um único componente, e o QA queria que os testes não dependessem de mocks de `MeterRegistry`. A decisão consolidada foi: criar `BusinessMetrics` em `infrastructure/observability/` que centraliza todos os registros, e usar `SimpleMeterRegistry` nos testes — eliminando a necessidade de mock.

---

## Fluxo de Trabalho por Etapa

Em cada uma das 14 milestones de implementação, o fluxo foi:

```
1.  Definição da milestone (escopo, critérios de aceite, restrições)
2.  Criação de prompt específico da etapa em prompts/<milestone>/
3.  Claude lê o CLAUDE.md e o prompt orquestrador da etapa
4.  Claude gera plano técnico consolidado (visão dos subagentes)
5.  Plano passa por aprovação humana com decisões finais
6.  Após aprovação explícita, Claude implementa em passos pequenos
7.  Claude gera relatório de implementação (arquivos, decisões, validações)
8.  Humano revisa git status, escopo e integridade do build
9.  Commits organizados por responsabilidade (feat, test, docs, infra)
10. PR aberto (simulado no desafio individual)
11. Tag SemVer criada somente após merge na main
```

Este fluxo garantiu que **nenhuma decisão técnica relevante foi tomada de forma autônoma pela IA** — toda implementação foi precedida de um plano aprovado pelo desenvolvedor.

---

## Uso por Milestone

| Milestone | Principal uso da IA |
|---|---|
| v0.2.0 | Schema financeiro, entidades JPA, constraints, ADR 0001–0004 |
| v0.3.0 | Strategy Pattern, BigDecimal, JaCoCo, pre-push hook |
| v0.4.0 | Currency engine, pares direcionais, ExchangeRateLookupService |
| v0.5.0 | Settlement flow transacional, 3 barreiras anti-dupla-liquidação, OutboxEvent |
| v0.6.0 | Controllers REST, DTOs, exception handlers, springdoc/OpenAPI |
| v0.7.0 | SQL nativo com NamedParameterJdbcTemplate, paginação, filtros dinâmicos |
| v0.8.0 | Angular 20 standalone, signals, lazy routing, proxy de desenvolvimento |
| v0.9.0 | Multi-stage Dockerfiles, Docker Secrets, Nginx proxy reverso |
| v0.10.0 | BusinessMetrics, Micrometer counters/timers, Prometheus scrape |
| v0.11.0 | Documentação completa, diagramas C4/ER, ADRs, README, AI_USAGE |
| v0.12.0 | Design de escala Staff/Principal: EDA, CQRS, Outbox evolution, SLI/SLO, roadmap incremental |
| v0.13.0 | Gestão de crise: incident response playbook, git revert simulation, hotfix, postmortem template |
| v0.14.0 | Pipeline CI com GitHub Actions: 4 jobs independentes (backend, frontend, docker, security-checks), documentação do pipeline, ADR 0011 |
| v1.0.0 | Release final: revisão e limpeza do README, release notes, ADR 0012 de fechamento de escopo, checklist final |

---

## Exemplos de Prompts Estratégicos

O arquivo `CLAUDE.md` na raiz do projeto funcionou como o prompt mestre do projeto — definindo stack, regras de arquitetura, escopo de cada milestone e restrições de segurança. Cada subdiretório em `prompts/` contém os prompts específicos de cada etapa.

Exemplo de instrução no CLAUDE.md que moldou decisões importantes:
```
Não usar double ou float em valores financeiros.
Usar BigDecimal para valores monetários, taxas e câmbio.
```

Essa regra foi aplicada rigorosamente em todas as 14 milestones, sem exceção.

---

## Decisões Aceitas

| Decisão | Origem | Resultado |
|---|---|---|
| `SimpleMeterRegistry` nos testes de métricas | IA sugeriu; humano aprovou | Eliminou necessidade de mocks complexos de `MeterRegistry` |
| `Timer.Sample` em `SettleReceivableUseCase` com try/catch/finally | IA propôs; humano aprovou | Captura falhas e sucesso corretamente sem alterar o fluxo existente |
| Refatoração para `executeInternal()` no `SettleReceivableUseCase` | IA propôs; humano aprovou | Separou o wrapper de métricas da lógica de negócio |
| Docker Secrets em vez de credenciais no `.env` | IA propôs; humano aprovação explícita | Credenciais nunca expostas em variáveis de ambiente |
| Proxy Angular (`proxy.conf.json`) em vez de CORS no backend | IA propôs; humano aprovou | Eliminou necessidade de configuração CORS em desenvolvimento |
| `NamedParameterJdbcTemplate` para relatórios em vez de JPA | IA propôs; humano aprovou | SQL nativo com filtros dinâmicos performáticos sem N+1 |

---

## Decisões Rejeitadas ou Revisadas

| Decisão | Proposta por | Motivo da Rejeição |
|---|---|---|
| **Microserviços reais** | Escopo inicial do desafio | Aumentaria complexidade operacional sem benefício; domínio exige ACID; prazo não comportaria |
| **Recalcular `presentValue` no relatório** | Análise inicial | `presentValue` não é persistido; recalcular exigiria refazer a lógica de precificação com dados históricos — problemático e fora do escopo |
| **CORS no backend para desenvolvimento** | Abordagem convencional | Resolvido com proxy Angular — mais simples e sem risco de vazamento de configuração CORS para produção |
| **Credenciais no `.env`** | Abordagem convencional de Docker Compose | Docker Secrets por arquivo é mais seguro; credenciais nunca aparecem em `docker inspect` ou logs |
| **Grafana** | Escopo original da observabilidade | Prometheus configurado e funcional; Grafana ficou de fora para priorizar outras entregas |
| **Resilience4j** | Agente backend especialista | Planejado mas não implementado; nenhuma dependência externa real exigiria resiliência neste escopo |
| **Testcontainers** | Agente QA | Planejado mas não implementado; cobertura JaCoCo ≥ 90% atingida com testes unitários + testes com Spring Boot Test |

---

## Erros e Casos que Exigiram Intervenção Humana

### Spring Boot 4 — Mudança de pacotes de teste
O Claude inicialmente sugeriu imports de `spring-boot-starter-test` com classes que mudaram de pacote no Spring Boot 4 (ex: `@WebMvcTest`, `MockMvc`). A dependência `spring-boot-starter-webmvc-test` precisou ser adicionada explicitamente ao `pom.xml` em vez de depender do starter padrão — fato identificado durante a tentativa de compilação.

### springdoc-openapi versão incompatível
A versão padrão do `springdoc-openapi-starter-webmvc-ui` sugerida pelo Claude não era compatível com Spring Boot 4.1.0. Foi necessário pesquisar e especificar a versão `3.0.3` explicitamente no `pom.xml`.

### `presentValue` não persistido
O Claude inicialmente considerou incluir o `presentValue` no extrato analítico. O humano rejeitou porque esse campo não está na tabela `settlements` — foi uma decisão arquitetural consciente do snapshot (o que importa para auditoria é o valor liquidado final, não o valor presente intermediário).

### Métrica `settlements.created.total` → Prometheus `settlements_total`
O Claude não antecipou que o Prometheus Client 1.x (usado no Spring Boot 4.x) remove o sufixo `_created` de contadores por ser reservado pelo formato OpenMetrics como timestamp de criação. A métrica é exposta como `settlements_total` em vez de `settlements_created_total`. Identificado e documentado — sem necessidade de renomear, pois o ID Micrometer interno permanece correto.

### Angular `replace` pipe inexistente
O Claude gerou `| slice:0:19 | replace:'T':' '` em template Angular para formatar datas. O pipe `replace` não existe no Angular padrão. Corrigido para chamada JavaScript direta no template: `item.settledAt.slice(0, 19).replace('T', ' ')`.

---

## Validação Manual

Todas as decisões abaixo foram **revisadas e validadas pelo desenvolvedor** sem delegar à IA:

- Precisão dos cálculos financeiros com `BigDecimal` e `RoundingMode.HALF_EVEN`
- Ordem das operações na transação de liquidação (Settlement antes de Receivable para ativar UNIQUE primeiro)
- Estratégia de 3 barreiras contra dupla liquidação
- Decisão de usar snapshot cambial em vez de recalcular taxa futuramente
- Critérios de cobertura JaCoCo (90% de linhas)
- Conteúdo dos ADRs como registro fiel das decisões
- Que nenhum segredo real fosse versionado

---

## Valor Agregado pela IA

- **Velocidade de implementação:** A IA permitiu implementar 14 milestones em prazo curto, mantendo qualidade arquitetural
- **Consistência:** Regras definidas no `CLAUDE.md` foram aplicadas uniformemente em todas as etapas
- **Documentação:** A geração de ADRs, diagramas Mermaid e documentação técnica seria muito mais lenta manualmente
- **Revisão crítica:** A IA identificou proativamente riscos como dupla liquidação, arredondamento cambial e isolamento do domínio

---

## Pontos que Exigiram Revisão Humana

- Toda decisão financeira (arredondamento, precisão, fórmulas)
- Toda decisão de segurança (secrets, CORS, exposição de dados)
- Validação de que o código gerado compila e passa nos testes reais
- Confirmação de que nada fora do escopo aprovado foi implementado
- Revisão do `git status` após cada etapa para garantir integridade

---

## Riscos do Uso de IA em Projetos Financeiros

| Risco | Como foi mitigado |
|---|---|
| Alucinação de APIs ou comportamentos de framework | Sempre compilar e testar o código gerado antes de aceitar |
| Uso de `double` em cálculos financeiros | Regra explícita no `CLAUDE.md` e revisão em toda PR |
| Complexidade desnecessária ("over-engineering") | Escopo definido por milestone com fronteiras claras |
| Documentação que não reflete o código | Regra: documentar apenas o implementado; verificar via `git status` |
| Decisões autônomas sem aprovação | Fluxo estruturado com aprovação explícita do plano antes de qualquer implementação |

---

## Conclusão Crítica

A IA foi uma ferramenta de **amplificação da capacidade do desenvolvedor**, não um substituto para julgamento técnico. Os maiores ganhos foram em:

1. Velocidade de geração de código boilerplate e infraestrutura
2. Coerência na aplicação de padrões (SOLID, Strategy, ACID) ao longo das 14 milestones
3. Geração de documentação técnica estruturada

Os maiores riscos realizados foram em versões de dependências incompatíveis, comportamentos específicos de frameworks e APIs que mudaram entre versões — áreas onde a IA trabalha com dados de treinamento que podem estar desatualizados.

**Toda decisão arquitetural, toda regra financeira e toda configuração de segurança foram revisadas e aprovadas pelo desenvolvedor antes de entrar no repositório.**
