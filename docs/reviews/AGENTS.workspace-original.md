# Instruções de continuidade deste projeto
Antes de implementar ou aprovar alterações, ler QUALITY_CONTRACT.md, README.md e CHECKPOINT.json.
QUALITY_CONTRACT.md registra a regra cardinal de qualidade solicitada pelo usuário.
Qualidade técnica e fidelidade visual prevalecem sobre tempo de resposta.
Último HTML colado na conversa: reprovado visualmente; não confundir com a alpha Java anterior.
Referência: catedral brutalista monumental, navegação esquerda de 25–30%, anjo sem rosto à direita e verde eco-brutalista.
Separar implementação, revisão técnica, inspeção visual, testes e parecer de aprovação.
Autorrevisão não é revisão independente; declarar a autoria real da revisão.
Ausência de evidência bloqueia aprovação; compilação não comprova aparência nem runtime.
Não declarar a aprovação visual do usuário por conta própria.
Preservar checkpoints, não modificar a instância jogável e não expandir o escopo sem autorização.
Em falha do Desktop Commander, reduzir a operação e revalidar o estado antes de retomar.

## Estado visual mais recente — R04
A reprovação descrita acima é do HTML antigo colado, não da R03 posteriormente aprovada pelo usuário.
Ler LAYOUT_REVIEW_R04.md: manter o Santuário aprovado e quatro ações, com Jogar em br2.xmxcloud.net:10511.
O candidato nativo navigation-r04 foi compilado/testado no núcleo, mas não homologado ou instalado no jogo.

## Modelo da inicialização — S01.2
Ler STARTUP_REVIEW_S01.md: O Despertar é um protótipo HTML demonstrativo, não o boot nativo.
R04 foi aprovada pelo usuário; a nova inicialização S01 continua pendente de aprovação visual.
Não apresentar percentuais simulados como leitura do Forge nem declarar 60 FPS homologados.

## Integração nativa N01 — 20/09/2026
O usuário aprovou também O Despertar S01.2 e autorizou iniciar a produção nativa.
Ler NATIVE_REVIEW_N01.md e candidates/native-n01/README.md antes de retomar.
Santuário R04 + O Despertar são as referências; não retornar à alpha com arte procedural.
N01 compilado e renderer testado em OpenGL real no PC; não confundir com execução do mod no Minecraft.
Homologação do boot com mods e navegação real pendente; teste do transformer bloqueado pela camada da ferramenta, sem contorno.
CHECKPOINT.json histórico permanece intacto; o novo candidato tem CHECKPOINT_N01.json próprio. Não instalar na instância jogável.

## Estado atual — N03 / testes autônomos
Ler NATIVE_REVIEW_N03.md e candidates/native-n03/CHECKPOINT_N03.json.
O mod foi executado no modpack completo em qa-n02/instance, não apenas no probe gráfico.
N02 corrige emulação gráfica do Angelica; N03 corrige canal criado após cancelamento da conexão.
Run04 passou pelos testes funcionais de boot, menu, framebuffer, recarga, cancelamento e mensagens da janela.
Permanece pendente a latência ao minimizar/perder foco: tentativa inicial >12s, repetição 8.501s. Não declarar produção aprovada.
F8 exibiu WarningScreen do BogoSorter; não declarar retorno direto ao DummyCore.
Processo de teste encerrado exit=0; 898 arquivos originais do manifesto inalterados; nenhum JAR instalado na instância jogável.
Não repetir migrações nem scripts de preparação de runs anteriores; ler recibos antes de retomar.

## Estado vigente — N04 / janela inicial
Consultar CURRENT_STATUS.json e candidates/native-n04/CHECKPOINT_N04.json antes de escolher candidato.
A N04 (0.2.0-alpha.4) já foi executada em runs06/07 e acrescenta O Despertar antes do início do splash do Forge.
O intervalo observado até arte customizada passou de 7.479s (N03/run05) para 2.432s (N04/run07); comparação pontual, não benchmark universal.
Foco medido diretamente no HWND, sem helper de foreground: seis ciclos recuperaram em menos de 1s; não houve mudança no código de foco do menu.
O trecho anterior à inicialização gráfica ainda não está coberto. Não declarar primeiro pixel sem tela preta.
N05 é somente experimento parcial após bloqueio de ferramenta; NÃO está integrado, compilado ou autorizado para promoção.
Teste de imagens ausentes usa qa-n02/evidence/fault-run08/receipt.json; conferir restoration.json antes de iniciar outro run.
Não redistribuir o JAR alterado para provocar falha; o candidato íntegro permanece em candidates/native-n04/dist.

