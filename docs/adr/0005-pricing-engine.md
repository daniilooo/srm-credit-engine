# ADR 0005 — Pricing Engine: Strategy Pattern, BigDecimal and Rounding

## Context

Implementação do motor de precificação para o SRM Credit Engine na versão `v0.3.0-pricing-engine`.

## Decisão

- Utilizar Strategy Pattern para encapsular spreads por tipo de recebível.
- Implementar estratégias iniciais com spreads hardcoded:
  - `DUPLICATA` => 0.015000 (1.5% a.m.)
  - `CHEQUE_PRE_DATADO` => 0.025000 (2.5% a.m.)
- Usar `BigDecimal` exclusivamente para todos os cálculos financeiros.
- Arredondamento: `RoundingMode.HALF_EVEN`.
- Resultado final com 4 casas decimais.
- `PricingException` estende `RuntimeException`.
- Cobertura mínima de testes: 90% por JaCoCo.
- Pre-push hook local para validar `mvnw clean verify` antes do push.

## Consequências

- Lógica de precificação isolada do JPA e pronta para testes.
- Spreads podem ser externalizados no futuro para tabela `receivable_types`.
- Garantia de qualidade local antes do push via JaCoCo e script.

