# Anúncios retidos após reinício

Na versão 2.8.3, a retirada informa o motivo da recusa: anúncio fora da lista atual, inventário sem espaço ou registro de transação retido. Um servidor encerrado sem gravar a marca de parada limpa pode deixar criações antigas em `RECOVERY_REQUIRED`, mesmo quando o anúncio permanece salvo. A troca de JARs não remove essa retenção já gravada.

## Diagnosticar e recuperar criações concluídas

Após instalar a versão atual, um administrador pode executar:

```text
/gts recuperar
```

Esse comando **só consulta** os registros. Ele informa a quantidade de anúncios retidos e quais criações concluídas correspondem ao anúncio armazenado. A permissão é `gts.admin.recover`, com nível de operador 4 por padrão. No console, use o comando sem a barra inicial.

Depois de conferir o diagnóstico e a consistência dos dados do servidor:

```text
/gts recuperar confirmar
```

A confirmação só libera registros que:

- Estão em `RECOVERY_REQUIRED` e eram `CREATED`, com identificação de criação pelo próprio vendedor e sem valor destinado a outro jogador.
- Correspondem ao ID, dono, moeda, tipo e **conteúdo completo do item/Pokémon** do anúncio salvo, ativo ou expirado.
- Não existem simultaneamente nas listas ativa e expirada.
- Continuam iguais ao registro consultado quando a recuperação é gravada.

Preço e prazo podem ter sido editados legitimamente depois da criação; isso não altera a identidade do item armazenado. Para Pokémon de um vendedor online, também é verificado se o mesmo UUID já está no time ou PC.

Cada recibo alterado recebe backup em **`config/gts/transactions/recovery-backups/`**, e o novo registro inclui o administrador e a data da recuperação. O comando não entrega itens, não movimenta saldos e não muda a propriedade dos anúncios: depois dele, o dono abre **Meu GTS** e usa a retirada normal, que continua validando o anúncio e a entrega.

## Casos que exigem análise

Compras, devoluções e operações incompletas/incertas não são liberadas por esse comando. Arquivos ilegíveis, dados divergentes e provedores externos de `ListingAPI` também não são liberados. O diagnóstico mostra o ID e a fase para análise administrativa.

A marca `CREATED` pressupõe os dados de inventário e do anúncio salvos pelo fluxo de criação. Se inventários, anúncios e recibos foram restaurados de backups diferentes, é necessário reconciliar esses dados antes de confirmar. Não apague a pasta de transações nem mude fases manualmente para contornar uma retenção: isso pode permitir uma segunda entrega.

Encerre atualizações do servidor com `/stop`. A versão atual registra a gravação de encerramento limpo diretamente no evento de parada do Fabric; falhas ou encerramentos forçados ainda exigem diagnóstico.

## Anúncios expirados

**Meu GTS** reúne anúncios ativos e expirados na mesma grade. Os expirados aparecem com `[EXPIRADO]` e uma descrição vermelha. Clique no anúncio e use o baú à direita para retirar. Expirados não oferecem edição de preço; o botão de voltar e a navegação continuam na mesma tela. O comando antigo `/gts expired` também permanece disponível.
