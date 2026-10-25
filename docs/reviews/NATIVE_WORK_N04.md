# Trabalho em andamento N04 — primeira janela e foco
Referência preservada: N03 / SHA-256 154c95045e70b15798988358ff4a38db75c57523b4946e4e4b786a0414b104ea.
O usuário pediu testes autônomos até encontrar o ajuste adequado e informou ausência do visual customizado no início da janela.
Cópia de uso diário novamente inspecionada: nenhum CinematicMenu instalado. As alterações continuam limitadas à homologação.

## Diagnóstico reproduzido
Run05 é a baseline N03, janela do próprio processo 14536, com observador de primeiros quadros.
Existem quadros pretos antes de O Despertar. O transformer N03 atende SplashProgress$3, não o intervalo anterior ao splash do Forge.
A captura de criação da janela pode conter pixels residuais do compositor/da janela anterior. Não usar tais imagens como conteúdo do jogo nem compartilhar como preview.
Arquivos em qa-n02/evidence/run05/early-window; tempos em first-window-observations.json.

## Candidato separado
candidates/native-n04 / 0.2.0-alpha.4; compilado, ainda aguardando teste real ao escrever este registro.
Novo hook depois de OpenGlHelper.initializeTextures, portanto depois da inicialização GL/Angelica e antes de beginMinecraftLoading.
Desenha um quadro estático aprovado com mensagem indeterminada e devolve controle ao jogo. Sem thread nova, percentual fictício, sleep ou espera artificial.
Conserva o splash do Forge para progresso subsequente. Não pode desenhar antes de existir um contexto gráfico utilizável.
52 ativos de arte inalterados, correção de cancelamento N03 preservada.

## Foco
Novo ensaio mede ShowWindowAsync, estado iconic/foreground do Windows e telemetria LWJGL separadamente.
O objetivo é distinguir bloqueio do comando de automação de atraso no cliente; não presume causa nem aprovação.
run05 fecha por comando normal após os ciclos de foco.
Não reexecutar preparação/migração se pasta/recibo existir. Conferir processo e checkpoints antes de seguir.

## Encerramento desta fase
O texto acima é o registro inicial de trabalho; a situação consolidada está em NATIVE_REVIEW_N04.md.
N04 testada em runs06/07 e recuperação sob recursos ausentes em run08; todos os clientes encerrados com exit=0.
Cópia de QA restaurada ao JAR íntegro alpha.4. CURRENT_STATUS.json aponta ao checkpoint de recuperação.
Abertura imediata sem qualquer quadro preto continua pendente; experimento N05 não integrado e fora do pacote ativo.
