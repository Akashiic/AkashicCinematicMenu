# Parecer N11 — GUI Wave 2 / 0.7.0-alpha.4

Candidata produzida e homologada em 21/09/2026 na cópia isolada do modpack completo.
A aprovação visual final permanece do usuário; este documento separa execução técnica de aprovação artística.

## Artefato testado
- JAR: AkashicCinematicMenu-0.7.0-alpha.4.jar
- SHA-256: 7dbf5177c4e26edea3c0d5caceafac6d4ec013eee6d543d2dbe80066d4c0fb4a
- 16.508.324 bytes; 81 classes Java 8; compilado contra os artefatos reais locais.
- Rebuild final reproduziu o mesmo SHA-256.

## O que foi tematizado
Options, Singleplayer/Select World, Create World, Language, Resource Packs, Controls, Video, Sound,
Chat, Snooper, Flat World, Flat Presets e confirmações vanilla.
No pack real, Video é redirecionado para Reese Sodium e também foi tematizado.
Create World pode passar primeiro pelo aviso do RTG ou ir direto ao GuiCreateWorld; as duas rotas foram tratadas.

A implementação troca apresentação compartilhada — background, superfície das listas e skin dos botões —
sem reimplementar as ações vanilla, sliders, seleção, scroll, criação de mundos ou callbacks.
GuiSlot deixa de usar o options_background de terra nessas telas auditadas.

## Sair do Minecraft
O menu principal não cria mais GuiYesNo para QUIT.
O bytecode contém exatamente uma chamada de shutdown para essa rota e nenhum callback de confirmação de saída.

No runtime, o primeiro ensaio tentou disparar QUIT com a janela sem foco; o input foi aceito,
mas a própria política do menu cancelou a transição por segurança. Essa falha de precondição foi preservada.
Na repetição, a janela QA foi colocada em foreground real, o processo encerrou com exit=0
e nenhum GuiYesNo apareceu após o comando.

Confirmações destrutivas não foram removidas:
o bytecode vanilla de Select World ainda contém a confirmação de delete,
e um GuiYesNo inofensivo foi aberto, tematizado e teve seu callback nativo exercitado no runtime.

## Música e segurança de mundo
A gravação da BGM, MenuBgm, BgmPlayback e MenuSoundGate permanecem byte-idênticos à N10.
A política de frontend recebeu apenas a expansão necessária para manter a música pelos submenus tematizados.
Em todas as telas da matriz, worldPresent=false; nenhum mundo foi criado, carregado ou apagado.
Nenhuma conexão com servidor foi iniciada nessa homologação e nenhuma credencial real foi usada.

## Gates independentes
- Núcleo anterior: 48 cenários / 17.489 asserções.
- SecondaryScreenPolicy: 58 verificações.
- Parallax/CTA N10: 4.807 verificações.
- Geometria: 1.785 verificações.
- Política UI: 140 verificações.
- Música/fade: 1.557 verificações.
- Playback: 18 cenários / 124 asserções.
- Classificação sonora: 32 verificações.
- Transformer no JAR SRG real: 13 verificações; dois usos da textura de terra do GuiSlot substituídos.
- Probe de QUIT: 5 verificações; uma chamada de shutdown; delete-world confirmation preservada.
- Renderizador secundário OpenGL: 14 verificações na Intel UHD Graphics.

A primeira comparação de determinismo do probe gráfico foi inválida porque comparava dimensões diferentes.
Ela foi preservada e repetida corretamente, sem mudança do produto.
Outro gate de atlas exigia arbitrariamente mais de 20 cores; o atlas autorado tem 19.
O gate foi corrigido para medir opacidade, variação real e estrutura do atlas.

## Runtime no modpack completo
O cliente completo iniciou com a alpha.4 na instância QA e terminou normalmente com exit=0.
A suíte registrou 83 checks funcionais passados antes do gate de saída.
Os dois registros de falha restantes na matriz V4 são a mesma precondição de janela inativa,
posteriormente resolvida pelo teste dedicado de foreground.
Zero marcadores monitorados de erro GL, fallback do tema secundário ou rejeição dos hooks foram encontrados.

## Telas capturadas em 1920×1001
Main menu, Options, Singleplayer, Create World, Language, Resource Packs, Controls, Reese Video,
Sound, Chat, Snooper, Flat World, Flat Presets, confirmação vanilla e menu antes do encerramento.

A métrica de pixels marrons usada pelo driver é apenas uma sentinela grossa contra o fundo de terra ocupando a tela;
ela não classifica cores artísticas. Todas as capturas ficaram abaixo do limite conservador de 35%.
O probe de bytecode é a evidência direta de que as duas referências de options_background no GuiSlot foram substituídas.

## Limites
- Apenas a família auditada recebe o tema; GUIs desconhecidas de outros mods permanecem intactas.
- A lógica interna de Resource Packs, mundos, controles e sliders é vanilla/modded original.
- A N11 não foi copiada para a instalação diária por este trabalho.
- A pasta diária observada no final continha N10/0.6.0-alpha.3; não se atribui autoria dessa instalação.
- A aprovação estética da Wave 2 continua pendente do usuário.
- Não declarar liberação geral até essa aprovação.

## Parecer
Build, hooks SRG, renderizador independente, matriz de telas e encerramento direto passaram no escopo descrito.
Status técnico: FULL_PACK_DIRECTED_TESTS_PASSED__USER_VISUAL_APPROVAL_PENDING.
