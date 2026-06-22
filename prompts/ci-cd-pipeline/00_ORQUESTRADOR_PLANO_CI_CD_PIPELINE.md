# Prompt — Orquestrador — Planejamento — v0.14.0 CI/CD Pipeline

Use o arquivo `agents/agent_orquestrador.md` como persona principal.

Branch:

```text
feature/ci-cd-pipeline
```

Milestone:

```text
v0.14.0-ci-cd-pipeline
```

## Idioma obrigatório

Toda documentação criada ou atualizada deve ser escrita em **português do Brasil**.

Manter termos técnicos consolidados em inglês quando fizer sentido: GitHub Actions, CI/CD, workflow, job, runner, build, test, cache, artifact, pull request, push, Docker Compose, backend, frontend.

## Contexto

O projeto já possui:

- backend Spring Boot com Maven Wrapper;
- Java 21;
- JaCoCo mínimo 90%;
- script `scripts/pre-push.sh`;
- frontend Angular;
- Docker Compose full stack;
- Prometheus;
- documentação completa;
- estratégia Git e tags SemVer;
- simulação de crise com Git.

Agora precisamos automatizar validações que hoje são executadas manualmente/localmente.

## Objetivo

Planejar a criação de um pipeline GitHub Actions que execute:

1. validação backend;
2. validação frontend;
3. validação Docker Compose;
4. checagem básica de arquivos sensíveis;
5. documentação do pipeline;
6. atualização do README;
7. atualização do AI_USAGE;
8. atualização do checklist final.

## Subagents disponíveis

Consulte e simule análise dos subagents:

```text
agents/agent_analista_requisitos.md
agents/agent_arquiteto_sistemas.md
agents/agent_backend_especialista.md
agents/agent_frontend_especialista.md
agents/agent_devops_especialista.md
agents/agent_qa_qualidade.md
```

## Regras obrigatórias

- Não alterar código de produção.
- Não alterar regras de negócio.
- Não alterar migrations Flyway.
- Não alterar frontend funcional, exceto se for estritamente necessário para CI.
- Não alterar backend funcional, exceto se for estritamente necessário para CI.
- Não adicionar secrets reais.
- Não implementar deploy real.
- Não publicar imagens Docker.
- Não criar Terraform/IaC.
- Não depender de serviços externos pagos.
- Não exigir credenciais no GitHub.
- Não fazer push.
- Não criar tag.

## Arquivos candidatos

Criar ou atualizar:

```text
.github/workflows/ci.yml
docs/ci-cd/github-actions.md
docs/adr/0011-ci-cd-pipeline.md
README.md
AI_USAGE.md
docs/validation/final-checklist.md
```

Antes de criar, verificar se já existe workflow ou documentação equivalente.

## Pipeline esperado

### Triggers

Executar em:

```yaml
on:
  pull_request:
    branches: [ "main" ]
  push:
    branches: [ "main" ]
```

### Job backend

Executar:

```bash
cd backend
chmod +x mvnw
./mvnw -B clean verify
```

Validar:

- compilação;
- testes;
- cobertura JaCoCo;
- qualidade mínima já configurada no projeto.

### Job frontend

Executar:

```bash
cd frontend
npm ci
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```

Usar Node compatível com o projeto.

### Job docker

Executar na raiz:

```bash
docker compose config
docker compose build
```

Não executar `docker compose up` nesta etapa, a menos que seja simples e sem flakiness.

### Job security/lightweight checks

Executar validação simples para evitar versionamento acidental de secrets:

```bash
git ls-files | grep -E '(^|/)(\\.env|db_user|db_password)$' && exit 1 || true
```

Melhorar o comando para não falhar de forma incorreta. O objetivo é detectar arquivos sensíveis versionados.

## Documentação esperada

### docs/ci-cd/github-actions.md

Documentar:

1. objetivo do pipeline;
2. triggers;
3. jobs;
4. comandos executados;
5. validações cobertas;
6. o que o pipeline bloqueia;
7. o que fica fora do pipeline;
8. como interpretar falhas;
9. como reproduzir localmente;
10. próximos passos.

### ADR 0011

Criar:

```text
docs/adr/0011-ci-cd-pipeline.md
```

Registrar:

- status;
- contexto;
- decisão;
- por que GitHub Actions;
- por que separar jobs;
- por que não implementar deploy real agora;
- consequências;
- alternativas consideradas.

## README / AI_USAGE / Checklist

Atualizar:

- README com seção breve de CI/CD e link para docs.
- AI_USAGE com milestone v0.14.0.
- docs/validation/final-checklist.md com critérios do pipeline.

## Resposta esperada

Responder com:

1. resumo da etapa;
2. análise por subagent;
3. decisão consolidada;
4. escopo incluído;
5. escopo fora;
6. arquivos a criar/alterar;
7. plano do workflow;
8. plano da documentação;
9. riscos;
10. critérios de aceite;
11. checklist de validação;
12. commits sugeridos;
13. pontos de aprovação humana.

Nesta primeira resposta não implemente arquivos. Gere apenas plano técnico e aguarde aprovação explícita.

Finalize exatamente com:

```text
Aguardando aprovação para iniciar a implementação da etapa v0.14.0-ci-cd-pipeline.
```
