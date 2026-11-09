# Novo navegador GTS — 2.8.0

O `/gts` usa um inventário de seis linhas, borda de vidro cinza e **28 anúncios por página**, nas quatro linhas centrais. A disposição segue a referência enviada pelo proprietário.

## Barra inferior

| Espaço | Ícone | Ação |
| --- | --- | --- |
| 46 | Relógio esquerdo | Alternar categoria de anúncios |
| 47 | Seta | Página anterior |
| 48 | Funil | Alternar ordenação |
| 49 | Livro e pena | Iniciar uma venda |
| 50 | Balde de água | Atualizar os anúncios, preservando filtro, ordenação e página quando possível |
| 51 | Seta | Próxima página |
| 52 | Relógio direito | Meu GTS: anúncios próprios, edição de preço e retirada |

No relógio esquerdo: **clique esquerdo avança**, **direito retrocede**. A descrição mostra todas as categorias e destaca a atual em verde: Todos, Meus anúncios, Itens, Pokémon, Shiny, HA, Legendary, Ultra Beast, Caixas, Itens especiais, PokéDollars e PokéTokens. HA é verificado com a propriedade oficial de habilidade oculta do Cobblemon; as classificações Legendary e Ultra Beast usam os dados do próprio Pokémon.

No funil: **clique esquerdo alterna** Publicação / Expiração / Nome / Preço; **direito inverte** o sentido; **Shift + esquerdo salva** a ordenação e seu sentido como padrão pessoal. As preferências ficam em `config/gts/browser-preferences.json`, por UUID, e sobrevivem a reinícios. O filtro de categoria e a página duram a conexão atual.

Ordenar por preço agrupa as moedas antes de comparar seus valores; não existe uma taxa de conversão entre tokens e dólares. Anúncios sem expiração ficam no fim em ambos os sentidos. Anúncios antigos usam a data disponível a partir da expiração/duração como aproximação; antigos anúncios sem duração conhecida ficam por último na ordenação de publicação mais recente. Novos anúncios guardam `createdAt` explicitamente.

## Caixas e itens especiais

Edite `config/gts/browser.json` e execute `/gts reload`. Exemplo:

```json
{
  "crates": {
    "itemIds": ["minecraft:tripwire_hook"],
    "itemTags": [],
    "nameContains": ["caixa", "crate", "chave"]
  },
  "specialItems": {
    "itemIds": ["minecraft:nether_star"],
    "itemTags": [],
    "nameContains": ["voucher", "especial"]
  }
}
```

Uma regra corresponde se **qualquer** ID, tag ou trecho do nome combinar. Use os IDs reais dos seus mods; os itens vanilla acima são apenas exemplos válidos para testar. Tags devem ser escritas sem `#`, como `seumod:chaves`. A comparação de nome ignora maiúsculas/minúsculas. Para usar somente IDs/tags exatos, deixe `nameContains: []`. Os padrões procuram palavras como caixa, crate, chave, key, especial, voucher e passe; ajuste-os para evitar incluir itens que você não deseja.

Os filtros não criam itens, não registram novas moedas e não dependem de um mod específico de caixas. Um filtro sem anúncios correspondentes exibe uma página vazia com os controles acessíveis.

## Criar uma venda pelo livro

1. Segure o item desejado ou tenha o Pokémon no time.
2. Clique no livro ou use `/gts sell`.
3. Escolha **Vender item em mãos** ou **Vender Pokémon**; no segundo caso, escolha o Pokémon do time.
4. Informe o preço no chat privado. Para item, use `preço [quantidade]`, como `150 3`; quantidade omitida significa **1**. Para Pokémon, informe somente o preço. Aceita ponto ou vírgula decimal, sem separador de milhar, e até duas casas decimais.
5. Escolha a moeda e confirme a venda no fluxo original do mod.

A resposta é interceptada e não é enviada ao chat público. Digite **`cancelar`**, sem barra, para voltar ao GTS. A entrada expira em dois minutos e é descartada ao desconectar. Nada é retirado do jogador ao escolher o item ou informar o preço: a retirada só ocorre na confirmação original da venda. Trocar o item em mãos ou o Pokémon selecionado invalida o rascunho. Os comandos completos de venda continuam disponíveis.

