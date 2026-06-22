# Prompts do Orquestrador — v0.11.0 Documentation & System Design

Este pacote contém os prompts revisados da etapa `v0.11.0-documentation-system-design`.

## Objetivo

Consolidar a documentação técnica e executiva do SRM Credit Engine em **português do Brasil**, destacando:

- README principal completo;
- AI_USAGE.md obrigatório;
- modelo de desenvolvimento assistido por IA;
- uso de subagents e agent orquestrador;
- C4 Context e Container;
- ER Diagram;
- justificativa da arquitetura;
- trade-offs técnicos;
- documentação Docker;
- documentação Prometheus;
- documentação de APIs;
- estratégia Git/branching;
- checklist final.

## IA usada no projeto

O AI_USAGE.md deve mencionar:

- GitHub Copilot
- OpenAI Codex
- Claude
- GPT

## Modelo de desenvolvimento usado

Documentar que cada etapa seguiu o fluxo:

1. definição da milestone;
2. criação de prompt específico;
3. Claude orientado a usar o agent orquestrador;
4. geração de plano técnico;
5. aprovação humana;
6. implementação;
7. relatório final de implementação;
8. revisão humana do escopo e `git status`;
9. commits organizados;
10. PR;
11. tag SemVer após merge.

## Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/documentation-system-design
```

## Tag futura

```bash
git checkout main
git pull origin main
git tag -a v0.11.0-documentation-system-design -m "Add project documentation and system design"
git push origin v0.11.0-documentation-system-design
```
