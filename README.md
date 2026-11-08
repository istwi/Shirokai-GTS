# Shirokai GTS — recuperação 2.6.2

Código e recursos recuperados do arquivo `GTS-Shirokai-2.6.2-fabric.jar` fornecido pelo proprietário do projeto. Mod de vendas entre jogadores para Cobblemon, baseado em Cobblemon GTS de bencrow11. Licença MIT original preservada em [LICENSE](LICENSE).

## Compatibilidade declarada no mod

| Componente | Versão |
| --- | --- |
| Minecraft | 1.21.1 |
| Java | 21 ou superior |
| Fabric Loader | 0.16.0 ou superior |
| Cobblemon | 1.8.3 ou superior |
| Impactor | 5.3.5 |

Esses requisitos vêm de `fabric.mod.json`; não equivalem a uma matriz de versões testadas. O JAR original também inclui fabric-api-repack 3.1.1-1.21.x e fabric-permissions-api 0.3.1.

## Correção: voltar para trocar moeda

Na venda (`/gts sell`), o menu de confirmação chamava novamente `template.build()` ao clicar em **trocar moeda**. A versão de GooeyLibs incluída esvazia o construtor em cada chamada a `build()`, portanto a segunda abertura recebia um inventário vazio. O caminho de retorno após uma confirmação rejeitada usava o mesmo construtor.

A correção substitui apenas a criação do construtor de seleção por `ReusableSelectionTemplate`: guarda o primeiro modelo e cria cópias dos slots a cada abertura. Os botões e os callbacks da sessão permanecem os mesmos. O construtor do menu de confirmação e a biblioteca compartilhada permanecem intactos.

## Gerar o JAR corrigido

Pré-requisitos: Java 21+, Python 3 e curl. Na raiz do checkout:

```sh
python3 tools/build.py
```

O script baixa ECJ, ASM e Guava do Maven Central, verifica SHA-256, compila a correção, executa o teste de regressão e gera:

```text
dist/GTS-Shirokai-2.6.2-fabric-currency-back-fix.jar
```

Este é um hotfix reproduzível **a partir do JAR original**, que está em `vendor/`. Não é uma recompilação integral das fontes recuperadas. O teste usa a implementação real de ChestTemplate extraída da biblioteca incluída; tipos mínimos de Minecraft são usados somente no teste, sem iniciar o jogo, e não entram no mod gerado. Ele reproduz o reset original e verifica dez reaberturas com duas moedas, botão de cancelar e slots independentes. O script também verifica que somente `CurrencySelection.class` mudou e uma classe auxiliar foi adicionada; os demais arquivos são preservados byte a byte no conteúdo.

## Instalação e teste no servidor

1. Faça backup do mod atual, das configurações e dos dados de vendas.
2. Pare o servidor e substitua o JAR antigo pelo corrigido em `mods/`. Mantenha apenas uma cópia do mod GTS.
3. Inicie o servidor com suas dependências habituais.
4. Use uma conta de teste: `/gts sell` → escolha a moeda → **trocar moeda**. Confira se as moedas e o botão de cancelar reaparecem; repita a troca.
5. Selecione outra moeda e confirme uma única venda; confira moeda, preço e anúncio. Confira também cancelar e uma venda de item/Pokémon conforme os fluxos usados no servidor.

O teste dentro do Minecraft ainda é necessário: não foi executado um servidor completo nem validada uma transação real nesta recuperação.

## Organização e limites da recuperação

- `src/recovered/java/`: fontes de referência decompiladas com Vineflower 1.11.1, preservando o comportamento original anterior ao hotfix.
- `src/recovered/resources/`: metadados e configuração dos mixins recuperados.
- `patch/src/`: código novo e compilável da correção.
- `tools/`: geração e aplicação da alteração localizada no bytecode.
- `tests/`: teste de regressão do modelo de seleção.
- `vendor/`: binário original para reprodução e comparação.

Os nomes `net.minecraft.class_*` são nomes intermediary presentes no artefato Fabric distribuído. O histórico Git, comentários originais, arquivos Gradle, versões exatas de todas as dependências de desenvolvimento e testes originais não podem ser restaurados a partir desse JAR. As fontes decompiladas podem exigir ajustes antes de uma compilação integral; não são apresentadas como projeto Gradle pronto. Alterar essas fontes de referência não altera automaticamente o hotfix: o script compila somente `patch/src/` e o aplicador em `tools/`.

Para retomar desenvolvimento completo, o próximo trabalho é reconstruir um projeto Fabric/Loom com os mappings e as dependências compatíveis, corrigir os artefatos de decompilação e validar o mod em servidor. A versão interna permanece 2.6.2 para evitar mudanças incidentais de compatibilidade; o sufixo no nome do arquivo identifica o hotfix.

SHA-256 do original: `3f379ae44b1cf5881031540be1a7db0d1f540ab83ff6f074b0f228c8d058885c`.
