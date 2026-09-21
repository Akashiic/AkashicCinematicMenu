# N08 — BGM recebida e telas de travessia
Esta candidata usa um workspace exclusivo para evitar sobrescrever uma preparação N08 diferente encontrada na QA antiga.
Código: native-n08-bgm-6c79c577 / 0.5.1-alpha.1.
QA física separada: qa-n08-bgm-6c79c577. Nenhuma instalação diária foi alterada.

## Fonte da música
O usuário anexou BGM.mp3. O arquivo está disponível na conversa e foi encontrado no PC sob outro nome.
SHA-256 de ambos: 1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408.
Foi usada essa cópia exata; não se baixou nem se buscou outra gravação.
Conversão Ogg Vorbis estéreo44.1kHz, 7138944frames / 161.880816s; sem mudar tempo, pitch ou aplicar reverb.
Apenas atenuação constante de -1.6697179dB para margem contra overshoot do decode MP3; sem compressor/normalização dinâmica.
Fade é aplicado em runtime, não gravado destrutivamente no arquivo.
A validação descartou um resultado com padding de um contador estimado do decoder; o pipeline final lê até o EOF real em blocos pequenos.

## Música no cliente
Uma única voz de streaming usa o SoundHandler do Minecraft e as categorias MUSIC/MASTER.
Entrada padrão2.5s, saída0.8s, ganho adicional0.40; opções finitas e limitadas em config/akashiccinematicmenu.cfg.
Submenus fora do mundo mantêm a voz; conexão/terrain/working saem do escopo. Mundo presente sempre devolve controle à música natural.
A supressão é direcionada à música minecraft/music.* no frontend quando a substituição está pronta; não altera os arquivos vanilla.
Arquivo ausente/inválido tem fallback; recarga revalida a mídia; bypass específico: -Dakashic.cinematic.disableBgm=true.
O arquivo de reprodução fica fora do JAR, em config/akashiccinematic/bgm/menu.ogg.

## Tema da textura de terra
A frase exata da captura veio de assets/aether_legacy/texts/en_US.txt; a classe é AetherLoadingScreen.
O hook atua somente depois do quadrilátero de terra e antes do progresso/textos/dicas originais.
Também cobre LoadingScreenRenderer vanilla e o desenho de GuiDownloadTerrain, preservando update/keepalive e fluxo de rede.
Não troca globalmente options_background e não cancela os métodos de carregamento.

## Estado antes do teste final
Build2de7bd9a00fd9fe5687e41d85013ab797694749d802c4e6ad6bbe07d6e257b4c, Java8/APIs reais.
Core48/17489; música1023; geometria1785; conexão15; política UI140. ResourcePack local11 verificações passaram.
Processo Minecraft próprio15828, execução verify1; driver de aceitação5344. Revalidar process.json antes de qualquer nova execução.
A última escrita interrompida do MenuBgm anterior tinha apenas30linhas; a retomada conferiu o arquivo e preservou o baseline antes da integração.
