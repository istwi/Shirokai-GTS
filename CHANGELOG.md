# Changelog

## 2.8.4

- Lore dos Pokémon em blocos com separadores: natureza/habilidade, IVs totais e duas linhas de atributos, movimentos, Ball/Tera/amizade e dados da venda.

- Menus de itens, Pokémon, expirados, histórico e busca com grade de 28 espaços e borda cinza, consistente com o navegador atual.
- Layouts versão 4: migração da grade antiga com backup, preservando grades personalizadas, títulos e botões.
- Tab sugere apenas `/gts`; subcomandos continuam funcionando quando digitados manualmente, com as mesmas permissões.

## 2.8.3

- Retirada informa a causa: anúncio fora da lista, inventário sem espaço ou fase de transação retida.
- `/gts recuperar` fornece diagnóstico de anúncios retidos após reinício; `confirmar` libera apenas criações concluídas compatíveis com o anúncio, com backup e auditoria.
- Compras/devoluções incertas, dados divergentes e provedores externos permanecem protegidos.
- Encerramento limpo do journal registrado diretamente no evento de parada do Fabric.
- Meu GTS inclui ativos e expirados, com `[EXPIRADO]` e retirada pelo mesmo baú à direita.
- A seleção de um anúncio atualiza seu estado ativo/expirado antes de abrir os detalhes; expirados não oferecem edição de preço.
- Testes de reprodução da retenção pós-reinício, recuperação com backup, elegibilidade e proteção de estados incertos.

## 2.8.2

- Tela do anúncio próprio: editar preço com name tag no espaço 11, item/Pokémon no 13 e retirada no 15.
- Retirada pelo vendedor devolve o item/Pokémon pelo fluxo transacional existente e reabre Meu GTS.
- Meu GTS com a mesma borda cinza e grade de 28 espaços do navegador principal, sem a barra antiga.
- Um único botão Voltar no espaço 49; direito e Shift + direito navegam entre páginas de vendas quando necessário.
- Layouts versão 3: migração de Meu GTS com backup e configuração separada `own_listing`, preservando a tela de compra.

## 2.8.1

- Painéis de vidro de fundos, decorações e botões dos menus padronizados para vidro cinza, inclusive em configurações antigas.
- Nomes, descrições, componentes e callbacks preservados na troca de ícone.
- Ícones de produtos e prévias do item negociado mantêm a cor original.

## 2.8.0

- Novo navegador de seis linhas, bordas e 28 anúncios por página, com os sete controles solicitados.
- Filtros por categoria com clique esquerdo/direito, incluindo Shiny, HA, Legendary, Ultra Beast, caixas e itens especiais.
- Ordenação por publicação, expiração, nome e preço; inversão e padrão persistente por jogador.
- Regras de caixas/itens especiais em `config/gts/browser.json`.
- Livro e `/gts sell` abrem um assistente com preço/quantidade em mensagem privada, seguido do fluxo de moeda/confirmar existente.
- Meu GTS com edição de preço pelo vendedor, confirmação e revalidação de estado/permissões/regras econômicas.
- Layouts versão 2 com backup e migração da barra anterior.
- Novos testes de filtros, ordenação, preferências, entrada de venda e migração.

## 2.7.0

- Projeto Fabric reconstruído com fontes editáveis em nomes Yarn, Gradle Wrapper e Java 21.
- Layouts configuráveis para os 11 menus existentes em `config/gts/layouts.json`.
- Títulos, linhas, slots, ícones, nomes, descrições, preenchimento e decorações configuráveis.
- Paginação respeita os slots de conteúdo configurados.
- `/gts reload` recarrega layouts; configuração inválida mantém o último layout válido e informa o erro.
- Correção do menu vazio ao voltar da confirmação para trocar moeda preservada no código-fonte.
- Testes automatizados de configuração e da sessão de seleção de moeda.
- Workflows de build/teste, artefatos e publicação de releases.

## 2.6.2 — recuperação

- Fontes e recursos recuperados do JAR fornecido pelo proprietário.
- Hotfix localizado e reproduzível para o retorno à seleção de moedas.
