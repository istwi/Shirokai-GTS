# Preço desatualizado e retirada — 2.8.6

## Base e diagnóstico

Base: `origin/zorykai` (`fc56b87`), que contém as fontes da 2.8.5 e o pacote com a marca visual Zorykai. `main` ainda estava na 2.8.3 na consulta de 20/11/2026. Não houve alteração de IDs, comandos, permissões, namespaces ou esquema dos anúncios/recibos.

A confirmação de compra mantinha uma referência ao anúncio mutável: o preço exibido podia ser diferente daquele utilizado pela transação. Na 2.8.5 publicada, a sequência isolada 10000 → 9000 comprou pelo preço novo; **a mensagem específica de indisponibilidade relatada no servidor do usuário não foi reproduzida somente com essa mudança de preço**. A versão instalada e o erro completo do servidor foram solicitados para investigar essa parte.

A retirada tinha um defeito verificável independente: apagava o arquivo e removia o anúncio da memória antes de entregar. Uma exceção na entrega deixava o anúncio invisível e o bem sem devolução; o snapshot permanecia somente no journal. O teste injeta uma exceção em `PlayerInventory.insertStack` para comprovar esse caminho, sem atribuir essa exceção artificial à causa original do servidor do usuário.

## Comportamento corrigido

- Cada confirmação guarda uma cópia da oferta exibida. Preço/moeda/vendedor são conferidos contra o anúncio atual **antes de iniciar o journal ou acessar a cobrança**.
- Oferta desatualizada informa a mudança de preço e reabre os detalhes atuais. O comprador precisa confirmar novamente; a tentativa antiga não cobra nem bloqueia a venda.
- A retirada decodifica e verifica o bem antes de registrar a intenção. O arquivo e a entrada visível são removidos somente depois da entrega, chamada de salvamento do jogador e registro `RETURNED`.
- Falha durante a entrega preserva o anúncio e o snapshot, com bloqueio contra repetição. Não há tentativa automática de repetir uma entrega incerta.
- Erros de compra/retirada registram a exceção completa no log padrão do servidor, com ID e fase. A exclusão de arquivo agora detecta falhas reais, em vez de ignorar o retorno de `File.delete()`.

## Evidência e limites

Build completo e **53 testes JUnit** aprovados. Teste de integração com Minecraft **1.21.1**, Java **21.0.9**, Fabric Loader **0.17.2**, Fabric API **0.116.6+1.21.1**, Cobblemon **1.8.3** e Impactor **5.3.5**, em Linux.

O mod separado `gts_checks` usa objetos reais do servidor, armazenamento Cobblemon, economia Impactor, persistência de anúncios e callbacks dos botões GooeyLibs. Os dois jogadores são **simulados no servidor**, incluindo a conexão; não houve teste com dois clientes gráficos nem validação em Windows. A exceção de entrega é injetada somente no mod de testes. Ele não está dentro dos JARs distribuídos.

As **77 verificações** cobrem itens e Pokémon, preço 10000 → 9000 e 10000 → 11000, API e botão com preço antigo, preservação de saldos/journal, edição e retirada posteriores, nova confirmação/comissão, repetição de ações, saldo insuficiente, inventário cheio e falha de entrega. Os mesmos testes foram executados no pacote publicado 2.8.5 e no 2.8.6. Os JSONs em [evidence/2.8.6](evidence/2.8.6) registram os resultados; reprovações da versão antiga demonstram as regressões corrigidas.

Esta mudança não comprova a causa completa da mensagem original nem recupera automaticamente perdas anteriores. Os snapshots em `config/gts/transactions/<id>.json` podem permitir uma conferência administrativa de casos antigos, verificando comprador, vendedor, saldos e armazenamento antes de qualquer devolução. Não apagar esses recibos para desbloquear operações. `/gts recuperar confirmar` continua limitado a criações concluídas; não libera entregas/compras incertas.

A chamada existente de salvamento do jogador é mantida. Não se promete atomicidade entre todos os arquivos do Minecraft/Cobblemon, a economia e o GTS em uma queda de energia. Provedores externos não fizeram parte deste ensaio.

## Repetição do teste

1. Compile com JDK 21: `./gradlew build remapIntegrationJar`.
2. Quando o snapshot Maven antigo do Cobblemon estiver indisponível, use os JARs oficiais locais: `./gradlew -PcobblemonJar=/caminho/cobblemon.jar -PimpactorJar=/caminho/impactor.jar build remapIntegrationJar`. Essas opções não incorporam Cobblemon/Impactor no mod.
3. Prepare um servidor Fabric de teste separado, aceitando a EULA localmente, com `server-ip=127.0.0.1`, os mods exigidos, um único GTS e o JAR `*-checks.jar`.
4. Inicie o launcher oficial do Fabric com Java 21 e `-Dgts.integrationTest=true`. O teste executa após 60 ticks e grava `gts-check-results.json` na pasta desse servidor. A propriedade e o endereço loopback são obrigatórios; o teste cria bens e contas fictícios e deve ser usado apenas nesse ambiente descartável.
5. Verifique que todos os registros tenham `passed: true`. O log deve incluir a falha artificial `Injected delivery failure`, seguida de sucesso nas verificações de preservação do anúncio.

A variante Zorykai é gerada por `python3 tools/package_zorykai.py build/libs/GTS-Shirokai-2.8.6.jar build/libs/GTS-Zorykai-2.8.6.jar`. Ela preserva as mesmas duas substituições visuais de moeda e o nome do mod da variante 2.8.5; o script verifica que somente `CurrencyMessages.class` e `fabric.mod.json` diferem do pacote Shirokai.
