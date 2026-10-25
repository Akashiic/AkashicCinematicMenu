# N04 — inicialização antecipada e recuperação verificada
Data: 20/09/2026. Autorrevisão técnica do assistente; aprovação visual final permanece do usuário.
Checkpoint: ACM-N04-RECOVERY-CERTIFIED-2026-09-20.
Candidato ativo: candidates/native-n04, versão 0.2.0-alpha.4.
SHA-256: 3adfd2cc102eed60666d5594afc0a375e2a1fc7047ed409c7afc81c5969c190e.

## 1. O que explica o começo sem nossa tela
A N03 atendia o splash de carregamento do Forge, mas não o intervalo entre a criação da janela e o início desse splash.
A N04 adiciona uma apresentação depois de OpenGlHelper.initializeTextures e antes de beginMinecraftLoading.
O quadro usa a arte aprovada e a mensagem Preparando o ambiente, sem inventar total ou percentual.
Quando o Forge começa a publicar as etapas, O Despertar passa a mostrar o progresso real.
Não foram alterados o fluxo de autenticação, as rotas aprovadas nem os 52 recursos visuais da N03.
Também foi verificado que a instalação de uso diário NÃO contém o candidato. Os testes aconteceram em qa-n02/instance.

## 2. Medição da primeira arte visível
N03/run05: primeira captura customizada inspecionada aos 7.479s após observar a janela visível.
N04/run07: quadro Preparando o ambiente inspecionado aos 2.432s.
Diferença observada: 5.047s. Uma execução selecionada por versão, com amostragem configurada de 250ms mais custo de captura.
Não é benchmark estatístico, aceleração garantida nem redução equivalente no tempo de carregar o modpack.
Ainda há um intervalo inicial de aproximadamente 2.4s sem arte na observação da N04. Primeiro pixel sem tela preta NÃO está aprovado.
A correção atual começa após a preparação da camada gráfica. A antecipação anterior a essa fase continua sem implementação aprovada.
Capturas inspecionadas: run07/early-window/frame-009.jpg (preparação) e frame-028.jpg (progresso Forge).

## 3. Foco e minimização
Nos runs06/07, seis ciclos usando ShowWindowAsync e SetForegroundWindow diretamente no HWND correto restauraram o menu.
Inatividade observada: 101–406ms. Restauração: 303–409ms. Polling de 100ms e telemetria do jogo em torno de 300ms.
Não são medições de latência por quadro nem números de FPS.
O teste anterior com processo auxiliar não assegurava o mesmo foreground do sistema. A demora de 8–12s não se repetiu no ensaio direto.
O código de foco do menu NÃO foi alterado; não atribuir uma correção de produto a essa mudança de metodologia.

## 4. Testes normais já concluídos
Runs06/07: 35 verificações de regressão e 11 verificações de startup/captura, além dos seis ciclos de foco.
Essas contagens incluem precondições e verificações de evidência; não representam classes independentes de defeitos.
Menu, Singleplayer, Opções, confirmação de saída, redimensionamento, recarga e cancelamento da conexão foram exercitados no run06.
As capturas foram conferidas nas dimensões reais; um teste de captura inicialmente leu um PNG antes de terminar a gravação.
O teste foi corrigido para aguardar CAPTURE_WRITTEN e decodificar o PNG inteiro. Falha inicial preservada, sem tolerância a PNG truncado.
As duas execuções normais encerraram com exit=0 e sem as falhas de renderização monitoradas nesse escopo.

## 5. Teste adicional de falha de recursos — run08
Uma cópia exclusiva de QA omitiu oito PNGs: boot.png e panel.png dos quatro perfis. Todos os demais bytes das entradas permaneceram idênticos.
O candidato oficial em candidates/native-n04/dist NÃO foi alterado.
Doze verificações passaram: falha inicial capturada, fallback do splash, falha do menu registrada uma vez, saída da tela customizada e continuidade do render loop.
A observação passou pela WarningScreen do BogoSorter e terminou no GuiMainMenuVanilla do DummyCore; a captura final foi inspecionada.
Não houve mundo ou conexão ao servidor nesse ensaio. Contador monitorado de erros GL permaneceu zero.
Isso testa imagens ausentes, não toda categoria de falha de driver, falta de memória ou encerramento abrupto da JVM.
Processo de teste 14736 encerrado normalmente, exit=0. O JAR íntegro foi restaurado na cópia de QA e seu SHA-256 revalidado.
Uma nova abertura automática pós-restauração não foi executada: a gravação do orquestrador foi bloqueada pela ferramenta.
O rollback e os hashes foram verificados; não se atribuiu aprovação de runtime a uma nova partida que não ocorreu.

## 6. Integridade e entrega
Recompilação reproduziu exatamente o JAR testado: 5774983 bytes, 43 classes Java 8.
Regressão reexecutada: 48 cenários e 17489 asserções; política de cancelamento: 15 verificações.
Arte aprovada preservada. Sem classes do driver de QA, bibliotecas do Minecraft ou arquivos de fonte TTF/OTF/WOFF no JAR.
898 arquivos do manifesto original conferidos: zero alterados ou ausentes; nenhum JAR instalado no modpack de uso diário.
A variante sem imagens só fica em build/evidências, identificada QA-ONLY. NÃO redistribuí-la.
O experimento native-n05 permanece parcial, não compilado nem integrado após bloqueio da ferramenta. Não incluído nesta entrega.
Arte do anjo continua 2D; blur pré-renderizado. Sem homologação de login autenticado, jogabilidade, Android ou outros drivers.

## 7. Parecer
APROVADO nos cenários dirigidos de boot, menu, foco e recuperação por recurso ausente que foram realmente executados.
NÃO aprovado como desaparecimento total do intervalo inicial, benchmark universal ou liberação geral do modpack.

## Evidências e continuidade
- candidates/native-n04/CHECKPOINT_N04.json: runs06/07, tempos, escopo e limitações.
- candidates/native-n04/CHECKPOINT_N04_RECOVERY.json: consolidação e teste de recuperação.
- candidates/native-n04/evidence/certification-build.json: rebuild e ausência de código experimental/QA/fontes.
- qa-n02/evidence/run08/fault-acceptance.json: doze verificações da falha deliberada.
- qa-n02/evidence/run08/fallback-review.jpg: captura real do retorno ao menu do pack.
- qa-n02/evidence/fault-run08/restoration.json: confirmação de restauração do JAR íntegro.
- CURRENT_STATUS.json: ponte para o checkpoint vigente, sem sobrescrever o checkpoint histórico.
Próximo requisito visual: tratar o intervalo residual anterior ao hook gráfico sem alterar a composição aprovada ou promover o experimento bloqueado.
Qualquer piloto na instalação diária deve ser uma ação explícita, com backup e rollback, não consequência automática de testes em QA.
