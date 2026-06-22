# Prompts do Orquestrador — SRM Credit Engine — v0.5.0 Settlement Flow

Este pacote contém apenas prompts para o **agent_orquestrador**.

A ideia é centralizar a governança no orquestrador e deixar que ele consulte/demande os subagents existentes no diretório `agents/`.

## Estrutura sugerida no projeto

```text
srm-credit-engine/
├── agents/
├── prompts/
│   └── settlement-flow/
│       ├── README_ORCHESTRATOR_PROMPTS.md
│       ├── 00_ORQUESTRADOR_PLANO_SETTLEMENT_FLOW.md
│       ├── 01_ORQUESTRADOR_IMPLEMENTACAO_SETTLEMENT_FLOW.md
│       ├── 02_ORQUESTRADOR_VALIDACAO_SETTLEMENT_FLOW.md
│       └── 03_ORQUESTRADOR_PR_SETTLEMENT_FLOW.md
```

## Ordem de uso

1. `00_ORQUESTRADOR_PLANO_SETTLEMENT_FLOW.md`
2. Revisar plano gerado.
3. Aprovar ou ajustar pontos pendentes.
4. `01_ORQUESTRADOR_IMPLEMENTACAO_SETTLEMENT_FLOW.md`
5. Rodar validações locais.
6. `02_ORQUESTRADOR_VALIDACAO_SETTLEMENT_FLOW.md`
7. Commit, push e PR.
8. `03_ORQUESTRADOR_PR_SETTLEMENT_FLOW.md`

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/settlement-flow
```

## Validação obrigatória

```bash
cd backend
./mvnw clean verify
```

```bash
cd ..
./scripts/pre-push.sh
```

## Tag futura

Após merge do PR na `main`:

```bash
git checkout main
git pull origin main
git tag -a v0.5.0-settlement-flow -m "Implement transactional settlement flow"
git push origin v0.5.0-settlement-flow
```
