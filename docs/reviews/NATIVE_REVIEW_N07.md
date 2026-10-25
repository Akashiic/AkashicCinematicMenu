# N07 — fidelidade, paleta mobile e recuperação
Candidata: 0.4.0-alpha.3. Autorrevisão técnica; aprovação visual/sonora final permanece do usuário.
JAR: candidates/native-n07/dist/AkashicCinematicMenu-0.4.0-alpha.3.jar
SHA-256: 14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac
Tamanho: 7238260 bytes, 56 classes Java8.

## O que foi produzido
Um único mapeamento cover para o cenário, painel27% ancorado emx0 e composição nativa na resolução real do framebuffer.
A combinação anterior de background cover e screenshot contain deixava69.7878px laterais em1920x1001. Não era só arredondamento de1–3px.
Texto, botões, molduras e progresso são desenhados separadamente da imagem, com nove atlas por tamanho e coordenadas de pixels.
Paleta escolhida a partir da referência mobile: #090D14, #171C25, #E8E4D8, #C4AA7C, #ACB2BB e #DCEBFA.
Fonte de cenário1207x830, sem alegar novos detalhes4K. Anjo2D; blur pré-processado com a mesma projeção da cena.
A leitura compacta foi ajustada para tamanho mínimo nominal9px/12px e reticências, em vez de encolhimento arbitrário.
Uma inspeção posterior detectou a moldura sobre o último rodapé. Alpha.3 move apenas o fundo dessa moldura de95.5% para97.5%.

## Integrações exercitadas
O aviso conhecido InventoryTweaks/BogoSorter vai ao log; o aviso desconhecido de teste foi preservado.
Cinco SFX de UI permanecem no JAR. MASTER mute e SoundHandler indicando reprodução foram testados; isso não substitui escuta e aprovação artística.
Connecting e Disconnected têm cenário e painel próprios; mantêm a GUI nativa, cancelamento, retorno e mensagem original.
Jogar mantém br2.xmxcloud.net:10511, Singleplayer, Opções e Sair. Não foi criada ou acessada uma partida/mundo autenticado.
Options e seleção de mundos continuam com interface nativa nesta candidata.

## Resultados da execução final
34 verificações básicas, 10 de resolução e 20 de recuperação passaram em n07release2.
Essa contagem inclui precondições e evidências; não representa classes independentes de falhas.
O mesmo JAR foi executado na cópia completa do modpack, não apenas no navegador ou num renderizador isolado.
Foram exercitados1920x1080,2560x1440,1280x800 e854x480, com tamanho do PNG, Display, Minecraft, framebuffer e UI conferidos.
Atrasos controlados de800ms e6500ms na thread principal foram seguidos por maximização real e capturas completas1920x1001 após a retomada.
O cliente final terminou com exit0. Nenhum dos marcadores de falha de renderização monitorados apareceu nessa execução.

## Auditoria e limites preservados
Core48 cenários/17489 asserções; geometria1785; política de conexão15; política de UI140; bounds de glyphs2016; quatro listeners públicos.
A primeira captura GDI e a primeira tentativa de pausa usavam HWND anterior a Display.setDisplayMode. O bytecode confirmou destroyWindow/createWindow; o teste passou a buscar e validar o handle novo.
N07final e n07recovery guardam as falhas anteriores. N07recovery2 já confirmou a correção metodológica, sem alterar o renderer.
Uma tentativa alpha.3, n07release, terminou com exit1 antes do menu, sem hs_err ou stacktrace fatal encontrado. A causa não foi estabelecida nem atribuída ao usuário ou ao código.
A execução final retirou a manipulação de janela durante o boot e depois passou nas suítes. Isso não explica por si só o encerramento anterior.
Portanto não há aprovação geral de produção, de ausência absoluta de tela preta ou de estabilidade em qualquer driver/JVM.
Um frame armazenado não apresenta a si mesmo durante uma pausa global. O que se verificou foi a recuperação após retomar, não desenho contínuo com todas as threads paradas.
A arte continua limitada à fonte1207x830. A mudança de tipografia não cria detalhe na imagem; parallax é limitado à folga do enquadramento.
A nova paleta, os enquadramentos e a sonoridade aguardam a aprovação final do usuário.

## Preservação e arquivos
A versão anterior N04 no workspace mantém seu hash. Este fluxo alterou somente a cópia de QA. Na leitura final, a pasta diária também continha um único JARalpha.3 com o hash correto, arquivo independente da QA e do dist. Não se atribuiu autor/data da cópia nem se alterou esse arquivo.
O JAR não contém o mod de testes nem arquivos TTF/OTF/WOFF. As imagens de glyphs são ativos rasterizados de UI.
N07release2: n07-acceptance.json, resolution-acceptance.json, focused-recovery.json, suite-chain.json e final-audit.json.
CHECKPOINT_N07.json vincula as evidências ao SHA256 da versão final. Metadados de build anteriores ao runtime não são aprovação de execução.
Parecer: cenários dirigidos executados passaram; risco do encerramento anterior e aprovação artística permanecem registrados.
