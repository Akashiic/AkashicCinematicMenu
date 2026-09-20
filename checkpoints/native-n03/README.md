# Akashic Cinematic Menu — N03 / 0.2.0-alpha.3
Candidato executado na cópia isolada do modpack; não é liberação geral.
Leia CHECKPOINT_N03.json e ../../NATIVE_REVIEW_N03.md antes de retomar.

## O que mudou
Correção do cancelamento de conexão com publicação tardia de NetworkManager.
O canal criado após cancelar é fechado pelo método assíncrono nativo; a vigilância aguarda worker terminado e canal fechado.
No máximo uma tentativa pendente do nosso menu; não substitui handshake ou autenticação do FML.
Mantém a correção N02 para renderização sob Angelica. Arte R04/S01, quatro botões e destino br2.xmxcloud.net:10511 preservados.

## Evidência
Núcleo: 48 cenários / 17489 asserções; lifecycle: 15 verificações. Build com APIs reais, Java 8.
Modpack real: 35 verificações game-thread, 7 de nova conexão/cancelamento, 16 de mensagens à janela passaram na rodada final.
A primeira tentativa de minimizar teve timeout; repetição restaurou corretamente, mas latência de 8.501s continua pendente.
JAR: dist/AkashicCinematicMenu-0.2.0-alpha.3.jar
SHA-256: 154c95045e70b15798988358ff4a38db75c57523b4946e4e4b786a0414b104ea
Capturas/logs ficam em ../../qa-n02/evidence/run04; auditoria em evidence/run04-runtime-audit.json.
O native-build.json descreve somente o instante da compilação; o checkpoint registra o teste posterior no Minecraft.

## Limites
Não instalar automaticamente no modpack de uso diário. Sem login autenticado, mundo, Android ou benchmark de FPS.
Estátua 2D, blur pré-renderizado. O pacote de QA nunca deve acompanhar o mod distribuído.
Processo de teste encerrado com exit=0; original e checkpoints anteriores preservados.

## Build e testes
python tools/test_core.py
java -ea -cp build/core-tests br.com.redeakashic.cinematic.core.ConnectionWatchTests
python tools/build_native.py
Os scripts de migração copiados nas tools são históricos e não devem ser reexecutados.
