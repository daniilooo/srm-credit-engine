# ADR 0011 — Pipeline de CI/CD com GitHub Actions

**Status:** Aceito

**Data:** 2026-06-22

**Autores:** Equipe SRM Credit Engine

---

## Contexto

O SRM Credit Engine chegou à v0.13.0 com todas as validações de qualidade executadas localmente:

- `./mvnw clean verify` — build + testes + cobertura JaCoCo
- `scripts/pre-push.sh` — hook local que bloqueia push com build quebrado
- `npm run build` — build de produção do Angular
- `docker compose config` — validação da configuração Docker

O pre-push hook é eficaz para o desenvolvedor local, mas tem limitações:
1. Pode ser bypassado com `--no-verify`
2. Não valida o ambiente de outros contribuidores
3. Não gera relatório centralizado e visível no GitHub
4. Não bloqueia merge de PRs automaticamente

Com o projeto em maturidade (13 milestones, 106 testes, cobertura JaCoCo ≥ 90%), é necessário um mecanismo de CI remoto que garanta a qualidade de forma objetiva e independente do ambiente local.

---

## Decisão

**Implementar pipeline de CI com GitHub Actions, composto por 4 jobs independentes:**

| Job | Responsabilidade |
|---|---|
| `backend` | Compilação Java 21 + 106 testes + JaCoCo ≥ 90% |
| `frontend` | Build Angular de produção + 9 specs Karma |
| `docker` | Validação do `docker-compose.yml` + build das imagens |
| `security-checks` | Detecção de arquivos sensíveis rastreados por Git |

**Triggers:** `pull_request` e `push` para `main`.

---

## Por Que GitHub Actions

| Critério | GitHub Actions | Alternativa |
|---|---|---|
| **Integração com GitHub** | Nativa — PRs, status checks, logs diretamente no repositório | Jenkins exige servidor próprio |
| **Custo** | Gratuito para repositórios públicos (2.000 min/mês para privados) | CircleCI/Travis têm limites menores no plano gratuito |
| **Runners disponíveis** | `ubuntu-latest` com Java, Node, Docker, Chrome pré-instalados | Outros CI exigem configuração adicional |
| **Sintaxe YAML** | Familiar, bem documentada, ampla comunidade | GitLab CI tem sintaxe similar mas exige migração |
| **Manutenção** | Zero — sem servidor próprio para gerenciar | Jenkins/TeamCity exigem infraestrutura própria |
| **Atualizações de actions** | `@v4` das actions oficiais — atualizadas pelo marketplace | Plugins Jenkins exigem atualização manual |

---

## Por Que 4 Jobs Separados

**Separação por responsabilidade:**

- Falha no backend não deve bloquear o feedback do frontend e vice-versa
- Jobs rodam em paralelo — tempo total ≈ max(tempo de cada job) em vez de soma
- Logs isolados por responsabilidade — desenvolvedor sabe exatamente o que quebrou
- Cada job tem contexto mínimo — sem dependências desnecessárias entre jobs

**Alternativa rejeitada: um único job sequencial**
- Tempo total maior (jobs sequenciais somam os tempos)
- Falha em qualquer step cancela os demais — sem visibilidade de outros problemas
- Mistura responsabilidades distintas em um log único difícil de interpretar

---

## Por Que Não Implementar Deploy Real Nesta Etapa

1. **Sem ambiente de produção definido** no escopo do desafio técnico
2. **Sem registry Docker configurado** — publicar imagens exigiria secrets de credenciais
3. **Complexity vs. value:** deploy automático agrega valor apenas quando existe um ambiente alvo. Implementá-lo sem destino concreto seria over-engineering
4. **Próximo passo natural:** quando um ambiente de staging for definido, o job `docker` já está pronto para ser estendido com build + push + deploy

---

## Decisão Sobre Dummy Secrets no Job Docker

O `docker-compose.yml` usa Docker Secrets com arquivo (`file:` driver), referenciando `./backend/secrets/db_user` e `./backend/secrets/db_password`. Esses arquivos são gitignored e não existem no runner de CI.

**Decisão:** criar arquivos com valores fictícios (`ci_user`, `ci_password`) em um step antes do `docker compose config`. Esses arquivos:
- Não são commitados (o `.gitignore` os exclui)
- Não têm efeito funcional (secrets só são montados em runtime pelo Docker)
- São necessários apenas para que o `docker compose config` valide a referência de arquivo

**Alternativa considerada:** remover a referência de secrets do `docker-compose.yml` para CI — rejeitada porque alteraria o arquivo de produção.

---

## Decisão Sobre ChromeHeadless no Job Frontend

O frontend usa `@angular/build:karma` com ChromeHeadless. Não existe `karma.conf.js` no projeto — a configuração é inline via `angular.json`.

**Decisão:** usar `--browsers=ChromeHeadless` diretamente, sem criar `karma.conf.js` preventivamente. O runner `ubuntu-latest` do GitHub Actions tem Chrome instalado e o ChromeHeadless funciona sem `--no-sandbox` na maioria dos runners modernos.

**Contingência documentada:** se o job `frontend` falhar com erro de sandbox, criar `frontend/karma.conf.js` com launcher `ChromeHeadlessCI` configurado com `--no-sandbox --disable-setuid-sandbox`. Isso seria uma alteração mínima do frontend estritamente necessária para CI.

---

## Consequências

### Positivas

- Nenhum PR pode ser mergeado na `main` sem passar pelo pipeline
- Visibilidade centralizada do status de CI em cada PR e commit
- Feedback rápido e isolado por responsabilidade (4 jobs independentes)
- Cache de Maven e npm reduz tempo de execução em execuções subsequentes
- Detecção automática de secrets versionados por acidente

### Limitações / Trade-offs

- Sem Testcontainers: testes do backend usam mocks — banco real não é testado em CI
- `docker compose build` pode levar 3–8 minutos na primeira execução (sem cache de layers)
- ChromeHeadless pode exigir `--no-sandbox` dependendo da versão do runner — monitorar nas primeiras execuções
- Sem deploy automático: pipeline valida mas não entrega

---

## Alternativas Consideradas

### Jenkins

**Rejeitado.** Exige servidor próprio, manutenção de plugins, configuração de agent. Para o escopo deste projeto, adiciona complexidade operacional sem benefício proporcional.

### CircleCI

**Rejeitado.** Requer conta e configuração adicional. O plano gratuito tem limites menores que o GitHub Actions. Integração com GitHub é menos nativa.

### GitLab CI

**Rejeitado.** Exigiria migrar o repositório para GitLab. O projeto usa GitHub Flow e está hospedado no GitHub.

### Job único sequencial

**Rejeitado.** Tempo total maior, feedback menos preciso, mistura responsabilidades. 4 jobs paralelos é claramente superior.

### Cache de layers Docker

**Postergado.** O `docker compose build` recompila as imagens a cada execução. Cache de layers via `docker/build-push-action` é uma melhoria válida, mas adiciona complexidade ao YAML. Prioridade baixa para v0.14.0.
