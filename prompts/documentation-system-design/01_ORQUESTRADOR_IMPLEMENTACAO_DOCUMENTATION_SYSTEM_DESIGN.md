# Prompt — Orquestrador — Implementação — v0.11.0 Documentation & System Design

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/documentation-system-design
```

Milestone:

```text
v0.11.0-documentation-system-design
```

## Idioma obrigatório

Toda documentação deve ser escrita em **português do Brasil**.

## Decisões obrigatórias

- Não alterar código de produção.
- Não alterar backend.
- Não alterar frontend.
- Não alterar Docker Compose.
- Não alterar migrations Flyway.
- Não alterar testes.
- Foco exclusivo em documentação.
- Não incluir secrets reais.
- Não versionar `.env`, `db_user` ou `db_password`.

## Sequência de implementação

1. Inspecionar documentação atual:
   - `README.md`;
   - `AI_USAGE.md`, se existir;
   - `docs/`;
   - `docs/adr/`;
   - `docs/c4/`;
   - `docs/er/`;
   - `docs/observability/`.
2. Identificar lacunas.
3. Atualizar `README.md`.
4. Criar/atualizar `AI_USAGE.md`.
5. Criar/atualizar `docs/architecture/overview.md`.
6. Criar/atualizar `docs/c4/context.md`.
7. Criar/atualizar `docs/c4/container.md`.
8. Criar/atualizar `docs/er/er-diagram.md`.
9. Criar/atualizar `docs/docker/running-with-docker.md`.
10. Criar/atualizar `docs/observability/backend-observability.md`.
11. Criar/atualizar `docs/api/endpoints.md`.
12. Criar/atualizar `docs/git/branching-strategy.md`.
13. Criar/atualizar `docs/validation/final-checklist.md`.
14. Criar ADR, se necessário.
15. Revisar links relativos.
16. Garantir que comandos são executáveis.
17. Garantir que nenhum segredo real foi incluído.

## Conteúdo obrigatório do README.md

Incluir:

1. visão geral;
2. contexto de negócio SRM/FIDC;
3. funcionalidades;
4. stack;
5. arquitetura;
6. justificativa da arquitetura;
7. trade-offs;
8. estrutura do projeto;
9. como rodar com Docker Compose;
10. como criar `.env` e secrets;
11. URLs úteis;
12. como rodar backend local;
13. como rodar frontend local;
14. como rodar testes;
15. endpoints principais;
16. fluxo de liquidação;
17. observabilidade;
18. segurança e secrets;
19. estratégia Git;
20. tags/milestones;
21. decisões técnicas;
22. limitações conhecidas;
23. próximos passos.

## Conteúdo obrigatório do AI_USAGE.md

Incluir:

1. objetivo;
2. ferramentas/modelos:
   - GitHub Copilot;
   - OpenAI Codex;
   - Claude;
   - GPT.
3. como a IA foi usada;
4. modelo de desenvolvimento com subagents;
5. papel do agent orquestrador;
6. fluxo por etapa:
   - milestone;
   - prompt específico;
   - Claude usando orquestrador;
   - plano técnico;
   - aprovação humana;
   - implementação;
   - relatório de implementação;
   - revisão humana;
   - commits;
   - PR;
   - tag após merge.
7. exemplos de prompts estratégicos;
8. decisões humanas;
9. erros/alucinações corrigidos;
10. validação manual;
11. valor agregado pela IA;
12. pontos que exigiram revisão humana;
13. riscos do uso de IA;
14. conclusão crítica.

Registrar explicitamente estes casos:

- Spring Boot 4 mudou pacotes de testes como `@WebMvcTest`.
- `springdoc-openapi` precisou versão compatível com Spring Boot 4.
- `presentValue` não estava persistido e não deveria ser recalculado no relatório.
- Métrica `settlements.created.total` foi problemática no Prometheus e foi renomeada/ajustada.
- Docker secrets foram preferidos a credenciais no `.env`.

## Justificativa da arquitetura

Documentar o **modular monolith em camadas**:

```text
interfaces/rest
application
domain
infrastructure
reporting
```

Explicar por que não microserviços:

- escopo do desafio não exigia;
- microserviços aumentariam complexidade operacional;
- domínio exige transação ACID;
- liquidação não pode ficar parcial;
- modular monolith preserva consistência e simplicidade;
- separação em camadas permite evolução futura.

## Trade-off obrigatório

Incluir:

```text
A escolha por modular monolith prioriza consistência, simplicidade, rastreabilidade e menor custo operacional para o escopo do desafio. Para cenários de escala extrema, o projeto poderia evoluir para uma arquitetura orientada a eventos, com filas, workers assíncronos, CQRS, read replicas e particionamento.
```

## Pontos obrigatórios

Destacar:

- `BigDecimal`;
- Strategy Pattern;
- transação ACID;
- optimistic locking;
- proteção contra dupla liquidação;
- snapshot cambial;
- `NamedParameterJdbcTemplate`;
- Docker Compose full stack;
- secrets por arquivo;
- Prometheus;
- métricas técnicas e de negócio;
- Angular + REST API;
- AI_USAGE com análise crítica.

## Diagramas

Usar Mermaid.

Criar/atualizar:

```text
docs/c4/context.md
docs/c4/container.md
docs/er/er-diagram.md
```

## Validações

```bash
git status --short
git diff --name-only
docker compose config
git status --short | grep -E "\\.env$|db_user$|db_password$|target|dist|node_modules"
```

O último comando não deve retornar nada.

## Resposta final esperada

Responder com:

1. arquivos criados/alterados;
2. README atualizado;
3. AI_USAGE criado/atualizado;
4. como o modelo com IA foi documentado;
5. como subagents e orquestrador foram documentados;
6. como o fluxo por etapa foi documentado;
7. diagramas criados/atualizados;
8. docs complementares;
9. ADR criada, se houver;
10. justificativa arquitetural;
11. trade-offs;
12. limitações;
13. comandos de validação;
14. confirmação de que código de produção não foi alterado;
15. confirmação de que nenhum segredo real foi incluído;
16. commits sugeridos.

Não faça push. Não crie tag.