## Editar o preço

Relógio direito → seu anúncio ativo → **Editar preço**. Informe o novo preço em mensagem privada e confira os valores anterior/novo antes de confirmar.

A edição preserva moeda, item/Pokémon, quantidade, ID e expiração. Somente o vendedor com permissão de venda e da moeda pode editar. Preços mínimos/máximos e regras de moeda são revalidados; um anúncio vendido, expirado, bloqueado ou alterado desde a abertura da edição não pode ser confirmado com os dados antigos. Falhas de persistência restauram o preço anterior em memória. Não há movimento de saldo nessa operação.

Para provedores externos registrados na `ListingAPI`, a edição direta é desabilitada, pois essa interface não oferece um contrato de atualização transacional. O restante do navegador continua disponível.

## Migração e personalização

`layouts.json` usa **versão 3**. Configurações das versões 1 e 2 recebem backup `layouts.json.v1.backup` ou `layouts.json.v2.backup`. A antiga barra principal da versão 1 é adaptada; a antiga disposição de Meu GTS é substituída pela grade com apenas o botão Voltar. Títulos explicitamente personalizados são preservados. Os demais menus recebem os campos novos que faltam.

Os controles do navegador principal permanecem configuráveis. As telas de venda são `sell_create`, `sell_party` e `price_confirm`. A tela do vendedor é `own_listing`, com `edit_price`, `asset`, `withdraw` e `back`; ela é separada de `listing`, usada na compra. Em `manage`, existe apenas `back`.

Consulte [LAYOUTS.md](LAYOUTS.md) e [o exemplo completo](examples/layouts.json). O visual dos itens em si depende das texturas do cliente; a disposição e as funções são fornecidas pelo mod.

### Vidros na versão 2.8.1

Painéis de vidro usados como fundo, decoração, botões ou ícones de moedas são exibidos como `minecraft:gray_stained_glass_pane`, inclusive quando uma configuração antiga indica outra cor. Produtos anunciados e prévias do item negociado preservam o item original.


### Meu GTS na versão 2.8.2

A tela tem seis linhas, borda cinza e a mesma grade de 28 anúncios do GTS principal. A antiga barra de botões foi removida. Existe uma única seta no espaço **49**: clique esquerdo volta ao GTS; direito avança uma página das suas vendas e Shift + direito volta uma página, quando houver mais de 28 anúncios. O título informa a página atual.

Ao abrir seu anúncio, a **name tag** para editar preço fica no espaço **11**, acima da posição antiga. O item/Pokémon fica no **13** e o botão de **Retirar do GTS** no **15**, à direita. A seta inferior no **22** volta para Meu GTS. Retirar devolve o item/Pokémon pelo mesmo fluxo transacional já usado pelo mod e atualiza a lista de suas vendas.

`layouts.json` agora usa **versão 3**. Configurações das versões 1 e 2 são migradas com backup `layouts.json.v1.backup` ou `layouts.json.v2.backup`. A antiga disposição de `manage` é substituída para retirar os botões antigos; um título explicitamente personalizado é preservado. A tela de compra mantém suas posições anteriores, pois as ações do vendedor agora estão na configuração separada `own_listing`.

Anúncios expirados continuam acessíveis por `/gts expired`; o livro para criar vendas permanece no menu principal.


### Expirados e recuperação na versão 2.8.3

Meu GTS reúne ativos e expirados, identificando estes com `[EXPIRADO]` e uma descrição vermelha. Abra um expirado e use o baú à direita para receber o item/Pokémon. Não é necessário usar a tela antiga de expirados, embora `/gts expired` continue disponível. O mesmo botão de voltar continua sendo o único controle da barra inferior.

Se um anúncio antigo ficou retido após reiniciar/trocar o mod, a mensagem informa o ID e orienta a administração. `/gts recuperar` consulta a situação; `/gts recuperar confirmar` só libera criações concluídas compatíveis e salva backup dos recibos alterados. Consulte [RECOVERY.md](RECOVERY.md) antes de confirmar em dados que tenham sido restaurados de backups.
