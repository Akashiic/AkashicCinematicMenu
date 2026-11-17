# Arquitetura — AkashicCinematicMenu 0.7.0-alpha.4

Fonte descrita: `src/` deste repositório (= candidata N11). Pacote Java `br.com.redeakashic.cinematic`. 81 classes, bytecode Java 8 (major 52), sem dependências embutidas (Minecraft, Forge, LWJGL, log4j e ASM vêm do jogo).

## 1. O que é

Mod **client-side** para **Minecraft 1.7.10 + Forge 10.13.4.1614** que é ao mesmo tempo:

* um **coremod** (`FMLCorePlugin = …bootstrap.CinematicLoadingPlugin`, `FMLCorePluginContainsFMLMod: true`) — instala *transformers* ASM no Minecraft/FML; e
* um **mod Forge** (`modid akashiccinematicmenu`, autor "Rede Akashic") — registra o menu "Santuário", a BGM, os SFX e os temas de tela.

Identidade visual: **Santuário** (menu) e **O Despertar** (overlay do splash), catedral brutalista, anjo sem rosto, paleta fria obsidiana/ardósia/marfim/ouro apagado/celeste. Cenário-fonte 1207×830, 2,5D (3 planos + máscara), blur pré-processado.

## 2. Pacotes

| Pacote | Papel | Observação |
|---|---|---|
| `bootstrap` | `CinematicLoadingPlugin`, 4 `IClassTransformer`, `EarlyWindowHooks`, `SplashHooks` | **excluído** dos transformers (`TransformerExclusions`). Roda no classloader do LaunchWrapper. |
| `client` | `ClientProxy`, `MenuHook`, `CinematicMenu`, `MenuConfig`, `MenuBgm(+Settings)`, `UiAudio`, `MixerLink` (+ `MixerChannels`, a única classe que referencia o AkashicMixer), `TransitionTheme`, `SecondaryScreenTheme`, `ConnectionScreenTheme`, `ConnectionCancellationGuard`, `BogoWarningCompat`, `LateBogoNoticeGate`, `ScenePainter`, `RenderScope` | lógica dependente de Minecraft/Forge |
| `core` | Lógica **pura** (sem Minecraft): `MenuFlow`, `MenuLayout`, `PixelLayout`, `CoverFit`, `Tween/Easing/CubicBezier/DampedAxis/DepthCamera`, `HeroButtonState`, `ScreenPolicy`, `SecondaryScreenPolicy`, `ServerTarget`, `BgmPlayback`, `BgmFrontendPolicy`, `MusicFade`, `MenuMusicPolicy`, `MenuSoundGate`, `MenuVolumeSource`, `UiCueGate`, `VorbisHeader`, `ConnectionWatchPolicy`, `BogoNoticePolicy`, `LoadState`, `BootSnapshot`, `Viewport`, `Numbers` | testável com `javac` + JRE puros — é onde ficam os 17.489 + milhares de asserções |
| `render` | OpenGL 1.x/2.x via LWJGL 2: `NativeVisuals`, `NativeTexture`, `NativeLayout`, `BackdropRenderer`, `BitmapText`, `UiTypography`, `HeroButton`, `SecondaryFrameRenderer`, **`RawScope`** | **NÃO** está excluído dos transformers: o **Angelica** reescreve estas chamadas GL (de propósito — ver §6) |

## 3. Transformers ASM (todos *fail-open*)

Cada um confere âncoras **exatas** (contagem, descritor, nome SRG *ou* MCP); se a forma do bytecode não bater, devolve a classe original e loga em `System.err`.

