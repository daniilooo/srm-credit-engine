# SRM Credit Engine — Instruções para Claude Code

## Contexto do Projeto

Este projeto é um teste técnico para a construção do **SRM Credit Engine**, uma plataforma de cessão de crédito focada em recebíveis, precificação, câmbio, liquidação, auditoria e consistência financeira.

O projeto segue uma arquitetura de **monólito modular**, com separação clara entre domínio, aplicação, infraestrutura, interfaces e relatórios.

## Branch Atual

```text
feature/currency-engine
```

## Milestone Atual

```text
v0.4.0-currency-engine
```

## Milestones Anteriores

### v0.2.0-domain-model

Entregou:

- schema financeiro inicial com Flyway;
- entidades JPA;
- enums de domínio;
- constraints financeiras;
- índices iniciais;
- estrutura de Outbox Pattern;
- validação com `spring.jpa.hibernate.ddl-auto=validate`;
- backend subindo com Actuator `UP`.

### v0.3.0-pricing-engine

Entregou:

- motor de precificação com Strategy Pattern;
- uso de `BigDecimal`;
- arredondamento explícito com `RoundingMode.HALF_EVEN`;
- testes unitários;
- JaCoCo com cobertura mínima de 90%;
- script `scripts/pre-push.sh`;
- bloqueio local de push caso build, testes ou cobertura falhem.

## Stack Técnica

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- JUnit 5
- Mockito
- JaCoCo
- Maven Wrapper

### Infraestrutura

- Docker Compose
- PostgreSQL
- Git hooks locais via `scripts/pre-push.sh`

## Organização Arquitetural

O projeto deve manter separação entre:

```text
domain
application
infrastructure
interfaces
reporting
```

## Regras Gerais de Arquitetura

1. Manter o domínio separado da aplicação e da infraestrutura.
2. Não acoplar regra de negócio diretamente ao Spring/JPA quando isso puder ser evitado.
3. Não criar controllers REST sem aprovação explícita.
4. Não criar frontend nesta milestone.
5. Não implementar fluxo de liquidação nesta milestone.
6. Não alterar o Pricing Engine sem necessidade clara e aprovação explícita.
7. Não aplicar conversão cambial final no Pricing Engine.
8. Não usar `double` ou `float` em valores financeiros.
9. Usar `BigDecimal` para valores monetários, taxas e câmbio.
10. Manter `./mvnw clean verify` passando.
11. Manter cobertura JaCoCo maior ou igual a 90%.
12. Manter `scripts/pre-push.sh` funcional.
13. Não versionar `.git/hooks`.
14. Não commitar relatórios gerados pelo JaCoCo.
15. Não alterar Docker Compose sem necessidade real.
16. Não criar GitHub Actions nesta etapa, salvo aprovação explícita.

## Tarefa Atual

Implementar o **Currency Engine**.

O Currency Engine deve:

- suportar inicialmente BRL e USD;
- registrar taxas de câmbio manualmente por camada de domínio/aplicação;
- buscar a taxa de câmbio mais recente para um par direcional;
- tratar `BRL -> USD` e `USD -> BRL` como pares distintos;
- validar que a taxa de câmbio seja positiva;
- validar que moeda de origem e moeda de destino sejam diferentes;
- lançar exceção clara quando não houver taxa cadastrada para o par informado;
- usar o campo temporal efetivo existente, como `validAt` ou `validFrom`, para buscar a taxa mais recente;
- usar `createdAt DESC` como critério secundário em caso de empate temporal;
- evitar nova migration Flyway se o schema atual já atender ao Currency Engine;
- preparar o contrato para futura liquidação, sem persistir settlement agora.

## Escopo Permitido nesta Milestone

Pode implementar:

- serviços de domínio do Currency Engine;
- casos de uso na camada `application/currency`;
- contratos como `ExchangeRateProvider`, `ExchangeRateLookupService` ou equivalentes;
- exceções específicas de currency/câmbio;
- queries em repositório para buscar a taxa mais recente;
- testes unitários;
- testes de service/repository, se fizer sentido;
- ADR/README, se houver decisão arquitetural relevante.

## Fora do Escopo nesta Milestone

Não implementar:

- REST controller;
- endpoints públicos;
- frontend;
- settlement flow;
- persistência de liquidação;
- conversão cambial final no Pricing Engine;
- integração com API externa de câmbio;
- cache;
- mensageria;
- dispatcher de Outbox;
- GitHub Actions;
- mudanças no Docker Compose sem necessidade.

## Regras de Câmbio

