# Pacote atual: 2.8.6

Correção da confirmação com preço desatualizado e preservação do anúncio em falha de retirada.

- `GTS-Shirokai-2.8.6.jar`: marca original.
- `GTS-Zorykai-2.8.6.jar`: mesma lógica e IDs, com a variante visual Zorykai preservada.

Instale somente uma variante e remova o JAR antigo com o servidor parado. As configurações continuam em `config/gts`.

53 testes JUnit e 77 verificações em servidor isolado com jogadores simulados. [Evidências e limites](../docs/STALE_PRICE_FIX.md). Não há recuperação automática de perdas antigas.

O workflow **Upload tested release JAR** verifica SHA256SUMS e publica o pacote Shirokai testado. Este hotfix foi preparado numa branch separada, sem criar tag ou alterar uma release existente.