## Consolidação N04 — recuperação certificada
Ler NATIVE_REVIEW_N04.md e candidates/native-n04/CHECKPOINT_N04_RECOVERY.json.
Run08 removeu imagens somente em variante QA-ONLY; 12 verificações de fallback passaram, cliente encerrou exit=0.
O JAR íntegro alpha.4 foi restaurado em qa-n02/instance; restoration.json confirma SHA-256 original.
Orquestração de nova abertura após rollback foi bloqueada pela ferramenta; não ocorreu novo runtime pós-restauração nesta revisão.
898 arquivos originais verificados intactos; arquivo de uso diário continua sem o candidato.
Versão vigente segue N04, não N05. A diferença observada de 7.479s para 2.432s não elimina o intervalo inicial nem aprova produção geral.

## Retomada N04 — divergência do pack diário observada
Ler NATIVE_REVIEW_N04_RESUMPTION.md e os novos caminhos em CURRENT_STATUS.json.
O JAR ativo/restaurado e o ZIP certificado foram revalidados; núcleo retestado separadamente, sem iniciar Minecraft.
O pack diário continua sem CinematicMenu e agora contém akashicindex-tablet-music-online-r2.jar, ausente da cópia homologada.
170 JARs originais registrados inalterados; 20 hashes de configuração/metadados divergentes na leitura mais recente. Não repetir a afirmação antiga de 898 arquivos atualmente idênticos.
Valores das 18 configs divergentes equivalem aos da QA na comparação realizada; não sobrescrever para forçar igualdade de bytes.
A instalação em uso variou durante a leitura; qualquer próximo runtime exige identificar a composição exata, sem promover automaticamente os testes antigos.
N04 permanece ativa; N05 bloqueada/parcial; intervalo inicial de ~2.4s não eliminado e produção geral não aprovada.

## Trabalho novo — N07 / paleta mobile e pixels nativos
O usuário autorizou nova paleta fria/marfim/ouro a partir do launcher mobile; a preferência antiga por verde dominante foi substituída.
Ler WORK_N07.md antes de agir. A candidata0.4.0-alpha.2 está em candidates/native-n07, SHA226b5c13ed0052a07e379feeab5745841c06f890346fb8cdb499f5ae03f3e8a5.
N07 remove composição raster contain e redesenha tipografia/ornamentos em pixels; preserva SFX, Bogo específico e duas telas secundárias encontradas em N06.
N07final terminou exit0; teste principal e resoluções passaram, mas a captura GDI durante pausa falhou. Rodada dirigida n07recovery em andamento deve ser consultada antes de continuar.
Não confundir essa candidata com N05 parcial bloqueada. Não modificar o pack diário nem declarar aprovação visual do usuário.

## Estado consolidado — N07 / 0.4.0-alpha.3
Ler CURRENT_STATUS.json, NATIVE_REVIEW_N07.md e candidates/native-n07/CHECKPOINT_N07.json.
Candidata final: SHA256 14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac.
N07release2 encerrou normalmente exit=0; passaram 34 verificações básicas, 10 de resolução e 20 de recuperação.
As pausas controladas de 800/6500ms seguidas de maximização recuperaram PNG/Display/Minecraft/FBO/UI em 1920x1001.
O teste corrigido busca novo HWND após Display.setDisplayMode, que destrói e recria a janela. Não confundir correção de teste com mudança de produto.
Alpha.3 corrige a moldura do rodapé compacto; textos e ornamentos são independentes da arte, paleta acompanha o mobile.
Cena-fonte 1207x830, anjo 2D, blur pré-processado; não declarar detalhe 4K ou render contínuo durante parada global.
Há risco aberto: n07release terminou exit=1 sem causa estabelecida. A execução final bem-sucedida não explica esse encerramento anterior.
Aprovação visual e sonora final do usuário pendente; não declarar liberação geral.
A leitura final encontrou uma cópia independente do JAR alpha.3 na pasta diária, com hash correto; não inferir ator/data da cópia nem repetir que está ausente.
Este fluxo instalou apenas a QA. Não alterar/remover o JAR diário ou copiar o driver de testes para ele.
N05 continua experimento parcial bloqueado, fora do candidato e pacote. Resultados antigos e falhas permanecem preservados.

## N08 BGM — 21/09/2026
O usuário JÁ forneceu BGM.mp3. Não solicitar novamente o arquivo nem repetir a afirmação antiga de ausência.
Candidata nova isolada: candidates/native-n08-bgm-r1; ler NATIVE_REVIEW_N08_BGM.md e CHECKPOINT_N08_BGM.json.
O N08 original ficou parcial após timeout; MenuBgm.java tinha30linhas e foi preservado. A continuação foi montada sobre N07 em pasta separada.
BGM.mp3 foi convertido no ambiente do chat para menu.ogg; SHA66db58689c1745385109a2ee9e5b5757f2d57f3f692d20ed13e9a7d084999db3. O áudio não foi localizado no PC.
JAR-base N08 compilado SHA c9248a3060139e5334facd6186e9c09f6826af650508e6c20d1438bc52975b0d não contém o OGG.
Run n08bgm1:15verificações passaram; renderizadores reais Aether/vanilla tematizados e capturas inspecionadas; exit0.
O teste de áudio nessa execução foi SOMENTE fallback por ausência, com0starts/0bloqueios vanilla. Mute sem voz não prova parada de áudio audível.
Núcleo fade1557; lifecycle18cenários/124asserções; cabeçalho Ogg70casos no ambiente do chat. Não são reprodução real da faixa no Minecraft.
A transferência do OGG/kit de áudio para o PC e a homologação da BGM exata permanecem pendentes. N07 segue referência ativa, N08 integração pendente.
Nenhuma alteração na instalação diária; somente QA. Não iniciar novo runtime antes de conferir processo, hashes e presença real da gravação.

