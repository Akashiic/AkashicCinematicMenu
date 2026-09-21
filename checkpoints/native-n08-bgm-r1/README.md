# N08 BGM R1 — 0.5.0-alpha.1
Candidata separada da N07 e da N08 parcial interrompida. Não é liberação geral.

## Arquivo fornecido
O usuário JÁ anexou BGM.mp3 ao chat. A mensagem anterior dizendo que faltava o arquivo estava incorreta.
MP3 original: SHA256 1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408,6475232bytes.
OGG preparado no ambiente da conversa: SHA256 66db58689c1745385109a2ee9e5b5757f2d57f3f692d20ed13e9a7d084999db3,3941511bytes.
44.1kHz,estéreo,161.8808s; ganho constante-2dB; sem mudança de velocidade/reverb. Fade não gravado no OGG.
O anexo existe no chat; não foi encontrada cópia do OGG em Downloads no PC. Isso é uma fronteira de transferência, não ausência do material fornecido.

## Compilado e exercitado
JAR-base: dist/AkashicCinematicMenu-0.5.0-alpha.1.jar
SHA256 c9248a3060139e5334facd6186e9c09f6826af650508e6c20d1438bc52975b0d;73classes Java8; APIs reais locais.
ThemeTransition altera o fundo do AetherLoadingScreen e LoadingScreenRenderer após a terra e antes dos textos/progresso/dica.
O hook do GuiDownloadTerrain também está compilado, mas não foi exercitado nessa rodada; não alteramos seus ticks/keepalive.
Run qa-n02/evidence/n08bgm1 executou os renderizadores reais Aether e vanilla por comandos de QA, sem abrir mundo ou conectar.
15 verificações passaram, capturas inspecionadas, cliente encerrou exit0 e contador GL monitorado permaneceu0.

## BGM implementada
Um único stream pelo SoundHandler, categoria MUSIC, respeitando MUSIC/MASTER. Entrada2.5s,saída0.8s,volume0.40,loop configurável.
Configuração separada: config/akashiccinematic-bgm.cfg. Contexto é o menu custom e submenus conhecidos, nunca gameplay.
Fade é temporizado, com reversão contínua, controle de duplicidade, retry limitado e recuperação por reload.
A música vanilla só é suprimida quando a nossa reprodução foi confirmada; falha/arquivo ausente mantém fallback.
Núcleo:1557 verificações; lifecycle18cenários/124asserções. Testes de cabeçalho70casos rodaram no ambiente da conversa com o OGG real.

## Limite explícito
O JAR-base testado no PC NÃO contém a faixa. O teste real confirmou ausência segura:0starts,0bloqueios vanilla e estadoMISSING.
O teste de mute sem voz só prova o estadoMUTED; não comprova encerramento de uma reprodução audível.
A reprodução da gravação exata, o fade audível, loop/reload no jogo e avaliação artística aguardam transferência do OGG e teste adicional.
O kit de áudio entregue no chat prepara uma nova cópia do JAR com a faixa, sem substituir o original nem instalar no pack diário.
