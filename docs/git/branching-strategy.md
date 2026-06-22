# Estratégia Git — SRM Credit Engine

## Modelo Adotado: GitHub Flow Adaptado

O projeto usa um modelo simplificado inspirado no **GitHub Flow**:

- `main` é sempre a branch estável e pronta para avaliação
- Cada milestone é desenvolvida em uma branch `feature/<milestone>`
- Toda feature é integrada via Pull Request (simulado)
- Tags SemVer são criadas **somente após merge na `main`**

Este modelo é adequado para projetos individuais com ciclos de entrega bem definidos e sem múltiplos times trabalhando em paralelo.

---

## Estrutura de Branches

```
main
├── feature/domain-model          → v0.2.0
├── feature/pricing-engine        → v0.3.0
├── feature/currency-engine       → v0.4.0
├── feature/settlement-flow       → v0.5.0
├── feature/rest-api              → v0.6.0
├── feature/reporting-api         → v0.7.0
├── feature/frontend-operator-panel → v0.8.0
├── feature/full-docker-compose   → v0.9.0
├── feature/backend-observability → v0.10.0
└── feature/documentation-system-design → v0.11.0
```

---

## Conventional Commits

Todos os commits seguem a especificação [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>(<escopo>): <descrição curta>
```

### Tipos utilizados no projeto

| Tipo | Quando usar |
|---|---|
| `feat` | Nova funcionalidade de negócio |
| `test` | Adicionar ou corrigir testes |
| `docs` | Documentação apenas |
| `build` | Configuração de build (Maven, JaCoCo, pom.xml) |
| `infra` | Docker, Docker Compose, Dockerfile, scripts |
| `fix` | Correção de bug |
| `refactor` | Refatoração sem mudança de comportamento |

### Exemplos reais do projeto

```bash
feat(domain): add financial domain model with JPA entities
feat(pricing): implement Strategy Pattern for receivable pricing
feat(currency): implement exchange rate engine
feat(settlement): implement ACID settlement flow
feat(api): expose REST endpoints for all use cases
feat(reporting): implement settlement report with native SQL
feat(frontend): add Angular 20 operator panel
infra(docker): add full Docker Compose with Nginx and secrets
feat(observability): add Micrometer counters and timers
docs(readme): rewrite README with complete project documentation
```

---

## Ciclo de Trabalho por Milestone

```
1. Criar branch: git checkout -b feature/<milestone>

2. Implementar em commits pequenos e semânticos

3. Verificar qualidade antes de qualquer push:
   ./scripts/pre-push.sh

4. Abrir Pull Request para main (simulado no desafio)

5. Revisar e aprovar o PR

6. Merge na main (squash ou merge commit)

7. Criar tag SemVer após merge:
   git checkout main
   git pull origin main
   git tag -a v<versão> -m "<descrição>"
   git push origin v<versão>
```

---

## Tags SemVer

| Tag | Milestone | Descrição |
|---|---|---|
| `v0.2.0-domain-model` | v0.2.0 | Schema financeiro e entidades JPA |
| `v0.3.0-pricing-engine` | v0.3.0 | Motor de precificação com Strategy Pattern |
| `v0.4.0-currency-engine` | v0.4.0 | Motor de câmbio com pares direcionais |
| `v0.5.0-settlement-flow` | v0.5.0 | Fluxo de liquidação transacional ACID |
| `v0.6.0-rest-api` | v0.6.0 | API REST com OpenAPI/Swagger |
| `v0.7.0-reporting-api` | v0.7.0 | Extrato analítico com SQL nativo |
| `v0.8.0-frontend-operator-panel` | v0.8.0 | Painel Angular 20 standalone |
| `v0.9.0-full-docker-compose` | v0.9.0 | Dockerização completa com secrets |
| `v0.10.0-backend-observability` | v0.10.0 | Micrometer + Prometheus |
| `v0.11.0-documentation-system-design` | v0.11.0 | Documentação e system design |

---

## Pre-Push Hook Local

O hook local em `scripts/pre-push.sh` bloqueia o push caso build, testes ou cobertura falhem.

### Instalação (uma vez por desenvolvedor)

```bash
chmod +x scripts/pre-push.sh
cp scripts/pre-push.sh .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

O hook executa `./mvnw clean verify` no backend e bloqueia o push se:
- O build falhar
- Algum teste falhar
- A cobertura JaCoCo for menor que 90%

### Observação

`.git/hooks/` não é versionado. O script `scripts/pre-push.sh` **é versionado** e deve ser instalado manualmente em cada desenvolvedor ou máquina nova.

---

## Estratégia de Crise: git revert

Para desfazer um commit já integrado na `main` sem reescrever a história:

```bash
# Identificar o commit problemático
git log --oneline main

# Reverter de forma segura (cria um novo commit de revert)
git revert <hash-do-commit>

# Abrir novo PR com o revert
git push origin feature/revert-<descricao>
```

`git revert` é preferível a `git reset --hard` em branches compartilhadas porque preserva a história e é rastreável.

---

## Regras de Governança

- Nenhum commit direto na `main`
- Nunca usar `--force` em `main` ou branches de feature abertas
- Nunca usar `--no-verify` para bypassar o hook sem justificativa documentada
- Nunca versionar `.env`, `db_user`, `db_password` ou `target/`
- Sempre criar tag **depois** do merge na `main`, nunca antes
