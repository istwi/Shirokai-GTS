# Changelog

## 2.7.0

- Projeto Fabric reconstruído com fontes editáveis em nomes Yarn, Gradle Wrapper e Java 21.
- Layouts configuráveis para os 11 menus existentes em `config/gts/layouts.json`.
- Títulos, linhas, slots, ícones, nomes, descrições, preenchimento e decorações configuráveis.
- Paginação respeita os slots de conteúdo configurados.
- `/gts reload` recarrega layouts; configuração inválida mantém o último layout válido e informa o erro.
- Correção do menu vazio ao voltar da confirmação para trocar moeda preservada no código-fonte.
- Testes automatizados de configuração e da sessão de seleção de moeda.
- Workflows de build/teste, artefatos e releases em rascunho.

## 2.6.2 — recuperação

- Fontes e recursos recuperados do JAR fornecido pelo proprietário.
- Hotfix localizado e reproduzível para o retorno à seleção de moedas.
