# Rodando com Docker Compose

Guia completo para subir o SRM Credit Engine localmente com Docker Compose.

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Docker | 24.x ou superior |
| Docker Compose | Plugin v2 (incluso no Docker Desktop) |
| RAM disponível | 4 GB recomendado |

Verifique sua versão:
```bash
docker --version
docker compose version
```

## Serviços disponíveis

| Serviço | Container | Porta local |
|---|---|---|
| PostgreSQL | `srm-credit-engine-postgres` | 5432 |
| Spring Boot REST API | `srm-credit-engine-backend` | 8080 |
| Angular SPA (via Nginx) | `srm-credit-engine-frontend` | 4200 |
| Prometheus | `srm-credit-engine-prometheus` | 9090 |

---

## 1. Clonar o repositório

```bash
git clone <url-do-repositório>
cd srm-credit-engine
```

---

## 2. Criar o arquivo `.env`

```bash
cp .env.example .env
```

O `.env.example` contém apenas configurações não sensíveis:

```
POSTGRES_DB=srm_credit_engine
POSTGRES_PORT=5432
BACKEND_PORT=8080
FRONTEND_PORT=4200
```

**Atenção:** o arquivo `.env` não é versionado. Nunca adicione credenciais a ele.

---

## 3. Criar os arquivos de secret

As credenciais do banco de dados são gerenciadas por **Docker Secrets** — arquivos montados em `/run/secrets/` dentro dos containers. Isso evita expor senhas em variáveis de ambiente.

```bash
# Criar o diretório de secrets (já existe no repositório)
# Criar os arquivos com as credenciais locais
echo "srm" > backend/secrets/db_user
echo "srm_password" > backend/secrets/db_password
```

**Importante:**
- Use qualquer usuário e senha de sua escolha para o ambiente local
- Os arquivos `db_user` e `db_password` são ignorados pelo `.gitignore`
- Nunca versione esses arquivos
- Os arquivos `.example` (ex: `db_user.example`) são apenas referências — não são usados em runtime

---

## 4. Subir a stack

```bash
docker compose up --build
```

O `--build` garante que as imagens do backend e frontend sejam reconstruídas. Na primeira execução, o Maven baixará dependências e o npm instalará pacotes — pode levar de 3 a 10 minutos.

Para subir em background:
```bash
docker compose up --build -d
```

---

## 5. Acompanhar os logs

```bash
# Todos os serviços
docker compose logs -f

# Apenas o backend
docker compose logs -f backend

# Apenas o frontend
docker compose logs -f frontend
```

---

## 6. Validar o ambiente

### Backend
```bash
# Health check
curl http://localhost:8080/actuator/health
# Esperado: {"groups":["liveness","readiness"],"status":"UP"}

# Swagger UI
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html
# Esperado: 200
```

### Frontend
```bash
# SPA servida pelo Nginx
curl -s -o /dev/null -w "%{http_code}" http://localhost:4200
# Esperado: 200

# Proxy do Nginx para a API
curl http://localhost:4200/api/v1/reports/settlements
# Esperado: 200 com JSON de extrato vazio
```

### Prometheus
```bash
curl http://localhost:9090/-/healthy
# Esperado: 200
```

---

## 7. URLs úteis

| URL | Descrição |
|---|---|
| http://localhost:4200 | Painel do operador (Angular SPA) |
| http://localhost:8080/swagger-ui/index.html | Documentação interativa da API |
| http://localhost:8080/actuator/health | Status da aplicação |
| http://localhost:8080/actuator/prometheus | Métricas no formato Prometheus |
| http://localhost:9090 | Interface do Prometheus |

---

## 8. Parar a stack

```bash
# Parar sem remover volumes (preserva dados do banco)
docker compose down

# Parar e remover volumes (dados serão perdidos)
docker compose down -v
```

**Atenção:** `docker compose down -v` remove o volume `postgres-data`. Os dados do banco serão perdidos.

---

## 9. Secrets — Por que arquivo e não variável de ambiente

Variáveis de ambiente são visíveis via `docker inspect` e em logs de erro. O Docker Secrets por arquivo monta as credenciais diretamente em `/run/secrets/<nome>` com permissões restritas (leitura apenas pelo processo do container).

O backend lê as credenciais no `docker-entrypoint.sh`:
```sh
DB_USER=$(cat /run/secrets/db_user)
DB_PASSWORD=$(cat /run/secrets/db_password)
```

O PostgreSQL usa `POSTGRES_USER_FILE` e `POSTGRES_PASSWORD_FILE`, que seguem o mesmo padrão.

---

## 10. Troubleshooting

### Backend não sobe — erro Flyway

Se o backend falhar com erro de migração Flyway, verifique se os secrets estão criados corretamente:
```bash
ls -la backend/secrets/
# db_user e db_password devem existir
cat backend/secrets/db_user
```

### Porta já em uso

```bash
# Verificar qual processo usa a porta 8080
lsof -i :8080

# Alterar porta no .env
BACKEND_PORT=8081
```

### Secret não encontrado

```bash
docker compose logs backend | grep "secret"
```
Se houver erro de secret, confirme que os arquivos existem e não estão vazios:
```bash
wc -c backend/secrets/db_user backend/secrets/db_password
```

### PostgreSQL não passa no healthcheck

O healthcheck usa `pg_isready`. Se falhar repetidamente, aumente o `start_period` no `docker-compose.yml` (padrão: 30s para o backend, já que o Postgres tem 5 tentativas × 10s).

---

## 11. Desenvolvimento local (sem Docker)

Consulte o `README.md` na raiz do projeto para instruções de execução local do backend com `./mvnw spring-boot:run` e do frontend com `npm start`.