1. As moedas suportadas inicialmente são `BRL` e `USD`.
2. Pares são direcionais.
3. `BRL -> USD` é diferente de `USD -> BRL`.
4. Não inverter taxa automaticamente nesta etapa.
5. Taxa deve ser estritamente positiva.
6. Moeda origem e moeda destino não podem ser iguais.
7. Se não houver taxa para o par informado, lançar exceção clara.
8. A taxa mais recente deve ser obtida por ordenação temporal descendente.
9. Em empate temporal, usar `createdAt DESC` como critério secundário.
10. `validTo`, se existir ou for citado, não entra na regra de busca nesta milestone.

## Regras de Precisão Financeira

1. Usar `BigDecimal`.
2. Não usar `double`.
3. Não usar `float`.
4. Comparar `BigDecimal` com `compareTo`.
5. Evitar conversões implícitas a partir de `double`.
6. Preferir criação de `BigDecimal` a partir de `String`, constantes ou valores já seguros.
7. Definir escala/arredondamento apenas quando necessário.
8. Não aplicar arredondamento escondido em regras de domínio sem documentar.

## Qualidade e Testes

A implementação deve manter:

- cobertura JaCoCo mínima de 90%;
- `./mvnw clean verify` passando;
- `scripts/pre-push.sh` passando;
- testes de cenários positivos;
- testes de cenários negativos;
- testes para taxa zero;
- testes para taxa negativa;
- testes para moedas iguais;
- testes para taxa inexistente;
- testes para busca da taxa mais recente;
- testes garantindo que o Pricing Engine não depende do Currency Engine.

## Comandos de Validação

A partir da raiz do projeto:

```bash
cd backend
./mvnw clean verify
```

A partir da raiz do projeto:

```bash
./scripts/pre-push.sh
```

Para validar o backend:

```bash
docker compose up -d
cd backend
./mvnw spring-boot:run
```

Em outro terminal:

```bash
curl http://localhost:8080/actuator/health
```

Resultado esperado:

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

## Fluxo de Trabalho com Claude Code

1. Primeiro, leia este arquivo `CLAUDE.md`.
2. Depois, leia os prompts da etapa em:

```text
prompts/currency-engine/
```

3. Comece pelo arquivo:

```text
prompts/currency-engine/00_ORQUESTRADOR_PLANO_CURRENCY_ENGINE.md
```

4. Antes de alterar arquivos, gere um plano técnico.
5. Aguarde aprovação explícita antes de implementar.
6. Implemente em passos pequenos.
7. Revise os arquivos alterados.
8. Rode os testes.
9. Rode o script de pre-push.
10. Informe arquivos alterados, decisões tomadas e resultado das validações.

## Prompts da Etapa

Ordem recomendada:

```text
prompts/currency-engine/00_ORQUESTRADOR_PLANO_CURRENCY_ENGINE.md
prompts/currency-engine/01_ORQUESTRADOR_IMPLEMENTACAO_CURRENCY_ENGINE.md
prompts/currency-engine/02_ORQUESTRADOR_VALIDACAO_CURRENCY_ENGINE.md
prompts/currency-engine/03_ORQUESTRADOR_PR_CURRENCY_ENGINE.md
```

## Regras de Segurança

Antes de executar comandos destrutivos, pedir confirmação.

Comandos que exigem atenção especial:

```bash
rm -rf
git reset --hard
git clean -fd
docker compose down -v
```

Não executar esses comandos sem explicar o impacto.

## Git Workflow

Branch atual:

```text
feature/currency-engine
```

Commits sugeridos:

```text
feat(currency): implement exchange rate engine
test(currency): cover exchange rate lookup and validations
docs(adr): document currency engine design decisions
```

Tag futura após merge na `main`:

```bash
git checkout main
git pull origin main
git tag -a v0.4.0-currency-engine -m "Implement currency exchange engine"
git push origin v0.4.0-currency-engine
```

## Critérios de Aceite

A etapa `v0.4.0-currency-engine` estará pronta quando:

- Currency Engine estiver implementado;
- BRL e USD estiverem suportados;
- pares direcionais estiverem respeitados;
- taxa positiva for obrigatória;
- moedas iguais forem rejeitadas;
- taxa mais recente por par for encontrada corretamente;
- taxa inexistente gerar exceção clara;
- nenhuma conversão final for aplicada no Pricing Engine;
- nenhum REST controller for criado;
- nenhum frontend for criado;
- nenhum settlement flow for criado;
- `./mvnw clean verify` passar;
- JaCoCo permanecer com cobertura mínima de 90%;
- `scripts/pre-push.sh` passar;
- backend subir e Actuator responder `UP`;
- ADR/README forem atualizados se necessário.

## Comunicação Esperada

Ao finalizar uma etapa, responda com:

1. resumo do que foi feito;
2. arquivos criados/alterados;
3. decisões técnicas tomadas;
4. se houve ou não migration Flyway;
5. testes criados;
6. resultado de `./mvnw clean verify`;
7. resultado de `scripts/pre-push.sh`;
8. pendências ou riscos;
9. sugestão de commits.
