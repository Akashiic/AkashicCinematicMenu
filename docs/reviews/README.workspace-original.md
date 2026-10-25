# Akashic Cinematic Menu — 0.1.0-alpha.1
Checkpoint: ACM-2026-09-20-ALPHA1.
Escopo: somente startup e menu principal do Forbidden Series 1.7.10 desktop.

## Estado real
Core: 44 cenários, 17.538 asserções, zero falhas; execução no PC confirmada.
JAR: dist/AkashicCinematicMenu-0.1.0-alpha.1.jar, 28.132 bytes, 27 classes Java 8.
SHA-256: 6d7f5b0a005343fa6188d282fe2742d32c8705fe2398e63b38283644fc86c047
Compilado contra APIs reais locais. NÃO homologado dentro do Minecraft.
Splash existente permanece intacto. Nenhum JAR instalado na instância jogável.
Arte nativa é procedural técnica; anjo final, 3D real e postFX não implementados.

## Executar
python tools/test_core.py
python tools/build_native.py
Dependências locais somente lidas; origem e hashes em evidence/native-classpath.json.

## Recuperação implementada, ainda não homologada no jogo
F7 alterna movimento reduzido. F8 retorna ao menu anterior e desativa o hook nesta sessão.
SHIFT mantido na abertura ativa bypass. JVM: -Dakashic.cinematic.disabled=true.

## Cronologia
M0 auditoria -> M1 core -> M2 alpha compilada -> M3 homologação isolada do pack.
B1 estudar integração do boot cedo, em paralelo ao teste M3 e à produção artística A1.
M4 arte final 2,5D -> B2 startup real -> M5 3D/efeitos opcionais -> R1 regressão e piloto.
Próximo bloqueador: runtime com DummyCore/Angelica na instalação-alvo isolada.
O pacote de fonte entregue na conversa inclui plano diretor, fluxogramas Mermaid e prévia HTML offline.
A prévia teve 24 verificações no Chromium do ambiente de produção; não é teste do Minecraft.
Relatórios locais do build e testes estão em evidence/.

## Regra cardinal de qualidade — 2026-09-20
Ler QUALITY_CONTRACT.md e AGENTS.md antes de retomar ou aprovar qualquer etapa.
O último HTML colado na conversa foi REPROVADO VISUALMENTE; a concept brutalista anterior permanece como referência.
Os resultados de testes da alpha acima não validam esse HTML posterior. Nenhuma nova homologação é declarada nesta revisão documental.

## Rodada visual R02.1 — em avaliação
Ler LAYOUT_REVIEW_R02.md para a nova rodada HTML: Santuário, Obelisco e Véu.
Aprovação visual final pendente do Akashic; a alpha Java permanece no checkpoint anterior.
Os artefatos HTML desta rodada são entregues na conversa, não instalados no PC nem no modpack.

## Atualização R04 — composição aprovada, quatro ações
O usuário aprovou o visual R03. Ler LAYOUT_REVIEW_R04.md para o estado mais recente.
Jogar conecta diretamente a br2.xmxcloud.net:10511; Singleplayer seleciona mundos locais; Opções; Sair.
O HTML atualizado foi entregue na conversa. Candidato Java de navegação em candidates/navigation-r04.
Aprovação visual não equivale a homologação do JAR; nenhum candidato foi instalado na instância jogável.

## Inicialização S01.2 — modelo em avaliação
O usuário aprovou a R04 e solicitou um modelo do startup. Ler STARTUP_REVIEW_S01.md.
HTML O Despertar entregue na conversa, com progresso demonstrativo e transição ao menu aprovado.
Aprovação visual S01 pendente. Sem integração ao boot real; desempenho de 60 FPS não aprovado.
Nenhum JAR, splash ou checkpoint nativo alterado nesta rodada.

## Integração nativa N01 — candidato de engenharia
O usuário aprovou O Despertar S01.2; menu R04 e startup são agora a base visual da produção.
Novo workspace: candidates/native-n01, versão 0.2.0-alpha.1. Ler NATIVE_REVIEW_N01.md.
Arte aprovada já desenhada em Java/OpenGL no PC, sem navegador em runtime.
Probe gráfico isolado passou; Minecraft completo e conexão não foram executados. Não é liberação para produção.
O ensaio do transformer ficou bloqueado pela ferramenta. Evidência ausente não foi promovida a aprovação.
Alpha.1/alpha.2 e CHECKPOINT.json originais preservados; nenhum JAR instalado na instância jogável.