| Transformer | Alvo | Ponto de inserção | Efeito |
|---|---|---|---|
| `EarlyWindowTransformer` | `net.minecraft.client.Minecraft` | logo após `OpenGlHelper.initializeTextures()` (1 âncora) | `EarlyWindowHooks.graphicsReady()` — primeiro quadro "Preparando o ambiente" |
| `SplashTransformer` | `cpw.mods.fml.client.SplashProgress$3` (`run`) | antes do `mutex`, no lugar do único `Display.update()`, antes do 2º `clearGL` | `SplashHooks.frame / present / finish` — overlay "O Despertar" sem substituir o loop/mutex do Forge |
| `LoadingThemeTransformer` | `AetherLoadingScreen`, `LoadingScreenRenderer`, `GuiDownloadTerrain` | depois do desenho do fundo de terra | só o **fundo** (`TransitionTheme`); progresso/dicas/rede intactos |
| `SecondaryThemeTransformer` | `GuiScreen.drawWorldBackground`, `GuiButton.drawButton`, `GuiSlot` | cabeçalho do método / troca de `GETSTATIC` | pele de fundo, botões (`widgets_forbidden.png`) e listas (`list_surface.png`) — **nenhuma ação é reimplementada** |

## 4. Fluxo de telas

* `MenuHook.onOpen` (`GuiOpenEvent`, prioridade LOWEST): troca **apenas** as classes exatas `GuiMainMenu`, `DummyCore.Client.GuiMainMenuVanilla`, `DummyCore.Client.GuiMainMenuOld` (`ScreenPolicy`). **SHIFT** mantido na abertura = bypass da sessão; **F8** = volta ao menu original e trava o hook na sessão (`fallback`); **F7** = movimento reduzido.
* `MenuFlow`: máquina `INTRO → READY → EXITING → HANDED_OFF`; um clique agenda **exatamente uma** navegação; o render nunca é dono da navegação.
* Ações (`MenuAction`): **PLAY** (conexão direta a `ServerTarget` = `br2.xmxcloud.net:10511`, nome "The End of Forbidden", sem lista de servidores), **SINGLEPLAYER**, **OPTIONS**, **QUIT** (encerra direto desde a N11; as confirmações destrutivas de outras telas permanecem).
* `SecondaryScreenPolicy`: allow-list **exata** de 17 classes (Options, SelectWorld, CreateWorld, Language, ResourcePacks, Controls, VideoSettings, OptionsSounds, ChatOptions, Snooper, CreateFlatWorld, FlatPresets, RenameWorld, YesNo, ConfirmOpenLink, `rtg…WorldTypeMessageGUI`, `…ReeseSodiumVideoOptionsScreen`).
* `ConnectionScreenTheme`/`ConnectionCancellationGuard`: temas de *Connecting/Disconnected* + fechamento do canal publicado tarde após cancelar (defeito real reproduzido no run03).
* `BogoWarningCompat`/`LateBogoNoticeGate`: o aviso conhecido InventoryTweaks/BogoSorter vai para o log; avisos desconhecidos são preservados.

## 5. Áudio

* **SFX** (`sounds.json`, categoria `master`): `ui.focus` (2 variantes), `ui.select`, `ui.back`, `ui.enter`, `ui.connect` → `sounds/ritual/*.ogg` (sintetizados; limite de 6 vozes, liberação de 90 ms). Há também `sounds/ui/*.ogg` (conjunto anterior, 5 arquivos).
* **BGM** (`bgm.menu`, categoria `music`, `stream:true`) → `assets/akashiccinematic/sounds/bgm/menu.ogg` (**incluída no repositório**, faixa de terceiros com permissão — OI-07). Um único *stream* via `SoundHandler`; respeita `MUSIC`/`MASTER`; entrada 2,5 s, saída 0,8 s, volume extra 0,40; contexto = menu raiz e telas de `BgmFrontendPolicy`, nunca dentro de mundo. Música vanilla/alheia só é suprimida **se** a nossa tocar de fato; sem a faixa → estado `MISSING`, nada é suprimido.
* Config separada: `config/akashiccinematic-bgm.cfg` (`music.enabled/loop/volume/fadeInSeconds/fadeOutSeconds`).
* **AkashicMixer (opcional, desde a 0.7.0-alpha.5).** Com o AkashicMixer instalado (com a API `routeSounds`), o mod registra dois canais no *Akashic Sounds Mixer* — **Música do Menu** (`menu_music`, eventos `akashiccinematic:bgm.*`) e **Sons do Menu** (`menu_sfx`, eventos `akashiccinematic:ui.*`) — e o mixer aplica o nível do canal **no lugar** do slider de categoria do Minecraft. Efeito: o slider *Música* do Minecraft deixa de afetar o menu; o *Master* do Minecraft continua valendo; mutar o canal silencia a BGM (é parada) e os SFX; a BGM do menu fica fora do *fade* de música do mixer. `music.volume` (0,40) e `audio.uiVolume` (0,45) continuam como **ajuste fino** por baixo do slider (100 % no mixer = mesmo volume de antes com Música a 100 %). A tela do mixer (`GuiAkashicMixer`) está na allow-list de `BgmFrontendPolicy`, então a BGM continua tocando enquanto o volume é ajustado. Sem o mixer, ou com um mixer anterior à API (`bab631d`), o comportamento é o da alpha.4 (*fail-open*, `MixerLink.status`).

