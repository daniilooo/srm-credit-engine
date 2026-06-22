# Prompt — Orquestrador — Validação — v0.8.0 Frontend Operator Panel

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/frontend-operator-panel
```

A implementação da etapa `v0.8.0-frontend-operator-panel` foi concluída localmente.

## Checklist obrigatório

1. Frontend Angular implementado.
2. Backend não alterado indevidamente.
3. Rotas criadas.
4. Tela de pricing simulation criada.
5. Tela de exchange rates criada.
6. Tela de settlement criada.
7. Tela de settlement report criada.
8. Services Angular dedicados criados.
9. Models TypeScript criados.
10. Reactive Forms usados.
11. HTTP calls usando baseUrl centralizada.
12. Tabela do relatório com paginação server-side.
13. Filtros do relatório funcionais.
14. Loading states implementados.
15. Error states implementados.
16. Success states implementados.
17. Sem `any` desnecessário.
18. Sem autenticação/autorização.
19. Sem integração externa.
20. Sem alteração em Docker Compose.
21. Build Angular passa.
22. Testes/lint passam, se configurados.
23. Backend segue com `./mvnw clean verify` passando.
24. README atualizado, se necessário.

## Comandos

Backend:

```bash
cd backend
./mvnw clean verify
```

Frontend:

```bash
cd frontend
npm install
npm run build
```

Se houver testes:

```bash
npm test -- --watch=false
```

Se houver lint:

```bash
npm run lint
```

## Responda com

1. Resultado da revisão por subagent.
2. Problemas encontrados.
3. Correções obrigatórias.
4. Correções opcionais.
5. Confirmação de critérios de aceite.
6. Sugestão de divisão de commits.
7. Sugestão de título do PR.
8. Riscos restantes.
9. Próxima etapa após merge/tag.
