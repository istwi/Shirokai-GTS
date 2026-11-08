# Shirokai GTS

Sistema de comércio de Pokémon e itens entre jogadores para **Minecraft 1.21.1 / Fabric / Cobblemon**. Projeto reconstruído a partir do mod Shirokai 2.6.2, com fontes editáveis, compilação completa e menus configuráveis na versão **2.7.0**.

[![Build and test](https://github.com/istwi/Shirokai-GTS/actions/workflows/build.yml/badge.svg)](https://github.com/istwi/Shirokai-GTS/actions/workflows/build.yml)

## O que você pode mudar

Edite `config/gts/layouts.json` no servidor para personalizar **os 11 menus**: GTS principal, itens, Pokémon, busca, gerenciamento, expirados, histórico, detalhes do anúncio, recuperação, seleção de moedas e confirmação da venda.

Você pode mudar títulos, quantidade de linhas, posições, ícones, nomes, descrições, preenchimento e decorações. Execute `/gts reload` e reabra o menu. Configurações inválidas mantêm o último layout válido.

**[Guia dos layouts e exemplos →](docs/LAYOUTS.md)**

O problema do menu vazio ao voltar para trocar a moeda foi corrigido: a seleção conserva um modelo reutilizável, sem reutilizar um construtor já esvaziado.

## Compilar

Instale **JDK 21** e execute na raiz do projeto:

```sh
./gradlew build
```

No Windows:

```bat
gradlew.bat build
```

O Gradle Wrapper baixa o Gradle com verificação SHA-256. O build compila todas as fontes atuais, executa os testes e gera o mod distribuível em **`build/libs/GTS-Shirokai-2.7.0.jar`**. O arquivo com sufixo `-sources.jar` contém as fontes e não deve ser instalado como mod.

A primeira compilação baixa Minecraft, mappings e dependências. O desenvolvimento usa Yarn `1.21.1+build.3` e Fabric Loom; não é necessário aplicar o hotfix antigo no binário para gerar a versão atual.

## Instalar no servidor

Faça backup dos dados e da configuração do GTS. Com o servidor parado, substitua o JAR antigo pelo novo em `mods/`, mantendo apenas uma versão do GTS.

| Dependência | Versão usada no desenvolvimento |
| --- | --- |
| Minecraft | 1.21.1 |
| Java | 21 |
| Fabric Loader | 0.17.2 |
| Fabric API | 0.116.6+1.21.1 |
| Cobblemon | 1.8.3 para 1.21.1 |
| Impactor | 5.3.5+1.21.1 |
| Cloud Fabric, dependência do Impactor | 2.0.0-beta.9 |

O build fixa o snapshot Cobblemon `1.8.3+1.21.1-20261024.164120-29` para evitar mudanças silenciosas na dependência de desenvolvimento. O servidor deve ter Cobblemon, Impactor e suas dependências compatíveis instaladas. GooeyLibs (`fabric-api-repack` 3.1.1-1.21.x) e Fabric Permissions API 0.3.1 já vão incorporados no GTS.

Configurações:

- `config/gts/config.json`: regras econômicas, moedas, preços e limites.
- `config/gts/lang.json`: textos e mensagens originais.
- `config/gts/layouts.json`: aparência e organização dos menus.

Os dados existentes continuam sob as regras do mod recuperado; a atualização não solicita apagar anúncios, histórico nem saldos. Valide uma cópia do servidor antes de substituir sua instalação principal.

## Comandos principais

| Comando | Função |
| --- | --- |
| `/gts` | Abrir os anúncios |
| `/gts pokemon` / `/gts item` | Filtrar por tipo |
| `/gts sell pokemon <slot> <preço>` | Vender Pokémon do time |
| `/gts sell item <preço> <quantidade> [tamanho da pilha]` | Vender o item em mãos |
| `/gts manage` | Gerenciar anúncios próprios |
| `/gts expired` | Recuperar anúncios expirados |
| `/gts history` | Ver histórico |
| `/gts search <busca>` | Buscar anúncios |
| `/gts reload` | Recarregar configuração, idioma e layouts |

O comando de venda abre seleção de moeda e confirmação depois de receber seus argumentos. `/gts sell` sozinho mostra a ajuda, como na versão recuperada.

Permissões de usuário incluem `gts.user.base`, `gts.user.sell`, `gts.user.manage`, `gts.user.expired`, `gts.user.history` e `gts.user.search`. Moedas usam `gts.currency.dollars` e `gts.currency.tokens`. Recarregar exige `gts.admin.reload` (nível de operador 3 por padrão); remover anúncio alheio exige `gts.moderation.remove` (nível 2). Consulte [PermissionProvider](src/main/java/org/pokesplash/gts/permission/PermissionProvider.java) para a lista completa.

## Estrutura do projeto

```text
src/main/java/       Fontes atuais, editáveis e compiladas pelo Gradle
src/main/resources/  Metadados Fabric e mixins
src/test/java/       Testes de layouts, moedas e regressão do menu
src/recovered/       Referência decompilada da versão 2.6.2
patch/               Código do hotfix antigo da versão 2.6.2
tools/               Gerador antigo de hotfix, preservado para reprodução
vendor/              JAR 2.6.2 original fornecido pelo proprietário
docs/                Guias e exemplos de configuração
.github/             Automação de build, testes, artefatos e releases
```

O histórico Git anterior, comentários originais e arquivos de build perdidos não estavam no JAR. O projeto atual reconstrói o processo de compilação e conserva a referência da recuperação. A base é Cobblemon GTS de **bencrow11**, com modificações Shirokai; a licença MIT original está preservada em [LICENSE](LICENSE).

Consulte [CONTRIBUTING.md](CONTRIBUTING.md) para desenvolvimento local e [CHANGELOG.md](CHANGELOG.md) para as mudanças. O GitHub Actions gera artefatos em cada build; uma tag `v<versão>` prepara uma release em rascunho para revisão.
