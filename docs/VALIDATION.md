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
