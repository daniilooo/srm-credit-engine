# Hotfix e Cherry-pick — Estratégias de Correção de Emergência

## Diferença Entre as Quatro Estratégias

Antes de escolher uma abordagem, entender o que cada uma faz:

| Estratégia | O que faz | Altera histórico? | Quando usar |
|---|---|---|---|
| **Rollback de deploy** | Reimplanta a imagem Docker anterior sem alterar o código | Não | Primeira linha de contenção — mais rápido |
| **`git revert`** | Cria um novo commit que desfaz um commit anterior específico | Não (adiciona commit) | Commit problemático identificado; branch compartilhada |
| **Hotfix** | Nova branch a partir de `main`, corrige o problema, mergeia como PR | Não | Correção nova necessária; revert não é suficiente |
| **Cherry-pick** | Copia um commit específico de outro branch para o branch atual | Não (copia commit) | Um commit pronto em outro branch deve ir para main sem trazer tudo |

> **Regra de ouro:** nunca `git reset --hard` em branches compartilhadas. Nunca `git push --force` em `main`. Esses comandos reescrevem histórico e quebram o repositório para os demais colaboradores.

---

## Quando Usar Hotfix

Use um hotfix quando:

- O bug em produção **não pode ser resolvido apenas revertendo** um commit (a correção exige código novo)
- A correção é simples o suficiente para ser implementada, testada e mergeada rapidamente
- Não existe release regular planejada para as próximas horas

**Exemplos de quando hotfix é a escolha certa:**
- Secret de banco não configurado no ambiente de produção — revert não resolve, é necessário adicionar configuração
- Frontend com URL de API incorreta em produção após deploy — revert levaria ao estado anterior com outro problema
- Validação de entrada aceita valor inválido que causa crash em produção — revert removeria outras mudanças não problemáticas do mesmo PR

**Quando hotfix não é necessário:**
- O problema é resolvido com rollback de deploy (mais rápido)
- O commit problemático é isolado e pode ser revertido de forma limpa

---

## Quando Usar Cherry-pick

Use cherry-pick quando:

- Uma correção já foi implementada em um branch de feature ainda não mergeado
- Apenas aquele commit específico deve ir para `main` — não o branch inteiro
- A correção não depende de outros commits do mesmo branch

**Exemplo prático:**  
O time está trabalhando em `feature/nova-liquidacao-parcelada` com 8 commits. O commit número 3 desse branch corrige um bug crítico de validação que também afeta `main`. Em vez de aguardar o PR completo ser mergeado (o que pode levar dias), faz-se cherry-pick somente do commit 3 para `main`.

**Cuidado com cherry-pick:**
- O mesmo commit existirá em dois branches com hashes diferentes — isso pode causar conflitos quando o PR original for mergeado
- Usar cherry-pick com moderação; preferir hotfix se a correção for simples de reimplementar
- Documentar qual commit foi cherry-picked e por quê

---

## Fluxo de Hotfix

### Passo 1 — Criar branch de hotfix a partir de main

```bash
# Garantir que está na main atualizada
git checkout main
git pull origin main

# Criar branch de hotfix com nome descritivo
git checkout -b hotfix/fix-settlement-validation
```

> A convenção `hotfix/*` sinaliza para o time que este é um branch de emergência com critérios de revisão acelerada.

### Passo 2 — Implementar a correção

```bash
# Implementar a correção mínima necessária
# NÃO incluir refatorações, melhorias ou mudanças não relacionadas ao bug
# Um hotfix deve ter o menor diff possível
```

### Passo 3 — Validar localmente

```bash
# Build completo
cd backend
./mvnw clean verify

# Se a correção for de frontend:
cd frontend
npm install
npm run build

# Subir o stack completo para validação manual
docker compose up --build

# Testar o cenário específico que estava falhando
curl -X POST http://localhost:8080/api/v1/settlements \
  -H "Content-Type: application/json" \
  -d '{"receivableId": "uuid-do-receivable", "paymentCurrencyCode": "USD"}'
```

### Passo 4 — Commit com mensagem rastreável

```bash
git add <arquivos-modificados>

git commit -m "fix(settlement): reject settlement when receivable status is invalid

Prevents NullPointerException when settlement is attempted on a
receivable with status PENDING_REVIEW.

Fixes: INC-20260422-002
Severity: SEV-2"
```

