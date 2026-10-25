# Análise antes da produção N06
Data: 20/09/2026. Base íntegra: 0.2.0-alpha.4, SHA 3adfd2cc102eed60666d5594afc0a375e2a1fc7047ed409c7afc81c5969c190e.
Pedido: resize/maximize do splash, aviso do BogoSorter, SFX e telas de conexão/desconexão.
A ampliação é explícita neste pedido; não altera inventário, HUD, autenticação nem lógica de rede.

## 1. Evidência da falha de resize
No LWJGL instalado (2.9.1), Display.update(boolean) verifica DisplayImplementation.wasResized e atualiza width/height/window_resized.
No Angelica 2.2.8 instalado, GLStateManager.updateDisplay fora da main thread usa swapBuffers e processMessages, sem aquela atualização de geometria.
No N04, nosso desenho consulta Display.getWidth/getHeight; glViewport recebe dimensões antigas quando o tamanho visível muda.
Reprodução n06baseline: duas maximizações durante o boot, 854x480 -> 1920x1001; fração branca 0.7867 nas duas capturas.
A captura inspecionada reproduz o retângulo antigo no canto inferior esquerdo apresentado pelo usuário.
Arquivos: lwjgl-display.txt (update), glsm-bytecode.txt (updateDisplay) e qa-n02/evidence/n06baseline/startup-resize/.
Correção de escopo: manter a única apresentação na posição original protegida pelo semáforo do Forge, executando Display.update original por ponte no bootstrap já excluído da transformação.
Não adicionar swap extra, thread nova, manipulação global de contexto nem resize de Minecraft na thread do splash.
O cenário tem cover próprio; os controles continuam contain, sem cortar áreas de clique para preencher aspect ratios.
Aceitação: maximize/restore durante carga, manter maximizado até o menu, conferir imagem e Display/Minecraft/FBO.

## 2. Aviso do BogoSorter
A classe instalada WarningScreen recebe uma lista pública messages. O gatilho é InventoryTweaks presente junto com BogoSorter.
ClientEventHandler.onGuiOpen é LOWEST; o once flag wasOpened não é uma correção da incompatibilidade.
Apenas o texto conhecido da advertência InventoryTweaks é movido para log; mensagens desconhecidas continuam visíveis.
Um gate tardio em loadComplete resolve a ordem relativa com handlers comuns, sem repostagem recursiva de GuiOpenEvent.
Nenhum mod de inventário foi removido, desativado ou reconfigurado; não alegar conflito resolvido.
Aceitação: aviso conhecido suprimido, desconhecido preservado, retorno por botão original, F8 sem reinserção do conhecido.

## 3. Áudio
Usar somente SoundHandler/PositionedSound existentes: categoria MASTER, volume adicional de UI, atenuação NONE e sem loop.
O motor de som não está pronto na janela gráfica inicial: nenhum áudio ou dispositivo paralelo será inicializado nessa fase.
Cinco sinais originais por síntese determinística: focus, select, back, enter e connect. Sem arquivos de terceiros.
OGGs decodificados e analisados: taxa, canais, finitude, pico, RMS. Aprovação artística auditiva continua com o usuário.
Gate monotônico impede rajadas por teclado/mouse e áudio de ação rejeitada. Não há fila de replay tardio.
No máximo três vozes rastreadas; mute e disable respeitados. Asset ausente falha silenciosamente e registra diagnóstico.
Aceitação: resolução dos cinco recursos no jogo, SoundHandler reportando reprodução, MASTER=0 impedindo submissão.

## 4. Telas secundárias
Primeiro recorte: classes exatas GuiConnecting e GuiDisconnected, somente fora de mundo.
DrawScreenEvent.Pre substitui somente o desenho após sucesso; as instâncias nativas continuam responsáveis por input, botão, canal e handshake.
O erro original e as linhas do servidor são preservados; erro excepcionalmente longo retorna ao desenho nativo em vez de ser cortado.
Nenhuma aprovação implícita para redesenho de Options, lista de mundos, mods, HUD ou inventário.
Aceitação: captura real das duas telas, cancelamento nativo, retorno ao menu, mensagem de teste reconhecível e canal fechado.

## 5. Gates anteriores a runtime
48 cenários de core/17489 asserções; 15 checks de conexão; 140 checks de debounce/cover/política de aviso.
Contagens incluem repetições matemáticas, não são 140 classes diferentes de falhas.
Build real contra APIs locais, JAR sem QA/fontes binárias/dependências do codec.
O primeiro candidato alpha.1 corrigiu o resize visual mas falhou no boot com IllegalAccessError: listener LateBogoNoticeGate não era public.
Candidato reprovado e fonte/JAR preservados; alpha.2 torna o subscriber público e adiciona auditoria de visibilidade dos quatro subscribers no bytecode.
A atualização do driver de testes e do candidato foi feita só em QA. A instância diária já contém N04 instalado pelo usuário e não foi sobrescrita.
A cópia QA recebeu o AkashicIndex existente para aproximar a composição atual; seu hash consta no recibo de implantação.
Não promover candidato por compilação, prints de splash isolados ou testes anteriores. Conclusão depende do restante da execução e inspeção.