## N03 — testes autônomos dentro do modpack
Ler NATIVE_REVIEW_N03.md para o resultado mais recente; candidato em candidates/native-n03.
JAR 0.2.0-alpha.3 executado em qa-n02/instance. N02 resolveu compatibilidade gráfica; N03 resolveu cancelamento tardio de conexão.
A rodada run04 confirmou boot visual real, quatro ações, redimensionamento/FBO, recarga e fechamento de canal.
35 verificações game-thread + 7 de reconexão + 16 de mensagens da janela passaram na execução final.
Foco/minimização ainda tem demora não aprovada; ver falha inicial preservada. F8 passa pelo aviso do BogoSorter.
Nenhuma liberação geral declarada; processo de teste encerrado com exit=0; 898 arquivos originais verificados intactos.
Arte e checkpoint visual aprovados permanecem; root CHECKPOINT.json histórico não foi substituído.

## N04 — inicialização antecipada e recuperação certificada
Estado vigente em CURRENT_STATUS.json; relatório completo em NATIVE_REVIEW_N04.md.
Candidato alpha.4 em candidates/native-n04/dist; arte aparece antes do splash do Forge, ainda após preparar a camada gráfica.
Comparação pontual: N03 7.479s / N04 2.432s para a primeira captura customizada. Intervalo inicial residual não eliminado.
Runs06/07: 46 verificações normais e seis ciclos de foco direto. Run08: 12 verificações de recursos ausentes com fallback bem-sucedido.
Variante de falha removida da cópia QA; JAR íntegro restaurado e hash confirmado. Não houve nova partida pós-restauração nesta revisão.
Pacote AkashicCinematicMenu-N04-verified-engineering.zip contém fonte, JAR íntegro, evidências e capturas inspecionadas; exclui variante de falha e experimento N05.
Modpack de uso diário preservado; não é liberação geral. Próximo gate visual permanece o intervalo anterior ao hook gráfico.

## N07 — fidelidade responsiva e paleta do launcher mobile
Candidata vigente: 0.4.0-alpha.3, em candidates/native-n07/dist.
Ler NATIVE_REVIEW_N07.md e CURRENT_STATUS.json; o checkpoint histórico permanece preservado.
O cenário usa um único enquadramento cover e a UI é composta em pixels do framebuffer. Textos/ornamentos não são uma captura ampliada.
Paleta fria em obsidiana/ardósia, marfim, ouro apagado e celeste. A aprovação artística final continua com o usuário.
N07release2 executou 34 verificações básicas, 10 de resolução e 20 de recuperação, encerrando exit=0.
Testes incluem as duas telas secundárias, aviso Bogo conhecido/desconhecido, SFX/mute, quatro resoluções e retomada após pausas de 800/6500ms.
Riscos: uma tentativa anterior encerrou cedo com exit=1 sem causa estabelecida; imagem-fonte 1207x830; sem garantia de renderização durante parada global.
A instalação diária foi somente lida nesta revisão e já continha uma cópia independente do JAR final com hash correto. Não atribuir automaticamente essa instalação ao fluxo de QA.
Não há aprovação geral de distribuição. O pacote de engenharia não inclui o mod de QA nem fontes TTF/OTF/WOFF.

## N10 — produção cerimonial e parallax por planos
Nova candidata0.6.0-alpha.3 em candidates/native-n10-ceremonial; ler NATIVE_REVIEW_N10.md.
O usuário confirmou a música da N09 como funcional. Na N10, faixa, controlador e evento BGM permanecem idênticos.
Botão Jogar carmesim/branco animado; três planos de parallax e máscara de ocultação; seis SFX sintetizados inspirados em catedral.
O renderer foi executado em OpenGL real/pbuffer privado, com276 verificações, cinco resoluções e fallback de camada ausente. Não foi o modpack completo.
A execução completa com Forge/Angelica e a aprovação visual/sonora do usuário permanecem pendentes; a N09 diária não foi substituída.
Galeria no PC: candidates/native-n10-ceremonial/docs/Revisao-N10.html. JAR, fontes e pacote de evidências em dist e nas subpastas da candidata.
O pacote de engenharia foi verificado e a recompilação reproduziu o hash do JAR. Não inclui driver de QA ou arquivos de fonte TTF/OTF/WOFF.

## N11 — GUI Wave 2
Candidata 0.7.0-alpha.4 em candidates/native-n11-gui-wave2. Parecer em NATIVE_REVIEW_N11.md.
A identidade do Santuário foi levada a Options, mundos, criação, idioma, Resource Packs, controles, vídeo, áudio, chat, Snooper, flat/presets e confirmações.
O pack real usa Reese Sodium para vídeo e pode interpor RTG antes de Create World; a homologação cobriu esses caminhos.
O botão Sair do menu principal encerra diretamente o Minecraft, mas confirmações destrutivas continuam nativas.
O cliente completo de QA terminou exit0, a BGM continuou pelos submenus, nenhum mundo foi aberto e nenhum erro GL/fallback do tema foi registrado.
Galeria local: candidates/native-n11-gui-wave2/docs/Revisao-N11.html.
JAR e pacote de engenharia ficam em candidates/native-n11-gui-wave2/dist. A versão diária não foi substituída por este trabalho.
Aprovação visual final da Wave 2 continua pendente antes de marcar liberação geral.
