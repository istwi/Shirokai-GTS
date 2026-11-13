# Personalizar os menus

O arquivo é **`config/gts/layouts.json`**, na pasta do servidor. Ele é criado na primeira inicialização. Você pode editá-lo e executar `/gts reload`; feche e reabra os menus para ver o resultado. Um menu já aberto não é reconstruído no meio de uma transação.

Comece pelo [exemplo completo dos padrões](examples/layouts.json) ou pelo [exemplo personalizado](examples/layouts-custom.json). Campos omitidos usam os padrões; não é necessário copiar todos os menus para alterar apenas um. Faça uma cópia do seu arquivo antes de editar.

## Menus disponíveis

| Chave | Tela |
| --- | --- |
| `gts` | Anúncios do `/gts` |
| `items` | Anúncios de itens e ordenação |
| `pokemon` | Anúncios de Pokémon e ordenação |
| `manage` | Seus anúncios |
| `expired` | Anúncios expirados |
| `history` | Histórico |
| `search` | Resultados de busca |
| `listing` | Compra e remoção administrativa de anúncio alheio |
| `own_listing` | Editar preço e retirar seu próprio anúncio |
| `expired_listing` | Recuperação de anúncio expirado |
| `sell_currency` | Escolha/troca da moeda na venda |
| `sell_confirm` | Confirmação da venda |
| `sell_create` | Escolha de venda de item/Pokémon |
| `sell_party` | Escolha de Pokémon do time |
| `price_confirm` | Confirmação da edição de preço |

O fluxo de venda preservado do mod é `/gts sell pokemon <slot> <preço>` ou `/gts sell item <preço> <quantidade> [tamanho da pilha]`, seguido dos menus de moeda e confirmação. Na versão 2.8.0, `/gts sell` sozinho abre o assistente de venda, que solicita o preço em mensagem privada. Veja [BROWSER.md](BROWSER.md).

## Campos de cada menu

| Campo | Significado |
| --- | --- |
| `rows` | De 1 a 6 linhas, com 9 espaços por linha |
| `title` | Título da tela; `null` usa o título padrão da tela; o navegador usa `GTS | {current}/{total}` |
| `contentSlots` | Espaços para anúncios ou moedas; não podem ocupar botões |
| `filler` | Aparência do preenchimento do fundo |
| `buttons` | Botões existentes, identificados pela função, com `slot` e `appearance` |
| `decorations` | Lista de itens decorativos, cada um com `slot` e `appearance` |

Cada `appearance` e o próprio `filler` aceitam:

```json
{
  "item": "minecraft:emerald",
  "name": "<green>Confirmar publicação</green>",
  "lore": ["<gray>Confira a moeda e o preço antes de confirmar.</gray>"]
}
```

`item` é o identificador de um item registrado. Nomes e descrições aceitam MiniMessage e os códigos de cor legados do mod. `null` mantém o valor original; `lore: []` remove a descrição. Se você omitir `item`, a aparência mantém o item atual e seus dados. Trocar o item substitui a aparência, sem alterar o item/Pokémon negociado.

Títulos são textos enviados à biblioteca de menus; não interpretam os placeholders econômicos de nomes/descrições. No título de `search`, `%search%` é substituído pela busca. Menus paginados aceitam `{current}` e `{total}` nos títulos.

## Como contar os espaços

Os espaços começam em **0**, da esquerda para a direita e de cima para baixo:

```text
 0  1  2  3  4  5  6  7  8
 9 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
```

Uma tela com 3 linhas termina em 26. Com 4, termina em 35. Ao diminuir `rows`, ajuste também todos os espaços de conteúdo e de botões para caberem na tela. Os espaços não podem se repetir entre conteúdo, botões e decorações.

Em menus paginados, os anúncios são colocados nos espaços de conteúdo em ordem crescente. A quantidade de páginas acompanha o número desses espaços. `contentSlots` não pode ficar vazio. O menu de moedas preserva até sete opções do mod original: mantenha exatamente sete espaços em `sell_currency.contentSlots`; a ordem dessa lista corresponde à ordem das moedas disponíveis.

## Exemplo: mudar a confirmação da venda

Este arquivo parcial é válido e preserva os outros menus:

```json
{
  "version": 4,
  "menus": {
    "sell_confirm": {
      "rows": 4,
      "title": "Confirme seu anúncio",
      "filler": {"item": "minecraft:gray_stained_glass_pane", "name": " "},
      "buttons": {
        "confirm": {
          "slot": 20,
          "appearance": {
            "item": "minecraft:emerald",
            "name": "<green>Publicar anúncio</green>",
            "lore": ["<gray>Clique para confirmar.</gray>"]
          }
        },
        "asset": {"slot": 13},
        "cancel": {"slot": 24},
        "change_currency": {
          "slot": 31,
          "appearance": {"item": "minecraft:arrow", "name": "<yellow>Trocar moeda</yellow>"}
        }
      }
    }
  }
}
```

As funções dos botões permanecem as mesmas. O botão `primary` em `listing` representa comprar. O vendedor usa a tela separada `own_listing`, com `edit_price` no espaço 11, `asset` no 13, `withdraw` no 15 e `back` no 22. `admin_remove` só tem ação de remoção quando a permissão original autoriza.

## Ícones e nomes das moedas

Os ícones, nomes, cores e símbolos das moedas já pertencem a **`config/gts/config.json`**, na seção `multiCurrency.currencies`. Exemplo de uma entrada:

```json
"impactor:tokens": {
  "enabled": true,
  "displayName": "PokéTokens",
  "symbol": "✦",
  "color": "light_purple",
  "icon": "minecraft:amethyst_shard"
}
```

Mantenha a estrutura restante de `config.json`. Isso altera a apresentação de uma moeda existente; criar uma entrada não registra uma nova moeda no Impactor. Os identificadores devem existir no serviço econômico. As posições e o fundo do seletor pertencem a `layouts.json`. Os textos padrão e as mensagens de preço/seleção pertencem a `lang.json`.

## Erros ao recarregar

JSON inválido, linhas fora de 1–6, espaços repetidos ou fora da tela, identificadores desconhecidos de menus/botões e itens inexistentes são rejeitados. O `/gts reload` informa o erro; o servidor conserva os últimos layouts válidos. Na primeira inicialização, conserva os padrões. O arquivo com erro não é sobrescrito.

Depois de uma alteração, confira voltar/trocar moeda, cancelar, confirmar, navegar entre páginas e as permissões de vendedor/administrador. As configurações só mudam a interface: preços, saldos, permissões e validações de transação continuam sob as regras do mod.

Os menus de itens, Pokémon, expirados, histórico e busca usam 28 espaços de conteúdo com borda de vidro cinza. Arquivos das versões 1–3 são migrados com backup `layouts.json.v<versão>.backup`: a grade antiga de 45 espaços é atualizada, preservando títulos, botões e grades personalizadas.
