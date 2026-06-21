# Prompts do Orquestrador — SRM Credit Engine — v0.4.0 Currency Engine

Este pacote contém apenas prompts para o **agent_orquestrador**.

A ideia é centralizar a governança no orquestrador e deixar que ele consulte/demande os subagents existentes no diretório `agents/`.

## Estrutura sugerida no projeto

```text
srm-credit-engine/
├── agents/
├── prompts/
│   └── currency-engine/
│       ├── README_ORCHESTRATOR_PROMPTS.md
│       ├── 00_ORQUESTRADOR_PLANO_CURRENCY_ENGINE.md
│       ├── 01_ORQUESTRADOR_IMPLEMENTACAO_CURRENCY_ENGINE.md
│       ├── 02_ORQUESTRADOR_VALIDACAO_CURRENCY_ENGINE.md
│       └── 03_ORQUESTRADOR_PR_CURRENCY_ENGINE.md
```

## Ordem de uso

1. `00_ORQUESTRADOR_PLANO_CURRENCY_ENGINE.md`
2. Revisar plano gerado.
3. Aprovar ou ajustar pontos pendentes.
4. `01_ORQUESTRADOR_IMPLEMENTACAO_CURRENCY_ENGINE.md`
5. Rodar validações locais.
6. `02_ORQUESTRADOR_VALIDACAO_CURRENCY_ENGINE.md`
7. Commit, push e PR.
8. `03_ORQUESTRADOR_PR_CURRENCY_ENGINE.md`

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/currency-engine
```

## Tag futura

```bash
git checkout main
git pull origin main
git tag -a v0.4.0-currency-engine -m "Implement currency exchange engine"
git push origin v0.4.0-currency-engine
```
