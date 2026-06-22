# GitHub Actions — Pipeline CI do SRM Credit Engine

## Objetivo

Automatizar as validações de qualidade que até a v0.13.0 eram executadas apenas localmente (via `scripts/pre-push.sh` e comandos manuais), garantindo que todo código integrado na branch `main` passe por build, testes, cobertura, validação Docker e verificação de segurança — sem dependência do ambiente local do desenvolvedor.

---

## Quando Executa

O pipeline é disparado automaticamente em dois eventos:

| Evento | Quando |
|---|---|
| `pull_request` para `main` | Ao abrir, atualizar ou re-abrir um PR com destino à branch `main` |
| `push` para `main` | Ao fazer merge de um PR (ou push direto) na branch `main` |

Isso garante que:
- Um PR não pode ser mergeado se o pipeline falhar
- A `main` nunca tem um commit que não passou pelo pipeline

---

## Arquivo do Workflow

```
.github/workflows/ci.yml
```

Nome do workflow: **SRM Credit Engine CI**

---

## Jobs

O pipeline tem **4 jobs independentes** que rodam em paralelo:

```
┌─────────────┐  ┌──────────────┐  ┌────────────────┐  ┌──────────────────┐
│   backend   │  │   frontend   │  │     docker     │  │ security-checks  │
│ Build+Tests │  │ Build+Tests  │  │ Config+Build   │  │ File scan        │
└─────────────┘  └──────────────┘  └────────────────┘  └──────────────────┘
```

Cada job é independente: falha em um não cancela os outros. O feedback é isolado por responsabilidade.

---

## Job: backend

**Responsabilidade:** compilar o backend, rodar os testes unitários e verificar cobertura JaCoCo.

| Step | Ação |
|---|---|
| Checkout | `actions/checkout@v4` |
| Java 21 (Temurin) | `actions/setup-java@v4` com cache Maven |
| Permissão do mvnw | `chmod +x backend/mvnw` |
| Build + testes + cobertura | `cd backend && ./mvnw -B clean verify` |

**O que valida:**
- Compilação sem erros
- 106 testes unitários passando
- Cobertura JaCoCo ≥ 90% de linhas (threshold já configurado em `pom.xml`)

**Falha quando:**
- Qualquer erro de compilação
- Qualquer teste falha
- Cobertura abaixo de 90%

**Cache Maven:** dependências do Maven são cacheadas pelo `actions/setup-java@v4`. Na primeira execução, o cache é construído (~2–3 min). Execuções subsequentes são significativamente mais rápidas.

**Reproduzir localmente:**
```bash
cd backend
./mvnw clean verify
```

---

## Job: frontend

**Responsabilidade:** construir o bundle de produção do Angular e rodar os testes unitários Karma.

| Step | Ação |
|---|---|
| Checkout | `actions/checkout@v4` |
| Node 22 | `actions/setup-node@v4` com cache npm |
| Instalar dependências | `cd frontend && npm ci` |
| Build de produção | `cd frontend && npm run build` |
| Testes unitários | `cd frontend && npm test -- --watch=false --browsers=ChromeHeadless` |

**O que valida:**
- Build Angular de produção sem erros
- 9 specs (Karma + Jasmine) passando

**Falha quando:**
- `ng build` falha (erro de TypeScript, template inválido, dependência faltando)
- Qualquer spec falha

**ChromeHeadless:** o runner `ubuntu-latest` do GitHub Actions tem Google Chrome instalado. O Karma usa ChromeHeadless sem interface gráfica. Se o runner exigir `--no-sandbox`, a correção será adicionar um `frontend/karma.conf.js` com launcher `ChromeHeadlessCI` — documentado como melhoria posterior.

**Cache npm:** `package-lock.json` do frontend é usado como chave de cache. `npm ci` (não `npm install`) garante instalação determinística.

**Reproduzir localmente:**
```bash
cd frontend
npm ci
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```

---

## Job: docker

**Responsabilidade:** validar a sintaxe do `docker-compose.yml` e garantir que as imagens Docker compilam sem erro.

| Step | Ação |
|---|---|
| Checkout | `actions/checkout@v4` |
| Criar dummy secrets | Cria `backend/secrets/db_user` e `backend/secrets/db_password` com valores fictícios |
| Validar Compose | `docker compose config` |
| Build das imagens | `docker compose build` |

**O que valida:**
- Sintaxe do `docker-compose.yml` está correta
- Todos os `Dockerfile` compilam sem erro (`backend/Dockerfile` e `frontend/Dockerfile`)

