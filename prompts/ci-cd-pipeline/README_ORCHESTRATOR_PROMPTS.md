# Prompts do Orquestrador — v0.14.0 CI/CD Pipeline

Este pacote contém os prompts da etapa `v0.14.0-ci-cd-pipeline`.

## Objetivo

Implementar pipeline de CI no GitHub Actions para validar automaticamente backend, frontend e Docker Compose em Pull Requests e pushes para `main`.

## Escopo esperado

- GitHub Actions;
- build e testes backend;
- validação de cobertura JaCoCo;
- build e testes frontend;
- validação Docker Compose;
- separação por jobs;
- documentação do pipeline;
- atualização de README, AI_USAGE e checklist final.

## Fora do escopo

Não implementar deploy real para AWS nesta etapa.

Não implementar:

- CD real;
- deploy em cloud;
- publicação de imagem Docker em registry;
- Terraform;
- secrets de produção;
- ambientes staging/prod reais;
- SonarCloud;
- GitHub Environments;
- permissões avançadas.

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/ci-cd-pipeline
```

## Tag futura

Após merge na main:

```bash
git checkout main
git pull origin main
git tag -a v0.14.0-ci-cd-pipeline -m "Add CI/CD pipeline with GitHub Actions"
git push origin v0.14.0-ci-cd-pipeline
```
