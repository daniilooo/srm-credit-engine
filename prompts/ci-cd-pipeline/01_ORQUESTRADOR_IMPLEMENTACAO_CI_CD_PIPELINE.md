# Prompt — Orquestrador — Implementação — v0.14.0 CI/CD Pipeline

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/ci-cd-pipeline
```

Milestone:

```text
v0.14.0-ci-cd-pipeline
```

## Status

O plano técnico foi aprovado. Agora implemente o pipeline e documentação associada.

## Idioma obrigatório

Toda documentação deve ser escrita em **português do Brasil**.

## Regras obrigatórias

- Não alterar regras de negócio.
- Não alterar migrations Flyway.
- Não adicionar secrets reais.
- Não implementar deploy real.
- Não publicar imagens Docker.
- Não criar Terraform/IaC.
- Não exigir credenciais no GitHub.
- Não criar tag.
- Não fazer push.
- Não adicionar dependências desnecessárias.
- Priorizar pipeline simples, claro e robusto.

## Sequência de implementação

1. Inspecionar:
   - `.github/workflows/`, se existir;
   - `backend/pom.xml`;
   - `backend/mvnw`;
   - `frontend/package.json`;
   - `docker-compose.yml`;
   - `scripts/pre-push.sh`;
   - README.md;
   - AI_USAGE.md;
   - docs/validation/final-checklist.md.
2. Criar diretório `.github/workflows/`, se necessário.
3. Criar `.github/workflows/ci.yml`.
4. Criar `docs/ci-cd/github-actions.md`.
5. Criar `docs/adr/0011-ci-cd-pipeline.md`.
6. Atualizar README.md com seção/link de CI/CD.
7. Atualizar AI_USAGE.md com milestone v0.14.0.
8. Atualizar docs/validation/final-checklist.md.
9. Validar YAML.
10. Rodar validações locais possíveis.

## Workflow esperado

Criar workflow com nome:

```text
SRM Credit Engine CI
```

Triggers:

```yaml
on:
  pull_request:
    branches: [ "main" ]
  push:
    branches: [ "main" ]
```

### Job backend

Requisitos:

- runner Linux;
- Java 21;
- cache Maven;
- executar Maven Wrapper;
- comando:

```bash
cd backend
chmod +x mvnw
./mvnw -B clean verify
```

### Job frontend

Requisitos:

- runner Linux;
- Node compatível com o projeto;
- cache npm;
- instalar dependências com `npm ci`;
- build;
- testes headless:

```bash
cd frontend
npm ci
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```

Se o projeto exigir ajustes de Chrome no runner, documentar a decisão. Não alterar frontend sem necessidade.

### Job docker

Requisitos:

- rodar na raiz;
- validar Compose;
- buildar imagens:

```bash
docker compose config
docker compose build
```

### Job security-checks

Validar que arquivos sensíveis não foram versionados:

- `.env`;
- `backend/secrets/db_user`;
- `backend/secrets/db_password`.

O job deve falhar se algum arquivo sensível estiver rastreado pelo Git.

Sugestão segura:

```bash
if git ls-files | grep -E '(^|/)(\\.env|db_user|db_password)$'; then
  echo "Sensitive file tracked by git"
  exit 1
fi
```

## docs/ci-cd/github-actions.md

Documentar:

1. objetivo;
2. quando executa;
3. jobs;
4. comandos por job;
5. como reproduzir localmente;
6. como interpretar falhas;
7. o que fica fora;
8. próximos passos.

## ADR 0011

Criar `docs/adr/0011-ci-cd-pipeline.md` com:

- status: Aceito;
- contexto;
- decisão;
- GitHub Actions como CI;
- jobs separados;
- sem deploy real nesta etapa;
- consequências positivas;
- limitações;
- alternativas consideradas.

## Validações locais

Rodar:

```bash
git status --short
git diff --name-only
docker compose config
git status --short | grep -E "\\.env$|db_user$|db_password$|target|dist|node_modules"
```

Se possível, rodar também:

```bash
cd backend && ./mvnw clean verify
```

```bash
cd frontend && npm ci && npm run build && npm test -- --watch=false --browsers=ChromeHeadless
```

Não rodar comandos extremamente demorados se já houver validação recente, mas documentar o que foi executado.

## Resposta final esperada

Responder com:

1. arquivos criados/alterados;
2. workflow criado;
3. jobs do workflow;
4. documentação CI/CD criada;
5. ADR criada;
6. README/AI_USAGE atualizados;
7. checklist atualizado;
8. comandos de validação executados;
9. confirmação de que não há deploy real;
10. confirmação de que nenhum segredo real foi incluído;
11. confirmação de que regras de negócio não foram alteradas;
12. commits sugeridos.

Não faça push. Não crie tag.
