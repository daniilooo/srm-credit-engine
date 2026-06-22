# Prompts do Orquestrador — v0.12.0 Scale & EDA Design

Este pacote contém os prompts da etapa `v0.12.0-scale-and-eda-design`.

## Objetivo

Criar documentação arquitetural de evolução para escala Staff/Principal, demonstrando como o SRM Credit Engine poderia evoluir para suportar alto volume, processamento assíncrono, Event-Driven Architecture, CQRS e resiliência operacional.

## Importante

Esta etapa é **documental**.

Não implementar:

- Kafka real;
- RabbitMQ real;
- workers reais;
- CQRS real;
- novas tabelas;
- novas migrations;
- alterações no backend;
- alterações no frontend;
- alterações no Docker Compose;
- GitHub Actions;
- Grafana;
- OpenTelemetry.

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/scale-and-eda-design
```

## Tag futura

Após merge na main:

```bash
git checkout main
git pull origin main
git tag -a v0.12.0-scale-and-eda-design -m "Add scale and event-driven architecture design"
git push origin v0.12.0-scale-and-eda-design
```