## 6. Configuração e interruptores

`config/akashiccinematicmenu.cfg` (Forge `Configuration`): `general.enabled`; `motion.layeredParallax`, `motion.animatedPlayButton`, `motion.reducedMotion`, `motion.particles`, `motion.parallaxStrength` (0–2); `compatibility.replaceKnownDummyCoreMenus`, `compatibility.hideBogoInventoryTweaksModal`; `audio.enabled`, `audio.uiVolume` (0–1); `screens.themeLoadingTransitions`, `screens.themeVanillaMenus`, `screens.themeConnectionScreens`.

Propriedades de JVM: `-Dakashic.cinematic.disabled=true` (desliga tudo), `-Dakashic.cinematic.disableSplash=true`, `-Dakashic.cinematic.reduceMotion=true`, `-Dakashic.cinematic.disableBgm=true` (nos ramos N08 6c79c577).

## 7. Recursos (`src/main/resources`)

`native/{classic,compact,hd,wide}/` (perfis por resolução: `boot.png`, `panel.png`, `scene.jpg`, 4 botões ×2 estados, `layout.properties`) · `native/depth/` (`sky`, `architecture`, `angel`, `occluder` + versões *blur*) · `native/fonts/` (atlas rasterizados de corpo/serifa — **sem TTF/OTF/WOFF**) · `backdrop*.jpg` · `textures/gui/{widgets_forbidden,list_surface}.png` · `sounds/{ritual,ui}/*.ogg` · `sounds.json` · `mcmod.info`.

## 8. ⚠ O ponto sensível: `RawScope` × Angelica

`RawScope` abre um escopo de desenho 2D com `glPushAttrib(GL_ALL_ATTRIB_BITS)`, `glPushClientAttrib`, desabilita `TEXTURE_1D/2D/3D/CUBE_MAP` em **todas** as unidades de textura e depende de `glPopAttrib()` para restaurar.
Com o **Angelica 2.2.18** o pacote `render` é redirecionado para o `GLStateManager`; a investigação da ThePatcher beta.10 (23/09) mostrou que o `RawScope` é **reescrito** pelo `AngelicaRedirectorTransformer` e levantou a hipótese de que o cache de `GL_TEXTURE_2D` fica dessincronizado do driver (sintoma: **blocos brancos** no mundo). A ThePatcher contorna *por fora* (fence após `RawScope.close()`); a correção **no mod** ainda não existe — ver `OPEN-ISSUES.md` (OI-01).

## 9. Testes (`src/test/java`, rodam sem Minecraft)

`CoreTests` (48 cenários / 17.489 asserções), `SecondaryScreenPolicyTests` (58), `CeremonialTests` (4.807), `PixelLayoutTests` (1.785), `UiFeaturesTests` (140), `MusicCoreTests` (1.557), `BgmPlaybackTests` (18 cenários / 124), `MenuSoundGateTests` (32), `MenuVolumeTests` (11), `ConnectionWatchTests` (15), `VorbisHeaderTests`, `FadeTrace`. Os *probes* gráficos/bytecode estão em `tools/*.java` (`SecondaryTransformerProbe`, `QuitBytecodeProbe`, `SecondaryGraphicsProbe`, `CeremonialGraphicsProbe`, …).
