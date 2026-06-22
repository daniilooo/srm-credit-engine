# Prompt — Orquestrador — Validação — v1.0.0 Final Release

Use `agents/agent_orquestrador.md`.

Valide a etapa `v1.0.0-final-release`.

## Checklist

1. Release notes criadas.
2. README revisado.
3. AI_USAGE revisado.
4. Checklist final atualizado.
5. Documentação principal acessível por links.
6. Nenhum secret real versionado.
7. Nenhum arquivo build versionado.
8. Nenhum prompt antigo solto indevido.
9. Nenhuma nova feature funcional introduzida.
10. Nenhuma migration alterada.
11. Nenhuma regra de negócio alterada.
12. Docker Compose validado.
13. Backend testado.
14. Frontend build/test validado ou justificativa documentada.
15. CI/CD documentado.
16. Tags/milestones refletidas.
17. Limitações conhecidas documentadas.
18. Próximos passos documentados.
19. Script da tag v1.0.0 preparado.
20. Projeto pronto para entrega.

## Comandos

```bash
git status --short
git diff --name-only
git ls-files | grep -E '(^|/)(\\.env|db_user|db_password)$'
docker compose config
```

Quando possível:

```bash
cd backend && ./mvnw -B clean verify
```

```bash
cd frontend && npm ci && npm run build && npm test -- --watch=false --browsers=ChromeHeadless
```

```bash
docker compose up -d --build
curl http://localhost:8080/actuator/health
curl -I http://localhost:4200
curl -I http://localhost:8080/swagger-ui/index.html
curl -I http://localhost:9090
docker compose down
```

## Responda com

1. resultado por subagent;
2. problemas encontrados;
3. correções obrigatórias;
4. correções opcionais;
5. critérios de aceite;
6. commits sugeridos;
7. título de PR sugerido;
8. riscos restantes;
9. comando de tag v1.0.0 após merge.
