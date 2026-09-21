# N10 — Ceremonial SFX, Layered Parallax e Jogar Carmesim
Candidata 0.6.0-alpha.3. Base preservada: N09/0.5.2-alpha.1, cuja BGM foi confirmada funcional pelo usuário.
Ler CHECKPOINT_N10.json e docs/PARECER_N10.md para separar o que foi testado do que permanece pendente.

## Artefato
`dist/AkashicCinematicMenu-0.6.0-alpha.3.jar`
SHA-256: `a15e18a206e89aa7736d71cae22813ecb4be0e7cb0dffd75d4c10e0939785859`
A faixa Fluxxwave, seu evento e o código de BGM não foram alterados. A candidata não foi instalada na instância diária.

## Implementação
O botão Jogar usa material carmesim/branco, gradiente lento, brilho de borda, passagem de luz suave e resposta de pressão.
A área de clique e o fluxo de conexão continuam independentes da animação; o destino permanece br2.xmxcloud.net:10511.
O cenário foi separado em céu, arquitetura e anjo/halo, com velocidades distintas e a inércia já existente no menu.
Uma máscara de ocultação da arquitetura preserva os pilares à frente dos joelhos; compartilha a profundidade da arquitetura.
O painel desfocado acompanha o enquadramento das camadas. Não há novo modelo3D nem blur de framebuffer em tempo real.
O menu carrega as camadas sob demanda. Startup e telas secundárias mantêm o cenário estático anterior.

## Som
Seis arquivos originais atendem cinco eventos: foco com duas variações, seleção, retorno, entrada e conexão.
Síntese de coro sem palavras, ressonância modal metálica, ar filtrado e caudas estéreo; não é gravação de um coral ou canto gregoriano real.
O efeito de sala é calculado ao construir os arquivos; não há DSP pesado por frame no cliente.
A troca de vozes usa liberação suave de90ms, prioriza ações e limita a seis vozes próprias; o debounce original foi preservado.
Volume e mute Master continuam sendo respeitados. Não se alterou o controle de música nem o áudio de gameplay.
Aprovação subjetiva de timbre e equilíbrio musical permanece do usuário.

## Inspeção
A primeira passagem de brilho foi reprovada por faixas visíveis; a final usa uma faixa contínua com alfa interpolado.
A máscara frontal foi acrescentada para impedir a estátua de sobrepor pedras que devem continuar à frente.

## Executado
Build contra APIs reais Java8 e regressões do núcleo/áudio; ver evidence/build-audit.json.
Renderização real em pbuffer privado na Intel UHD, sem abrir outra janela nem carregar o modpack completo.
276 verificações gráficas na versão final: estados GL exercitados, cinco resoluções, regiões de clique, extremos de movimento e imagem estável em movimento reduzido.
Camada ausente simulada: fallback igual pixel a pixel ao caminho estático, com liberação das texturas alocadas parcialmente.
Os seis OGGs foram decodificados pelo JOrbis das bibliotecas do pack; durações e finalização corretas.
Nenhum teste isolado é apresentado como homologação de execução com todos os mods.

## Controles
`motion.layeredParallax=false` desativa os planos móveis.
`motion.animatedPlayButton=false` mantém o botão carmesim, sem a animação decorativa.
F7/movimento reduzido remove o movimento da câmera e os efeitos temporais do CTA, preservando foco e cliques.
Intensidade de parallax0 também evita carregar as camadas. Valores maiores continuam limitados pela margem restaurada da imagem.

## Limites
A fonte de cenário permanece1207x830; não se inventou detalhe4K. O efeito é2.5D, com faixas escondidas restauradas para movimento curto, não uma cena3D navegável.
Não há promessa de manter frames sendo apresentados durante travamento global de JVM/driver.
A execução completa desta versão no modpack e a aprovação final do som/visual ainda estão pendentes; havia apenas1–2GiB livres e clientes Java do usuário abertos.
Não instalar duas versões. Não tratar arquivos de testes em build/ como produto. Nenhum driver de QA ou fonte TTF/OTF/WOFF faz parte do JAR.

## Evidências para avaliação
Abrir docs/Revisao-N10.html para gravação do renderizador e controles de audição.
A animação WebP é composta de42 quadros nativos, não simulação CSS do menu.
A audição PCM encontra-se em evidence/audio/Ceremonial-SFX-Audition.wav; não é captura do SoundHandler.
O pacote de engenharia inclui o fonte, os recursos, o JAR final e evidências selecionadas; dependências do build não são redistribuídas.
Para recompilar a candidata preservada: `python tools/verify_n10.py`. Scripts de migração/versionamento são históricos e não devem ser repetidos.
