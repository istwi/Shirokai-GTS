# Novo navegador GTS — 2.8.0

O `/gts` usa um inventário de seis linhas, borda de vidro azul-claro e **28 anúncios por página**, nas quatro linhas centrais. A disposição segue a referência enviada pelo proprietário.

## Barra inferior

| Espaço | Ícone | Ação |
| --- | --- | --- |
| 46 | Relógio esquerdo | Alternar categoria de anúncios |
| 47 | Seta | Página anterior |
| 48 | Funil | Alternar ordenação |
| 49 | Livro e pena | Iniciar uma venda |
| 50 | Balde de água | Atualizar os anúncios, preservando filtro, ordenação e página quando possível |
| 51 | Seta | Próxima página |
| 52 | Relógio direito | Meu GTS: anúncios próprios, edição de preço e acesso aos expirados |

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

Relógio direito → seu anúncio → **Editar preço**. Informe o novo preço em mensagem privada e confira os valores anterior/novo antes de confirmar.

A edição preserva moeda, item/Pokémon, quantidade, ID e expiração. Somente o vendedor com permissão de venda e da moeda pode editar. Preços mínimos/máximos e regras de moeda são revalidados; um anúncio vendido, expirado, bloqueado ou alterado desde a abertura da edição não pode ser confirmado com os dados antigos. Falhas de persistência restauram o preço anterior em memória. Não há movimento de saldo nessa operação.

Para provedores externos registrados na `ListingAPI`, a edição direta é desabilitada, pois essa interface não oferece um contrato de atualização transacional. O restante do navegador continua disponível.

## Migração e personalização

`layouts.json` passa para **versão 2**. Ao carregar uma configuração da versão anterior, o mod guarda uma cópia em **`layouts.json.v1.backup`**, adapta a antiga barra principal e adiciona os novos controles. O título e o preenchimento personalizados do menu principal são preservados; sua disposição antiga permanece no backup. Os outros menus conservam suas configurações e recebem os campos novos que faltam.

Os novos controles `category_filter`, `sort_filter`, `create`, `refresh` e `manage`, além das setas, permanecem configuráveis. As novas telas são `sell_create`, `sell_party` e `price_confirm`; em `listing`, existe `edit_price`. Em `manage`, existem `back` e `create`. Configurações personalizadas antigas podem precisar ajustar posições que coincidam com um novo controle; erros de sobreposição são informados ao recarregar.

Consulte [LAYOUTS.md](LAYOUTS.md) e [o exemplo completo](examples/layouts.json). O visual dos itens em si depende das texturas do cliente; a disposição e as funções são fornecidas pelo mod.