## Diagnóstico de música do menu — 21/09/2026
A instalação diária inspecionada tinha0.5.0-alpha.1 sem OGG e Música0%; latest.log confirma o recurso ausente. Não atribuir a música ouvida ao Et Futurum sem telemetria.
Foi criada0.5.2-alpha.1 autocontida em candidates/native-n09-audio-fixed, com a gravação fornecida e filtro de música por categoria/IDs conhecidos apenas no frontend.
Ler o README da candidata e evidence/integrated-audio-audit.json. Compilação e testes unitários passaram; runtime NÃO executado por falta de memória livre com clientes do usuário abertos. Nenhum processo/jar/volume diário foi modificado.
Para música audível, o controle Música precisa ser maior que0%; não contornar mute intencional. Não instalar duas versões. N07/anteriores preservadas.

## N10 — 21/09/2026 — SFX cerimoniais, planos e Jogar carmesim
O usuário confirmou a BGM da N09/0.5.2-alpha.1 como funcional. Ela é a base desta produção; bytes, evento e código de BGM foram preservados.
Candidata: candidates/native-n10-ceremonial, versão0.6.0-alpha.3, SHAa15e18a206e89aa7736d71cae22813ecb4be0e7cb0dffd75d4c10e0939785859.
Ler CHECKPOINT_N10.json, NATIVE_REVIEW_N10.md e README da candidata antes de retomar.
Três profundidades reais(céu, arquitetura, anjo/halo) mais máscara frontal na profundidade da arquitetura; fonte1207x830, efeito2.5D e movimento limitado.
Seis arquivos originais de SFX sintetizados; não são gravações de cantores gregorianos. CTA carmesim/branco com animação separada da área clicável.
276 verificações do renderizador OpenGL em pbuffer privado passaram; seis OGGs decodificados pelo JOrbis do pack; fallback de camada ausente conferido pixel a pixel.
Não foi iniciado o modpack completo: havia clientes do usuário e apenas1–2GiB livres. Homologação com Angelica/Forge e aprovação artística continuam pendentes.
Não apresentar vídeo do probe como captura do Minecraft nem audição PCM como gravação do SoundHandler.
Galeria offline: docs/Revisao-N10.html. JAR e pacote de engenharia em dist, recibo em evidence/archive-receipt.json.
O cenário estático permanece no startup e fallback; movimento reduzido desativa os planos e animações temporais do botão.
A candidata não foi instalada por este fluxo; não substituir a N09 do usuário automaticamente. Falhas/reprovações de shimmer e versões intermediárias foram preservadas.

## N11 — GUI Wave 2 / 0.7.0-alpha.4 — 21/09/2026
Ler NATIVE_REVIEW_N11.md, candidates/native-n11-gui-wave2/CHECKPOINT_N11.json e README da candidata.
SHA final: 7dbf5177c4e26edea3c0d5caceafac6d4ec013eee6d543d2dbe80066d4c0fb4a.
Options, Select World, Create World/RTG, Language, Resource Packs, Controls, Reese Video, Sound, Chat, Snooper, Flat/Presets e confirmações auditadas foram tematizados.
A lógica vanilla/modded dessas telas não foi reimplementada; GuiSlot/GuiButton/GuiScreen recebem apenas skin/background em allow-list exata.
Main-menu QUIT fecha direto. Delete-world e demais confirmações destrutivas permanecem; GuiYesNo foi exercitado em runtime com callback inofensivo.
Run full-pack n11run1 encerrou exit0, sem mundo ou conexão. BGM permaneceu tocando pelos submenus e zero erros GL/fallbacks monitorados foram observados.
O primeiro gate de quit foi inválido por active=false; a repetição com foreground real encerrou sem GuiYesNo. Preservar ambos os registros.
RTG Create World tem rota stateful: WorldTypeMessageGUI ou GuiCreateWorld. Reese Sodium é a tela de vídeo real do pack; ambas rotas estão cobertas.
A alpha.4 foi recompilada de forma reproduzível. Pacote em dist/Forbidden-N11-GUI-Wave2-engineering.zip.
A instalação diária observada continha N10/0.6.0-alpha.3; este fluxo não a substituiu. Aprovação visual do usuário permanece pendente.
