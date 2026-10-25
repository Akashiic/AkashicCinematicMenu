# N03 — testes autônomos no modpack real — 20/09/2026
Checkpoint: ACM-N03-AUTONOMOUS-QA-2026-09-20.
Autorrevisão do assistente. A aprovação visual R04/S01 permanece do usuário.

## Candidato efetivamente executado
candidates/native-n03/dist/AkashicCinematicMenu-0.2.0-alpha.3.jar
SHA-256: 154c95045e70b15798988358ff4a38db75c57523b4946e4e4b786a0414b104ea
5771076 bytes, 41 classes Java 8. 52 ativos visuais idênticos à N02.
Instância: qa-n02/instance; runtime Java legado do Technic; Intel UHD Graphics / GL 4.6 core / Angelica.
Identidade de teste ACMenuQA, sem credenciais pessoais. Nenhum mundo local criado.
O cliente completo com os mods foi iniciado; não foi apenas o probe gráfico do N01.

## Falhas encontradas e tratadas
N01 falhava no contexto core do Angelica. N02 já existente retirou a exclusão do pacote render para permitir emulação gráfica.
Reauditoria do run02 invalidou capturas HD (ainda eram 854x480) e a suposta comprovação do fechamento da conexão.
O driver de teste V3 agora atualiza/observa Display, Minecraft e framebuffer; verifica o tamanho real dos PNGs.
Run03 reproduziu a falha real: cancel flag=true, retorno ao menu, manager publicado depois e canal aberto por mais de 10s.
A N03 mantém uma vigilância limitada à conexão iniciada pelo nosso menu, fecha o canal publicado após cancelamento e libera somente após worker encerrado/canal fechado.
Sem novo protocolo, novas threads de conexão, alteração de autenticação ou tentativas paralelas: o fluxo continua do FML.
A decisão de lifecycle ganhou 15 testes independentes de Minecraft; regressão de 48 cenários/17489 asserções passou.

## Rodada final run04
Processo Minecraft 10644, início 1789923297.532755, fim 1789923892.104769, exit=0.
Chegada observada ao menu: 339.84 segundos nesta execução; não é benchmark comparativo de boot.
35/35 verificações do driver game-thread, 7/7 de reconexão/cancelamento e 16/16 da última tentativa de mensagens da janela passaram.
Os números incluem precondições e capturas; não são classes independentes de bugs nem garantia de ausência de falhas.
Splash: rendered=true, finished=true, failed=false. Captura mostra estágio real do Forge.
Singleplayer, Opções, confirmação/negação de saída, foco Home/End/wrap, F7 e cancelamento de transição exercitados.
Três resoluções conferidas no framebuffer e no PNG: 1366x768, 1024x768, 854x480; redimensionamento real da janela também testado.
Cancelamento antes da publicação do canal: defeito reproduzido no run03 e não repetido na N03; canal fechado e worker liberado, seguidos por 10s de observação.
Nova tentativa após cleanup: abriu normalmente; cancelamento depois da publicação também fechou canal, com mais 5s de observação.
Amostragem da telemetria ~300ms: valor calculado 0ms significa fechado na primeira amostra publicada, não latência física zero.
Recarga real dos recursos retornou ao menu; contador de erros GL permaneceu 0 no escopo monitorado.
Click na janela em Singleplayer, Escape, Home, Tab, F7 e F8 via mensagens do Windows exercitados; não são teclas físicas humanas.
F8 saiu do nosso menu para a WarningScreen do BogoSorter. Não afirmar retorno direto ao DummyCore.

## Pendência que impede aprovação geral
Primeira tentativa de minimizar excedeu 12s para observar active=false; preservada em window-input-attempt1.json.
Com precondição de foco renovada, repetição observou inactive após 8.501s e restaurou normalmente.
A recuperação funcional passou, mas a demora não está aprovada. Origem no mod, stack gráfico, SO ou automação ainda não isolada.
Não ocultar essa pendência apenas porque a última suíte encerrou sem falhas.
Login autenticado, mundo/jogabilidade, stress prolongado, Android e outros drivers não testados.
O log completo do pack contém avisos/erros de outros componentes; glErrors=0 não significa log global sem erros.
Anjo 2D e blur pré-renderizado permanecem; não houve implementação de modelo 3D ou blur dinâmico nesta revisão.
A comparação antes/depois de desempenho e a distribuição geral continuam não aprovadas.

## Preservação e evidências
898 arquivos originais do manifesto conferidos: zero alterados, zero ausentes. Não equivale a varredura de todo o computador.
Nenhum CinematicMenu instalado na instância jogável. Checkpoint histórico e JARs anteriores mantêm os hashes registrados.
qa-n02/evidence/run04/final-audit.json: identidade, escopo, testes e evidências.
qa-n02/evidence/run04/acceptance-v3.json, retry-connection.json e window-input.json: resultados finais.
qa-n02/evidence/run04/review-v3/: capturas inspecionadas; screenshots/: PNGs originais.
qa-n02/evidence/AUDIT_RUN02.md e run03/: reprovações preservadas.
candidates/native-n03/CHECKPOINT_N03.json: continuidade; native-build.json é metadado de compilação anterior ao teste real.

## Parecer e próximo gate
Testes funcionais executados aprovados; latência de foco pendente; NÃO liberar como produção geral.
Próximo trabalho: isolar a demora de foco/minimização, avaliar retorno F8 através dos avisos externos e completar testes de entrada/saída de sessão.
Não executar de novo scripts de migração de fonte nem trocar arquivos do modpack de uso diário automaticamente.
