# SRM Credit Engine

Plataforma de cessão de crédito multimoedas para simulação, precificação e liquidação de recebíveis.

## Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Validation
- Spring Data JPA
- PostgreSQL
- Flyway
- OpenAPI/Swagger
- Resilience4j
- Micrometer + Prometheus
- Testcontainers
- JUnit 5 + Mockito

### Frontend

- Angular
- Angular Material ou PrimeNG
- Reactive Forms
- RxJS
- Tabela server-side com filtros

### Infra

- Docker Compose
- PostgreSQL
- Prometheus
- GitHub Actions
- Pre-commit hook simples
- PlantUML/Mermaid

## Arquitetura

A solução será construída como um modular monolith, separando claramente domínio, aplicação, infraestrutura, interfaces REST e relatórios analíticos.

## Como rodar

Em construção.

## Git Hook pre-push (local)

Para garantir build, testes e cobertura mínima local antes de um push, execute os seguintes passos locais:

```bash
chmod +x scripts/pre-push.sh
cp scripts/pre-push.sh .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

O hook executará `./mvnw clean verify` dentro da pasta `backend` e bloqueará o push caso o build falhe, os testes falhem ou a cobertura JaCoCo seja menor que 90%.

Observação: `.git/hooks` não é versionado; o script `scripts/pre-push.sh` é versionado e deve ser instalado manualmente em cada desenvolvedor.

## Documentação

- `agents/`: personas e workflow dos agentes de IA.
- `docs/adr/`: Architecture Decision Records.
- `docs/c4/`: diagramas C4.
- `docs/er/`: modelo entidade-relacionamento.
- `docs/eda/`: proposta de arquitetura orientada a eventos.
- `docs/crisis-management/`: simulação de gestão de crise com Git.

## AI Usage

O uso de IA será documentado no arquivo `AI_USAGE.md`.
