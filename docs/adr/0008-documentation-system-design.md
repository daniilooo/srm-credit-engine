# ADR 0008 — Estratégia de Documentação e System Design

## Status

Aceito

## Contexto

Após a conclusão das etapas v0.2.0 a v0.10.0, o projeto possui backend, frontend, dockerização completa e observabilidade implementados. A documentação existente — `README.md` e `AI_USAGE.md` — foi criada no início do projeto e reflete intenções, não o que foi realmente entregue. Os ADRs (0001–0007) documentam decisões pontuais mas não existia documentação de alto nível, diagramas visuais ou guias operacionais.

O desafio exige avaliação em nível Sênior/Staff, o que requer que a documentação permita a qualquer leitor:

1. Entender o propósito e funcionamento do sistema sem ler o código
2. Reproduzir o ambiente localmente
3. Compreender as decisões arquiteturais e seus trade-offs
4. Avaliar o uso consciente de IA no desenvolvimento

## Decisão

Criar documentação estruturada e completa em `v0.11.0-documentation-system-design`, cobrindo:

- `README.md` — reescrito como porta de entrada completa do projeto
- `AI_USAGE.md` — reescrito com análise crítica real do uso de IA
- `docs/architecture/overview.md` — justificativa arquitetural, camadas e fluxo de liquidação
- `docs/c4/context.md` e `docs/c4/container.md` — diagramas C4 com Mermaid
- `docs/er/er-diagram.md` — schema real do banco com Mermaid erDiagram
- `docs/api/endpoints.md` — documentação estática dos 5 endpoints REST
- `docs/docker/running-with-docker.md` — guia completo de setup com Docker Compose e secrets
- `docs/observability/backend-observability.md` — métricas Micrometer e Prometheus
- `docs/git/branching-strategy.md` — modelo de branches, commits e tags SemVer
- `docs/validation/final-checklist.md` — checklist verificável de critérios de aceite

### Decisões de conteúdo

**Documentar apenas o que foi implementado.** Funcionalidades planejadas mas não entregues (Resilience4j, Testcontainers, Grafana, GitHub Actions CI/CD, dispatcher do Outbox) são listadas como limitações conhecidas e próximos passos — não como implementadas.

**Mermaid para todos os diagramas.** Renderiza em qualquer plataforma que suporte Markdown (GitHub, GitLab, VS Code). Sem ferramentas externas.

**Português do Brasil para toda documentação.** Termos técnicos consolidados permanecem em inglês (Docker Compose, Spring Boot, Angular, Prometheus, Strategy Pattern, Outbox Pattern, etc.).

**Nenhum segredo real.** Exemplos de `db_user` e `db_password` usam valores fictícios. Arquivos `.env` e secrets não são versionados.

## Alternativas consideradas

| Alternativa | Motivo de não adoção |
|---|---|
| Apenas atualizar README.md | Insuficiente para avaliação Sênior/Staff — falta visão estruturada, diagramas e guias |
| Usar PlantUML | Requer tooling externo; Mermaid renderiza nativamente no GitHub/GitLab |
| Documentar em inglês | Decisão do projeto — documentação em pt-BR com termos técnicos em inglês |
| Criar wiki separada | Mantém a documentação desacoplada do código; preferível documentação no repositório |

## Consequências

- O repositório passa a ter documentação completa e auditável
- Qualquer avaliador pode reproduzir o ambiente com `docker compose up --build`
- As decisões arquiteturais e trade-offs são rastreáveis
- O uso de IA é documentado com exemplos reais e análise crítica
- Limitações reais são expostas honestamente, demonstrando maturidade de engenharia
