# Prompts do Orquestrador — SRM Credit Engine — v0.8.0 Frontend Operator Panel

Este pacote contém prompts para a etapa **v0.8.0-frontend-operator-panel** do projeto SRM Credit Engine.

## Objetivo

Implementar o **Painel do Operador** em Angular, consumindo a REST API já criada no backend.

A etapa deve entregar:

- tela de simulação de pricing;
- tela de registro/consulta de taxa de câmbio;
- tela de liquidação de recebível;
- grid de extrato de liquidação com filtros e paginação server-side.

## Estrutura sugerida

```text
srm-credit-engine/
├── prompts/
│   └── frontend-operator-panel/
│       ├── README_ORCHESTRATOR_PROMPTS.md
│       ├── 00_ORQUESTRADOR_PLANO_FRONTEND_OPERATOR_PANEL.md
│       ├── 01_ORQUESTRADOR_IMPLEMENTACAO_FRONTEND_OPERATOR_PANEL.md
│       ├── 02_ORQUESTRADOR_VALIDACAO_FRONTEND_OPERATOR_PANEL.md
│       └── 03_ORQUESTRADOR_PR_FRONTEND_OPERATOR_PANEL.md
```

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/frontend-operator-panel
```

## Milestone

```text
v0.8.0-frontend-operator-panel
```

## Tag futura

Após merge na `main`:

```bash
git checkout main
git pull origin main
git tag -a v0.8.0-frontend-operator-panel -m "Implement Angular operator panel"
git push origin v0.8.0-frontend-operator-panel
```