**Falha quando:**
- `docker-compose.yml` tem erro de YAML ou referência inválida
- Qualquer `Dockerfile` tem instrução inválida ou layer quebrado

**Dummy secrets:** o `docker-compose.yml` usa Docker Secrets com arquivo (`file: ./backend/secrets/db_user`). Esses arquivos são gitignored e não existem no CI. Um step cria arquivos temporários com valores fictícios (`ci_user`, `ci_password`) apenas para que o `docker compose config` possa validar e o `docker compose build` possa compilar as imagens. Os arquivos não são commitados e não têm efeito em runtime (os secrets só são montados no container pelo Docker, não durante o build).

**Por que não `docker compose up`:** executar o stack completo em CI exige que o backend inicialize (healthcheck com 30s de timeout), o banco suba, as migrations rodem — tornando o job lento e propenso a flakiness. Para v0.14.0, `docker compose build` é suficiente para garantir integridade dos Dockerfiles.

**Reproduzir localmente:**
```bash
# Criar os arquivos de secret (se não existirem)
echo "srm" > backend/secrets/db_user
echo "srm_password" > backend/secrets/db_password

docker compose config
docker compose build
```

---

## Job: security-checks

**Responsabilidade:** detectar arquivos sensíveis que foram acidentalmente rastreados pelo Git.

| Step | Ação |
|---|---|
| Checkout | `actions/checkout@v4` |
| Verificar secrets | `git ls-files` com grep para padrões sensíveis |

**Script:**
```bash
if git ls-files | grep -E '(^|/)(\.env|db_user|db_password)$'; then
  echo "ERROR: Sensitive file(s) found tracked by git."
  exit 1
fi
echo "Security check passed — no sensitive files tracked by git."
```

**O que valida:** nenhum `.env`, `db_user` ou `db_password` está listado como arquivo rastreado pelo Git.

**Por que `git ls-files` e não `find`:** `git ls-files` mostra apenas os arquivos efetivamente rastreados pelo repositório — não detecta falsos positivos de arquivos locais criados no runner (como os dummy secrets do job Docker).

**Falha quando:** algum dos arquivos sensíveis foi adicionado ao index Git acidentalmente.

**Reproduzir localmente:**
```bash
git ls-files | grep -E '(^|/)(\.env|db_user|db_password)$' && echo "ALERTA" || echo "LIMPO"
```

---

## Como Interpretar Falhas

| Job | Tipo de falha | O que verificar |
|---|---|---|
| `backend` | Build error | Erros de compilação Java — checar `pom.xml`, dependências, imports |
| `backend` | Test failure | Log do teste específico — revisar a classe de teste e o código alterado |
| `backend` | JaCoCo coverage | Cobertura abaixo de 90% — adicionar testes para o código novo |
| `frontend` | Build error | Erro TypeScript ou template Angular — checar o log do `ng build` |
| `frontend` | Test failure | Spec falhando — checar o log do Karma para o spec específico |
| `docker` | Compose config | YAML inválido no `docker-compose.yml` — checar sintaxe |
| `docker` | Build failure | Dockerfile com instrução inválida ou layer quebrado |
| `security-checks` | Arquivo sensível | Remover o arquivo do tracking: `git rm --cached <arquivo>` e atualizar `.gitignore` |

---

## O Que Fica Fora do Pipeline

| Item | Motivo |
|---|---|
| Deploy em ambiente real | Fora do escopo do desafio técnico |
| Push de imagens Docker para registry | Sem registry configurado |
| Testcontainers / banco em CI | Aumentaria complexidade e tempo; backend usa mocks |
| Testes E2E (Cypress, Playwright) | Não implementados no projeto |
| Análise estática (SonarQube, CodeQL) | Evolução futura |
| Notificações (Slack, e-mail) | Evolução futura |
| Release automática e tag | Tag é criada manualmente após merge e validação |

---

## Próximos Passos

1. **Cache de layers Docker:** usar `docker/build-push-action` com cache para reduzir o tempo do job `docker`
2. **ChromeHeadlessCI:** se o job `frontend` falhar por sandbox, criar `frontend/karma.conf.js` com launcher `ChromeHeadlessCI` configurado com `--no-sandbox`
3. **Testcontainers em CI:** adicionar PostgreSQL como service do GitHub Actions para testes de integração do backend
4. **Análise estática:** integrar SonarCloud ou CodeQL para análise de vulnerabilidades
5. **Deploy automático:** configurar job de deploy para ambiente de staging após merge na `main`
