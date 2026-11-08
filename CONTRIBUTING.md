# Contribuindo

Use JDK 21 e o Gradle Wrapper incluído. Compile com `./gradlew build` (`gradlew.bat build` no Windows). As fontes de desenvolvimento estão em `src/main/java`, os recursos em `src/main/resources` e os testes em `src/test/java`.

As fontes em `src/recovered` são apenas a referência decompilada original. O gerador de hotfix em `tools/build.py` pertence à versão 2.6.2 e não compila as mudanças da versão 2.7.0. Para desenvolver a versão atual, use Gradle.

Para mudanças nos layouts, atualize os padrões de `LayoutConfig`, os testes e `docs/LAYOUTS.md`; gere o exemplo com `./gradlew exportLayoutDefaults`. Não coloque arquivos de servidor, logs, mundos, credenciais ou configurações pessoais no Git.

Para desenvolvimento local, use `./gradlew runServer`. O servidor fica em `run/server`. Leia e configure a EULA do Minecraft nesse diretório antes da inicialização completa. Os dados de teste ficam fora do Git. O build baixa as dependências de desenvolvimento, inclusive Cobblemon, Impactor e Cloud; elas não são todas incorporadas no JAR distribuído.

Uma mudança em layout deve preservar os callbacks de navegação, as validações de venda e a confirmação única. Verifique moedas, confirmação, voltar, cancelar, paginação e permissões no servidor de teste antes de usar em produção.

Ao criar uma tag `v<mod_version>`, o workflow Release recompila, testa e publica os JARs e checksums. Também é possível executá-lo manualmente pela aba Actions para enviar os arquivos à release da versão atual.
