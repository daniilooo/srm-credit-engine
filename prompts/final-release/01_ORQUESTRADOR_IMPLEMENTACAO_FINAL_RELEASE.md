# Prompt — Orquestrador — Implementação — v1.0.0 Final Release

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/final-release-polish
```

Milestone:

```text
v1.0.0-final-release
```

## Status

O plano técnico foi aprovado. Agora execute a revisão final e implemente apenas ajustes necessários.

## Idioma obrigatório

Toda documentação criada ou atualizada deve ser escrita em **português do Brasil**.

## Regras obrigatórias

- Não criar feature funcional.
- Não alterar regras de negócio.
- Não alterar schema/migrations.
- Não incluir secrets reais.
- Não fazer deploy real.
- Não criar tag.
- Não fazer push.
- Não mascarar limitações conhecidas.
- Documentar apenas o que está implementado.
- Se encontrar problema funcional relevante, reportar antes de alterar.
- Correções permitidas:
  - documentação;
  - links;
  - typos;
  - checklist;
  - release notes;
  - consistência de milestones;
  - limpeza de arquivos untracked indevidos, se aprovado.

## Sequência de implementação

1. Inspecionar status:
   ```bash
   git status --short
   git branch --show-current
   ```
2. Verificar arquivos soltos:
   ```bash
   git status --short
   find prompts -maxdepth 2 -type d | sort
   ```
3. Verificar secrets:
   ```bash
   git status --short | grep -E "\\.env$|db_user$|db_password$|target|dist|node_modules"
   git ls-files | grep -E '(^|/)(\\.env|db_user|db_password)$'
   ```
4. Revisar README.md.
5. Revisar AI_USAGE.md.
6. Revisar docs/validation/final-checklist.md.
7. Criar `docs/release/v1.0.0-final-release-notes.md`.
8. Criar `docs/adr/0012-final-release.md`, se fizer sentido.
9. Atualizar README.md com link para release notes, se fizer sentido.
10. Atualizar AI_USAGE.md com v1.0.0, se fizer sentido.
11. Atualizar checklist final com status final.
12. Validar links principais.
13. Rodar validações locais possíveis.

## Validações recomendadas

### Backend

```bash
cd backend
./mvnw -B clean verify
```

### Frontend

```bash
cd frontend
npm ci
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```

### Docker Compose

Na raiz:

```bash
docker compose config
docker compose build
docker compose up -d
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus | head
curl -I http://localhost:4200
curl -I http://localhost:8080/swagger-ui/index.html
curl -I http://localhost:9090
docker compose down
```

Se algum teste/validação for demorado ou inviável, documentar o motivo e o comando equivalente.

## Release notes

Criar:

```text
docs/release/v1.0.0-final-release-notes.md
```

Conteúdo obrigatório:

1. visão geral da release;
2. funcionalidades entregues;
3. stack;
4. arquitetura;
5. backend;
6. frontend;
7. Docker;
8. observabilidade;
9. CI/CD;
10. documentação;
11. AI_USAGE;
12. limitações conhecidas;
13. próximos passos;
14. checklist de validação;
15. tags/milestones.

## ADR 0012 opcional

Criar se fizer sentido:

```text
docs/adr/0012-final-release.md
```

Registrar:

- status: Aceito;
- contexto;
- decisão;
- por que congelar escopo funcional em v1.0.0;
- por que priorizar validação/documentação;
- consequências;
- próximos passos fora da release.

## Resposta final esperada

Responder com:

1. arquivos criados/alterados;
2. release notes criadas;
3. ADR criada, se houver;
4. README/AI_USAGE/checklist atualizados;
5. validações executadas;
6. problemas encontrados;
7. correções aplicadas;
8. confirmação de que não houve nova feature;
9. confirmação de que não houve alteração de regra de negócio;
10. confirmação de que nenhum segredo real foi incluído;
11. status final do projeto;
12. commits sugeridos.

Não faça push. Não crie tag.
