# AI Usage Report

Este projeto utiliza IA como apoio ao desenvolvimento, arquitetura, revisão técnica e documentação.

## Ferramentas utilizadas

- ChatGPT
- GitHub Copilot / IDE Assistant, quando aplicável
- Agentes especializados definidos em `/agents`

## Diretriz de uso

A IA será utilizada como copiloto técnico. Todas as decisões arquiteturais, regras financeiras, tratamento de concorrência, modelagem de dados e validações críticas serão revisadas manualmente.

## Registro de uso

Durante o desenvolvimento, serão documentados:

1. Prompts estratégicos utilizados.
2. Sugestões aceitas.
3. Sugestões rejeitadas.
4. Erros, alucinações ou riscos identificados.
5. Decisões técnicas tomadas pelo desenvolvedor.

## Exemplos esperados de análise crítica

- Rejeição de `double` para valores monetários.
- Uso de `BigDecimal` para precisão financeira.
- Aplicação de câmbio após o cálculo do valor presente.
- Uso de transação ACID na liquidação.
- Uso de optimistic locking contra dupla liquidação.
