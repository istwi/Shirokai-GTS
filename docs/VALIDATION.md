# Validação 2.8.1

Compilação completa aprovada; 29 testes passaram, incluindo a conversão de painéis de botões para cinza, a preservação de nomes/callbacks e a proteção da cor de produtos e prévias.

---

# Validação 2.8.0

Verificada em 9 de novembro de 2026 com Minecraft 1.21.1 / Fabric Loader 0.17.2, Cobblemon 1.8.3 e Impactor 5.3.5:

- Compilação integral, testes e remapeamento pelo Gradle Wrapper com JDK 21.
- Migração real de layouts versão 1, com criação de `layouts.json.v1.backup` e nova barra principal.
- Menu principal de seis linhas, bordas e sete controles nas posições 46–52.
- Venda de um diamante pelo livro: escolha do item, resposta privada `150 1`, escolha da moeda, confirmação, retirada de uma unidade e persistência do anúncio a $150.
- Meu GTS: abertura do anúncio, edição para $200, confirmação e verificação do arquivo persistido, conservando a quantidade em inventário.
- 31 anúncios ativos: primeira página com 28 anúncios, segunda com 3, setas em ambos os sentidos.
- Filtro de Pokémon excluiu os anúncios de itens; clique direito retornou ao filtro de itens.
- Preço crescente mostrou terra de $100 antes do diamante de $200; a inversão trouxe o diamante primeiro. Shift + clique salvou PRICE/reversed=true por UUID, e a conexão seguinte manteve a preferência.
- Categorias Caixas e Itens especiais foram verificadas com IDs configurados: terra e diamante foram separados corretamente.
- Assistente de Pokémon abriu a seleção do time e anunciou um Eevee Shiny com habilidade oculta, confirmado pelo registro persistido do Cobblemon.
- Respostas privadas de preço não apareceram no chat público/log de mensagens do servidor.
- Servidor de teste encerrado normalmente com `/stop`.

Os testes automatizados cobrem categorias/flags, ordenação, expiração sem prazo, preferências por jogador, regras de itens, validação da entrada, migração e a regressão da troca de moeda. Os testes em rede usam um cliente de protocolo local, sem renderização gráfica. Esse cliente não decodifica todos os componentes dos ícones de Pokémon; a criação foi confirmada pela ação no servidor e pelo anúncio persistido. Aparência no cliente gráfico, todas as espécies e integração com os mods específicos de caixas do servidor do proprietário ainda precisam ser conferidas na instalação dele. As regras de IDs reais desses mods devem ser preenchidas em `browser.json`.

A edição direta com provedores externos de ListingAPI é explicitamente desabilitada por falta de contrato transacional nessa interface.

---

# Validação da reconstrução 2.7.0

Validado em 8 de novembro de 2026:

- Compilação integral das fontes, processamento dos mixins, remapeamento Fabric e geração dos JARs com Gradle 8.14.3 / JDK 21.
- Execução do Gradle Wrapper incluído, com verificação do checksum da distribuição.
- 15 testes JUnit executados: 9 de configuração/layouts, 4 da sessão de moeda e 2 de regressão/aparência do menu. Sem falhas ou testes ignorados.
- Geração do exemplo completo com `./gradlew exportLayoutDefaults`.
- Inicialização de um servidor Fabric 0.17.2 / Minecraft 1.21.1 com Cobblemon `1.8.3+1.21.1-20261024.164120-29` e Impactor `5.3.5+1.21.1`, até a mensagem de prontidão.

## Interações verificadas no servidor

Foi usada uma conexão local do protocolo Minecraft com uma conta de teste e o arquivo `docs/examples/layouts-custom.json`. A verificação recebeu os pacotes reais de abertura e conteúdo dos inventários, sem renderização gráfica:

1. `/gts` abriu o menu com o título personalizado e os controles de navegação.
2. `/gts sell item 100 1`, com um diamante em mãos, abriu o seletor de quatro linhas com a moeda disponível, a decoração e o botão de cancelar na posição 31.
3. Selecionar a moeda abriu a confirmação personalizada: item na posição 13, confirmar em 20, cancelar em 24 e trocar moeda em 31.
4. Clicar em trocar moeda reabriu o seletor preenchido, conservando o botão da moeda e o cancelamento.
5. Cancelar encerrou o menu e conservou os oito diamantes da conta de teste.
6. Alterar o título no arquivo e executar `/gts reload` mudou o título na próxima abertura, sem reiniciar.
7. Configurar `rows: 0` e recarregar informou o erro e conservou o último layout válido, que abriu novamente.
8. O exemplo válido foi restaurado, recarregado, e o servidor foi encerrado normalmente com `/stop`.

As compras entre jogadores, vendas de Pokémon, múltiplas moedas reais e aparência em cliente gráfico não foram verificadas nesta sessão. O ambiente local tinha a moeda padrão do Impactor; uma entrada configurada para tokens não cria essa moeda no serviço econômico. Antes de atualizar um servidor ativo, valide esses fluxos em uma cópia dos seus dados e com as moedas/dependências que você usa.

O Cobblemon registrou avisos próprios sobre integrações opcionais e data fixers; eles não impediram a inicialização. A consulta das chaves públicas do Minecraft falhou no servidor local sem autenticação, mas a conexão de teste foi feita apenas em loopback e em modo offline. Isso não valida login Microsoft em um servidor público.
