# Prompt — Orquestrador — Implementação — v0.8.0 Frontend Operator Panel

Use o arquivo `agents/agent_orquestrador.md` como sua persona principal.

Estamos na branch:

```text
feature/frontend-operator-panel
```

Milestone:

```text
v0.8.0-frontend-operator-panel
```

## Status

O plano técnico foi aprovado. Agora implemente de forma sequencial e controlada.

## Decisões padrão recomendadas

1. Implementar frontend Angular backend-consuming.
2. Não alterar backend sem aprovação.
3. Usar Reactive Forms.
4. Criar API services dedicados.
5. Criar TypeScript models dedicados.
6. Usar environment para base URL.
7. Criar rotas:
   - `/pricing`
   - `/exchange-rates`
   - `/settlements`
   - `/reports/settlements`
8. Criar tela de pricing simulation.
9. Criar tela de exchange rates.
10. Criar tela de settlement.
11. Criar tela de settlement report.
12. Implementar paginação server-side no relatório.
13. Tratar loading, success e error.
14. Não usar `any` sem justificativa.
15. Não criar autenticação.
16. Não criar integração externa.
17. Não alterar Docker Compose.
18. Manter build Angular passando.
19. Rodar testes/lint se disponíveis.

## Sequência obrigatória

1. Revisar estrutura atual do frontend.
2. Confirmar versão Angular e se usa standalone components.
3. Revisar Angular Material/PrimeNG disponível ou instalar somente se aprovado.
4. Criar/ajustar environment com API base URL.
5. Criar modelos TypeScript.
6. Criar API services.
7. Criar rotas.
8. Criar componentes/telas.
9. Implementar Reactive Forms.
10. Implementar chamada real aos endpoints.
11. Implementar tratamento de erros.
12. Implementar loading/success states.
13. Implementar tabela do extrato com filtros e paginação server-side.
14. Rodar build.
15. Rodar testes/lint se disponíveis.
16. Validar manualmente com backend rodando.
17. Atualizar README, se necessário.

## Endpoints disponíveis

```text
POST /api/v1/pricing/simulations
POST /api/v1/exchange-rates
GET  /api/v1/exchange-rates/latest
POST /api/v1/settlements
GET  /api/v1/reports/settlements
```

## Validações esperadas

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

Aplicação:

```bash
cd backend
./mvnw spring-boot:run
```

```bash
cd frontend
npm start
```

## Ao final, responda com

1. Arquivos criados/alterados.
2. Decisões técnicas.
3. Rotas implementadas.
4. Componentes criados.
5. Services criados.
6. Models criados.
7. Como paginação server-side foi implementada.
8. Como erros/loading/success foram tratados.
9. Resultado do build frontend.
10. Resultado dos testes/lint, se aplicável.
11. Confirmação de que backend não foi alterado ou listar alterações.
12. Checklist de aceite.
13. Sugestão de commits.
14. Próxima etapa sugerida.

Não faça push, não abra PR e não crie tag.
