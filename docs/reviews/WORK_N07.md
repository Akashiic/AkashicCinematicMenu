# Trabalho em andamento — N07 / 0.4.0-alpha.2
Não confundir esta candidata com a N04 certificada anteriormente nem com o experimento bloqueado N05.
Pedido mais recente: eliminar corredor lateral, recuperar tamanho após pausas, melhorar nitidez e adotar paleta do mobile.
Base de desenvolvimento encontrada: native-n06 / 0.3.0-alpha.3, incluindo resize/splash, SFX, Bogo específico e conexão/desconexão.

## Implementado nesta retomada
Compositor responsivo por pixels do framebuffer; remove a segunda imagem contain que criava margem de cerca de70px em1920x1001.
Texto independente com nove atlas por tamanho; ornamentos e progresso em geometria. Fonte de cenário1207x830 não artificialmente ampliada como detalhe novo.
Paleta: #090D14, #171C25, #E8E4D8, #C4AA7C, #ACB2BB, #DCEBFA; verde não é acento dominante.
Recursos de cena reutilizados em resize; superfícies minimizadas/inválidas são ignoradas. Não promete redesenho durante parada de todas as threads.
Primeira alpha foi reprovada pela leitura pequena em854x480. Alpha.2 corrige mínimos e elide textos longos.
Refinamento opcional de overscan/densidade teve gravação bloqueada; não foi aplicado nem contornado.

## Identidade e testes
JAR final em candidates/native-n07/dist/AkashicCinematicMenu-0.4.0-alpha.2.jar
SHA-256: 226b5c13ed0052a07e379feeab5745841c06f890346fb8cdb499f5ae03f3e8a5
Core48 cenários/17489 asserções, geometria1785 verificações, política conexão15 passaram; quatro listeners públicos auditados.
N07run1 executou alpha.1 e fechou exit0; teste de foreground não conseguiu provar foco e foi reprovado, sem atribuir culpa ao usuário.
N07final executa alpha.2: processo6136; receitas em qa-n02/evidence/n07final.
Scripts de regressão e recuperação estão ativos para essa execução; revalidar process.json antes de novas operações.
QA usa somente a cópia isolada. Não tocar no modpack diário nem promover antes do resultado e inspeção.
A aprovação visual final é do usuário; esta alteração não herda aprovação cromática do verde anterior.

## Checkpoint intermediário após teste principal
N07final/alpha.2 chegou ao menu e passou na suíte básica: Bogo conhecido/aviso desconhecido, SFX+mute, conexão/desconexão e retorno.
A geometria real foi validada também em1920x1080,2560x1440,1280x800 e854x480 com PNGs completos.
A primeira suíte de pausa falhou na aquisição de device context GDI DURANTE a pausa; não comprova falha do renderer. Esse resultado foi preservado.
O cliente6136 foi encerrado normalmente, exit0. Não reabrir o mesmo run.
Nova rodada dirigida n07recovery usa o MESMO JAR226b5c13ed0052a07e379feeab5745841c06f890346fb8cdb499f5ae03f3e8a5.
Processo Minecraft13900; launcher DC13008; driver focused_stall_recovery.py DC16892. Espera boot, pausa800/6500ms, verifica recomposição após retomar e pede encerramento.
O driver não tenta capturar GDI durante a pausa: lê a captura completa do próprio jogo após a retomada.
Ao retomar, conferir process.json e focused-recovery.json antes de nova abertura ou alteração. A aprovação ainda depende desse ensaio e da inspeção posterior.

## Correção confirmada no driver de recuperação
O bytecode LWJGL2.9.1 mostrou que Display.setDisplayMode destrói/recria a janela nativa.
O helper guardava HWND anterior ao resize; isso explica o device context inválido e a tentativa de maximizar um handle antigo.
Não é uma correção nova do renderer: o JAR permanece226b5c13ed0052a07e379feeab5745841c06f890346fb8cdb499f5ae03f3e8a5.
A rodada n07recovery foi encerrada exit0 após reprovação do teste. Falha e logs preservados.
Rodada atual: n07recovery2, processo Minecraft1364, launcher DC16800, driver DC2576.
focused_stall_recovery_verified.py resolve novamente a janela do PID após cada resize e valida IsWindow e o retorno de ShowWindowAsync.
Só o resultado dessa rodada pode aprovar as pausas. Não contar a troca de metodologia como otimização de produto.
A auditoria e o empacotamento foram vinculados a n07recovery2, mantendo as falhas anteriores no checkpoint final.

## Última candidata — alpha.3 e fechamento da inspeção
A repetição n07recovery2 confirmou o diagnóstico de HWND obsoleto: handle anterior inválido após setDisplayMode; handle novo foi usado e a recuperação800/6500ms passou em1920x1001. Processo1364 fechou exit0.
A inspeção1:1 encontrou uma borda decorativa atravessando a última linha do rodapé compacto. Alpha.3 altera só o fundo dessa moldura de95.5% para97.5%, sem mover textos/hitboxes.
Candidata final atual:0.4.0-alpha.3, SHA14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac,7238260bytes,56classes.
Core48/17489, geometria1785, conexão15, UI140, glyphs2016 e4listeners públicos passaram de novo após essa mudança.
N07release encerrou cedo com exit1, sem stacktrace fatal ou hs_err encontrado; causa NÃO ESTABELECIDA, não atribuída ao usuário nem apagada.
Reexecução controlada n07release2 não manipula janela durante o boot; mesma candidata. Processo Minecraft6036, launcher DC14368, sequência de suítes DC10652.
run_safe_start_n07_suites.py executa básico, resoluções e recuperação com HWND revalidado; esta última pede fechamento normal.
Auditoria FINAL correta: qa-n02/tools/audit_n07_final_version.py. Ela usa n07release2 e SHAalpha.3, não o script anterior de alpha.2.
Ainda não promover CURRENT_STATUS sem resultados, inspeção do rodapé corrigido e process.json final. O encerramento anterior não explicado precisa permanecer nos riscos documentados.

## Encerramento confirmado — candidata final
N07release2/alpha.3 concluiu as três suítes e encerrou exit=0. Foram 34 verificações básicas, 10 de resolução e 20 de recuperação.
Os PNGs de 854x480, conexão/desconexão e recuperações após 800/6500ms foram inspecionados; rodapé corrigido e superfície 1920x1001 recomposta.
Auditoria concluída em CHECKPOINT_N07.json; NATIVE_REVIEW_N07.md e CURRENT_STATUS.json apontam à versão 0.4.0-alpha.3.
A recompilação reproduziu o SHA14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac.
Pacote de engenharia criado e verificado: AkashicCinematicMenu-N07-Fidelity-engineering.zip,16026855bytes,SHA4af3409245f0da443212ac105321400625e016fd08056c0447cb48ed69ee4664.
Uma cópia independente do mesmo JAR foi encontrada na pasta diária na leitura final. Este fluxo não a criou/removeu/substituiu; não inferir autoria ou momento da cópia.
O exit1 anterior de n07release continua sem causa estabelecida. Não existe aprovação geral; os ensaios finais não apagam o histórico.
A aprovação das novas cores, enquadramento e SFX permanece com o usuário. N05 parcial bloqueada continua excluída.
