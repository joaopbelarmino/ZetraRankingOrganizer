# ZetraRankingOrganizer

Plugin para Paper 1.21.4 e Java 21 que sincroniza posições de rankings do PlaceholderAPI com grupos do LuckPerms. Anteriormente chamado **TopMagnatasPlugin**.

## Funcionamento

O plugin consulta os placeholders definidos em `cargos.yml` a cada intervalo de `config.yml`. Quando o líder muda, remove o grupo do jogador anterior e atribui ao novo jogador. Os titulares ficam registrados em `data.yml`.

O arquivo padrão configura os dez maiores doadores mensais da CentralCart. Os grupos do LuckPerms e os placeholders precisam existir no servidor. LuckPerms e PlaceholderAPI são necessários para o funcionamento; os rankings da CentralCart também exigem a integração correspondente.

## Compilação

Com JDK 21 e Maven instalados:

```sh
mvn clean package
```

O resultado será `target/ZetraRankingOrganizer-1.0.0.jar`.

## Instalação e migração

1. Pare o servidor e faça backup da pasta `plugins/TopMagnatasPlugin`.
2. Remova o JAR antigo e coloque o novo JAR em `plugins`.
3. Renomeie a pasta `plugins/TopMagnatasPlugin` para `plugins/ZetraRankingOrganizer`, preservando `config.yml`, `cargos.yml` e especialmente `data.yml`.
4. Inicie o servidor e confira as atualizações dos grupos no console.

Não mantenha os dois plugins ativos ao mesmo tempo.

## Origem desta versão

Código recuperado por descompilação do arquivo `TopMagnatasPlugin-1.0.0 (1).jar`, pois os fontes originais não estavam disponíveis no projeto. Foram ajustados o nome, o pacote Java e os tipos necessários para recompilar. A lógica de sincronização original foi preservada. Compilação local validada; funcionamento em um servidor Minecraft ainda precisa ser validado.