### Passo 5 — Abrir PR com revisão acelerada

```bash
git push origin hotfix/fix-settlement-validation
```

Título do PR: `fix(settlement): hotfix — reject invalid receivable status [INC-20260422-002]`

**Critérios de revisão acelerada em hotfix:**
- Mínimo 1 revisor (preferencialmente o Tech Lead)
- CI deve passar (build + testes)
- Diff mínimo — sem mudanças não relacionadas
- O revisor confirma que a correção resolve o problema reportado

### Passo 6 — Merge e tag de patch (se necessário)

```bash
# Após merge na main
git checkout main
git pull origin main

# Tag de patch — apenas se o incidente justificar rastreabilidade de versão
# Convenção: vMAJOR.MINOR.PATCH
# Exemplo: se a versão atual é v0.10.0, o hotfix seria v0.10.1
git tag -a v0.10.1-hotfix -m "Hotfix: reject invalid receivable status (INC-20260422-002)"
git push origin v0.10.1-hotfix
```

> A tag de patch é **opcional** para SEV-3 e SEV-4. Para SEV-1 e SEV-2, é **recomendada** para rastrear exatamente qual versão está em produção após a correção.

---

## Fluxo de Cherry-pick

### Passo 1 — Identificar o commit a ser copiado

```bash
# Ver o log do branch de origem
git log --oneline feature/nova-liquidacao-parcelada

# Saída exemplo:
# fgh3456 feat: add installment pricing strategy
# ijk7890 feat: add installment settlement flow
# lmn1234 fix: validate settlement amount is positive  ← este é o que queremos
# opq5678 feat: add installment receivable type
# ...
```

### Passo 2 — Criar branch de cherry-pick a partir de main

```bash
git checkout main
git pull origin main
git checkout -b fix/cherry-pick-settlement-validation
```

### Passo 3 — Executar o cherry-pick

```bash
# Copiar apenas o commit desejado
git cherry-pick lmn1234

# Se houver conflitos, resolver manualmente e depois:
git cherry-pick --continue

# Se quiser abortar:
git cherry-pick --abort
```

### Passo 4 — Validar e abrir PR

```bash
# Validar o build
cd backend && ./mvnw clean verify

# Publicar e abrir PR
git push origin fix/cherry-pick-settlement-validation
```

Título do PR: `fix(settlement): cherry-pick settlement amount validation from feature branch`

**Incluir no corpo do PR:**
- De qual branch e commit foi feito o cherry-pick
- Por que não aguardar o PR original
- Link para o PR original

---

## Checklist Antes de Abrir PR de Hotfix ou Cherry-pick

- [ ] O diff está mínimo — apenas o necessário para corrigir o bug
- [ ] Nenhuma refatoração ou melhoria incluída
- [ ] `./mvnw clean verify` passou localmente
- [ ] O cenário de crise foi testado manualmente
- [ ] Nenhum segredo real no código
- [ ] Mensagem de commit referencia o ID do incidente
- [ ] PR tem título claro com prefixo `fix(` e referência ao incidente

---

## Cuidados Gerais

| Situação | Recomendação |
|---|---|
| Hotfix introduz regressão | Reverter o hotfix com `git revert` antes de tentar nova correção |
| Cherry-pick causa conflito no PR original | Comunicar ao autor do PR original; ele precisará adaptar a base |
| Hotfix mergeado mas bug persiste | Não fechar o incidente — investigar se a causa raiz era outra |
| Pressão para pular testes em hotfix | Manter testes obrigatórios — um hotfix quebrado é pior que esperar |
| Múltiplos hotfixes no mesmo dia | Avaliar se a arquitetura ou o processo de testes precisa de melhoria |

---

## Resumo — Árvore de Decisão Rápida

```
Bug em produção identificado
  ↓
Existe imagem Docker anterior estável?
  → Sim → Rollback de deploy (mais rápido)
  → Não ↓

O bug foi introduzido por um commit isolado identificável?
  → Sim, e revert é suficiente → git revert <hash> em branch fix/*
  → Sim, mas revert não resolve (correção nova necessária) → Hotfix
  → Não identificado → Hotfix

A correção já existe em outro branch (feature)?
  → Sim, e pode ser isolada → Cherry-pick do commit específico
  → Não → Hotfix novo
```
