# ADR 0010 — Estratégia Git para Gestão de Crise

**Status:** Aceito

**Data:** 2026-06-22

**Autores:** Equipe SRM Credit Engine

---

## Contexto

O SRM Credit Engine opera em domínio financeiro. Erros em produção podem impactar liquidações, dashboards de operações, observabilidade e rastreabilidade de auditoria. Com o projeto em maturidade operacional (11 milestones concluídas), é necessário documentar formalmente como o time responderia a incidentes usando Git de forma segura e rastreável.

O projeto já possui:
- Estratégia de branching documentada em `docs/git/branching-strategy.md`
- SemVer com tags por milestone
- Pre-push hook de qualidade (`scripts/pre-push.sh`)
- Um incidente real registrado como simulação: métrica `settlements.created.total` → `settlements_total` no Prometheus (INC-20260415-001 simulado)

Sem uma estratégia documentada, o time sob pressão de um incidente pode tomar decisões técnicas precipitadas — como `git reset --hard` em branch compartilhada ou force push em main — que destroem histórico e causam problemas adicionais.

---

## Decisão

**O padrão de recuperação de código para incidentes em branches compartilhadas é `git revert`, não `git reset --hard`.**

Adicionalmente:

- Hotfix é usado quando a correção exige código novo (não apenas reversão)
- Cherry-pick é usado quando um commit específico de outro branch deve ir para main sem trazer o branch inteiro
- `git reset --hard` é proibido em branches compartilhadas
- Force push em `main` é proibido em qualquer circunstância
- Toda operação de emergência deve ser feita via branch + PR, nunca diretamente em main

---

## Por Que `git revert` em Branch Compartilhada

| Critério | `git revert` | `git reset --hard` |
|---|---|---|
| Altera histórico? | Não — cria novo commit | Sim — apaga commits existentes |
| Seguro em branch compartilhada? | **Sim** | **Não** |
| Requer force push? | Não | Sim (se a branch já foi publicada) |
| Rastreável? | Sim — o commit de revert é visível no log | Não — os commits desaparecem do histórico |
| Reversível? | Sim — pode-se reverter o revert | Difícil — exige `reflog` e intervenção manual |
| Impacto em colaboradores? | Nenhum — pull normal funciona | Quebra o repositório local de quem fez pull |

**Razão prática:** em um incidente, a última coisa que o time precisa é de um segundo incidente causado por reescrita de histórico. O `git revert` é conservador por design — ele **adiciona** informação ao histórico em vez de destruir.

---

## Por Que Evitar Reescrever Histórico em Branch Compartilhada

O histórico Git é um recurso de auditoria crítico em domínio financeiro. Saber **quando** uma mudança foi introduzida, **quem** a introduziu e **o que** foi alterado é essencial para:

1. Rastrear quando um bug foi introduzido
2. Correlacionar uma falha com um deploy específico
3. Auditar decisões arquiteturais em investigações pós-incidente
4. Preservar a rastreabilidade exigida em ambientes regulados (FIDC, cessão de crédito)

Um `git reset --hard` seguido de force push remove permanentemente essa rastreabilidade. O `git revert` preserva tudo e **adiciona** contexto — o commit de revert informa o ID do incidente, a razão e o hash original.

---

## Quando Usar Hotfix

O hotfix é preferível ao revert quando:

1. O problema exige código novo — não é possível apenas desfazer um commit existente
2. O commit problemático está entrelaçado com outros commits que devem ser mantidos
3. A solução é pequena e pode ser implementada, testada e mergeada rapidamente

**Fluxo do hotfix:**
```
main → branch hotfix/* → commit de correção → PR → merge → tag patch (opcional)
```

O hotfix **nunca** vai diretamente para main — sempre via branch + PR, mesmo sob pressão de tempo. Um PR com review mínimo de 1 pessoa é o checkpoint de segurança.

---

## Quando Usar Cherry-pick

O cherry-pick é adequado quando:

1. A correção já existe em outro branch (feature ou fix) que ainda não foi mergeado
2. Apenas aquele commit específico deve ir para main, sem trazer o restante do branch
3. A equipe avaliou que o cherry-pick não causará conflitos no PR original

**Cuidado:** cherry-pick cria um commit com novo hash que contém as mesmas mudanças. Quando o PR original for mergeado, Git pode apresentar conflitos ou duplicações. Comunicar ao autor do PR original é obrigatório.

---

## Regras Invioláveis

| Regra | Justificativa |
|---|---|
| Nunca `git reset --hard` em branches compartilhadas | Reescreve histórico, quebra colaboradores |
| Nunca `git push --force` em `main` | Destrói o histórico da branch principal |
| Sempre branch + PR para correções de emergência | Garante review mínimo e CI passando |
| Mensagem de commit referencia o ID do incidente | Rastreabilidade no histórico |
| Build deve passar antes do merge do fix | Um hotfix que quebra os testes é pior que o bug |

---

## Consequências

### Positivas

- Histórico Git preservado e auditável mesmo após incidentes
- Colaboradores não têm seus repositórios locais quebrados por operações de emergência
- Processo documentado reduz tempo de decisão sob pressão — o time sabe o que fazer
- Commits de revert e hotfix ficam visíveis no log com referência ao incidente

### Negativas / Trade-offs Aceitos

- `git revert` é marginalmente mais lento que `reset --hard` — requer criação de branch e PR
- Em incidentes SEV-1 com urgência extrema, o processo de PR pode parecer burocrático
- Cherry-pick pode criar conflitos no PR original — exige comunicação adicional

**Mitigação:** para SEV-1, o processo de PR pode ser acelerado (1 aprovador, review em 5 minutos), mas nunca eliminado. O checklist de release e o playbook definem critérios de aceleração.

---

## Alternativas Consideradas

### Alternativa 1 — `git reset --hard` + force push como estratégia de rollback

**Rejeitada.** Destrói histórico, quebra repositórios locais de colaboradores, não é rastreável e é irreversível sem uso de `git reflog` (que tem TTL e pode expirar). Nunca deve ser recomendado para branches compartilhadas.

### Alternativa 2 — Merge direto na main sem PR

**Rejeitada.** Elimina o único checkpoint de segurança durante um incidente. Um segundo par de olhos no diff de um hotfix pode detectar um erro que agravaria o incidente. O custo de tempo de um PR mínimo (5–10 minutos) é insignificante comparado ao risco de um segundo incidente.

### Alternativa 3 — Rollback de deploy como único mecanismo de recuperação

**Rejeitada como estratégia única.** Rollback de deploy (reimplantar imagem anterior) é a primeira linha de contenção para SEV-1 e SEV-2, mas não resolve todos os cenários — ex: se o bug exige uma migration de rollback, ou se a imagem anterior não está disponível. Git revert e hotfix são necessários como mecanismos complementares.

### Alternativa 4 — Branch de release separado (gitflow)

**Rejeitada.** O projeto usa GitHub Flow simplificado (feature branches → main), que é adequado para o tamanho e cadência do time. Gitflow adicionaria complexidade de manutenção de branches `release/*` e `develop` sem benefício proporcional.
