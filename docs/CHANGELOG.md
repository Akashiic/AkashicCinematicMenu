# Changelog

Reconstruído a partir dos pareceres, `CHECKPOINT_*.json` e JARs do acervo (ver `PROVENANCE.md`). Datas/horas = *mtime* do JAR final (America/Sao_Paulo). "Status" segue o `QUALITY_CONTRACT`: **compilado/testado ≠ aprovado pelo usuário**.
Detalhes e evidências por versão: `CRONOLOGIA.md` §2–3. Hashes: `LINEAGE.md`.

## [0.7.0-alpha.10] — 2026-11-17 — novo endereço do servidor (candidata)
- O botão **Jogar** conecta em `br2.xmxcloud.net:10542` (pedido do dono, 17/11/2026; antes porta `10511`). Mudança só em `core/ServerTarget.PORT`; o teste "exact direct server endpoint" passa a exigir a porta nova.
- A tela "Conectando" já mostra o endereço real desde a alpha.8, então passa a exibir o novo sozinha.
- Status: núcleo PASSOU; JAR na CI do PR; **não rodou no jogo.**

## [0.7.0-alpha.9] — 2026-11-17 — Listas com o tema (candidata)
- Pedido do dono: Singleplayer, Multijogador, Pacotes de recursos e Idiomas "mal têm o nosso background". Causa (vista nas capturas reais da N11): a `GuiSlot` vanilla pinta o fundo da lista **opaco** (a nossa textura multiplicada pela cor sólida do vanilla) mais as faixas de cima e de baixo; a tela de mundos nem chama o fundo padrão.
- Correção genérica, em todas as telas da allow-list que usam lista: dois ganchos novos no `SecondaryThemeTransformer` (âncoras exatas, fail-open independente do patch de textura existente): logo depois do `Tessellator.draw()` do fundo da lista em `GuiSlot.drawScreen`, o painel é repintado com recorte (scissor) e ganha uma **superfície translúcida** (café 46 %, fios dourados, friso vermelho); no fim de `overlayBackground`, a faixa recebe o painel de volta, sem perder a função de esconder as linhas que passam do limite.
- **Forge**: no Forge 1.7.10 o fundo da lista sai de `drawScreen` para `drawContainerBackground` (descoberto pela sonda na CI). O transformer agora reconhece as duas estruturas (vanilla e Forge), para a troca de textura e para os ganchos. *Correção de registro:* as 7 menções de "GuiSlot themed" da N11 vêm de sondas contra a estrutura vanilla; no jogo, com o Forge, o patch de textura da lista provavelmente nunca foi aplicado (por isso as listas marrons das capturas).
- Lista do tamanho da tela (mundos, idiomas, servidores) é limitada à largura do painel.
- Menu principal (pedido do dono): **Single** e **Multi** na mesma linha, duas metades iguais que somam exatamente a largura do container (bordas alinhadas com os outros botões); textos abreviados "02 SINGLE" / "03 MULTI". O grupo passa a 4 linhas e continua centralizado entre o lema e o Discord. Setas ←/→ também navegam.
- CI: `tools/ci/run_transformer_probe.py` roda a sonda do transformer contra a `GuiSlot`/`GuiScreen`/`GuiButton` reais (Forge 1.7.10 do RFG) antes do build; log publicado como artifact.
- Status: núcleo PASSOU; transformer testado contra uma `GuiSlot` sintética (verificação de bytecode `-Xverify:all`, ordem dos ganchos, idempotência); sonda contra a `GuiSlot` real **PASSOU** na CI (vanilla SRG e Forge: 16 checagens e 2 ganchos cada); **JAR compilado** (`08a934db…`, 16.236.164 B, 87 classes); **não rodou no jogo; aprovação do dono pendente.**

## [0.7.0-alpha.8] — 2026-11-17 — Multijogador + Discord (candidata)
- **Multijogador de volta** (pedido do dono, 17/11; tinha sido removido na navegação R04 de 20/09): ordem Jogar, Singleplayer, **Multijogador**, Opções, Sair. Abre a `GuiMultiplayer` vanilla.
- **Botão do Discord**: compacto, azul Discord (`#5865F2`), abaixo dos cinco botões; abre direto no navegador (escolha do dono, sem tela de confirmação) o convite fixo `https://discord.gg/cMc32d7UTF` (`core/MenuLinks`). Não sai do menu; 1,5 s de proteção contra clique duplo. Teclado: Tab/setas/Home/End passam por ele.
- O menu continua sem URL/comando arbitrário: `MenuAction` é fechado e o único link é a constante testada.
- Telas de multijogador (`GuiMultiplayer`, `GuiScreenAddServer`, `GuiScreenServerList`) entram nas allow-lists exatas do tema e da BGM do menu.
- Correção: a tela "Conectando" com tema mostrava sempre `br2.xmxcloud.net:10511`; agora mostra o servidor que está sendo de fato conectado (`Minecraft.func_147104_D`), o que importa para servidores da lista.
- Layout (ajustes do dono, 17/11): os 5 botões ficam **centralizados no espaço livre** entre o fim do lema (35 % da altura; a linha do header, 27,2 %, quando o lema não aparece) e o botão do Discord (80 %), opção "B". **Jogar mais alto** (8,1 % contra 6,0 % dos demais, texto 23 contra 19) e com **espaço maior abaixo dele** (2,6 % contra 1,0 % entre os demais), ambos incluídos na centralização (diferença ≤ 1 px, testada em `PixelLayoutTests`). O rótulo "EDIÇÃO DE NATAL" foi para o rodapé ("EDIÇÃO DE NATAL 2026", no lugar de "BOAS FESTAS · NATAL 2026", que cortava em 854/1024).
- Status: núcleo e 11 suítes PASSOU (CoreTests 49 cenários); amostras pelo código real; **JAR compilado na CI** (`bd08d75e…`, 16.233.427 B, 87 classes); **não rodou no jogo; aprovação do dono pendente.**

## [0.7.0-alpha.7] — 2026-11-17 — Edição de Natal + AkashicMixer (candidata)
- Integra o branch `claude/focused-tesla-4haxh8` (0.7.0-alpha.5, 02/11), que tinha ficado fora do `main`: a BGM e os SFX do menu voltam a ser controlados pelo AkashicMixer quando ele está instalado (fail-open). A alpha.6 não tinha esse hook.
- CI `build-jar` baixa `akashicmixer-0ff47b7-dev.jar` do JitPack e confere o SHA-256 `56e6e80c…` (o mesmo da evidência da alpha.5) antes de compilar.
- Pendência do mixer renumerada de OI-17 para **OI-20**.
- Status: núcleo e 11 suítes PASSOU; **JAR compilado na CI** (`bc7fdbee…`, 16.229.515 B, 85 classes); **relato do dono em 17/11/2026: "Funcionou essa versão" (escopo do teste não detalhado; não substitui a homologação).**

## [0.7.0-alpha.6] — 2026-11-17 — N12 *Edição de Natal* (candidata, sem JAR)
- *Numeração:* entrou no `main` como `0.7.0-alpha.5`, renomeada para alpha.6 porque já existia um JAR alpha.5 (`799385c5…`, AkashicMixer, branch não mergeado) — ver `LINEAGE.md`.
- **Nova estética completa** a partir da arte de Natal do dono (`docs/design/N12-natal/`): menu, loading/splash, transição, conexão e telas secundárias.
- Paralaxe refeita em 3 planos (corredor, perseguidores, garota + árvore) de 2080×1196; vidro em meia resolução; enquadramento ancorado para 4:3 e 21:9.
- Paleta quente (café, ouro de estrela, creme, vermelho de laço, verde pinheiro); estrela, guirlanda de luzes e neve; botão Jogar vermelho de Natal.
- Textos de Natal mantendo **THE END OF FORBIDDEN**; botões no mesmo lugar.
- Correção: texto pequeno perdia a base das letras (E→F) na filtragem bilinear; glifos ganham 1 px de margem.
- **BGM do menu trocada** pela faixa fornecida pelo dono (Ogg/Vorbis 44,1 kHz estéreo, 225,79 s, `616bfa84…`), copiada sem recodificar; `sounds.json` (`akashiccinematic:bgm/menu`, `stream: true`) e o loop já atendiam, sem mudança de código.
- CI `build-jar`: gera as dependências SRG via RetroFuturaGradle (`tools/ci/rfg-deps`), roda `build_native.py` e publica JAR, `.sha256`, `native-build.json` e amostras como artifacts **soltos** (`upload-artifact@v7`, `archive: false`).
- Ferramentas: `tools/build_natal_layers.py`, `tools/build_natal_gui.py`, `tools/preview/` (amostras pelo código real, sem Minecraft).
- Status: núcleo e suítes PASSOU; **JAR compilado na CI** (`dcc69230…`, 16.226.074 B, 81 classes); **não rodou no jogo; aprovação visual do dono pendente.**

## [0.7.0-alpha.5] — 2026-11-02 — *candidata, não testada no jogo*
- **BGM e SFX do menu passam para o AkashicMixer** (pedido do dono, 02/11/2026): com o AkashicMixer instalado, canais **Música do Menu** e **Sons do Menu** no *Akashic Sounds Mixer*; o slider *Música* do Minecraft não afeta mais o menu (o *Master* continua). Integração opcional (`after:akashicmixer`); sem o mixer, comportamento da alpha.4.
- A tela do mixer mantém a BGM do menu tocando (allow-list de `BgmFrontendPolicy`).
- `tools/build_native.py` exige a API do AkashicMixer no classpath (`--mixer-jar`), só para compilar; nada do mixer entra no JAR.
- Status: núcleo puro testado (todas as suítes, Linux). **Não rodou no jogo; aprovação sonora do dono pendente.**

## [0.7.0-alpha.4] — 2026-09-21 22:46
- Tema do Santuário estendido às GUIs secundárias (allow-list de 17 classes): Options, Select World, Create World (+ aviso RTG), Language, Resource Packs, Controls, Video (Reese Sodium), Sound, Chat, Snooper, Flat World/Presets, Rename World, confirmações vanilla.
- **Sair** encerra direto no menu principal; confirmações destrutivas preservadas.
- Status: testes dirigidos no pack completo aprovados (13 gates; 83 checagens; exit=0). **Aprovação visual do dono pendente.** OI-01 (RawScope × Angelica) em aberto.

## [0.6.0-alpha.3] — 2026-09-21 16:18 (N10)
- CTA **Jogar** carmesim; cenário 2.5D em 3 profundidades + máscara de ocultação; 6 SFX rituais sintetizados.
- Status: 276 + 4.807 verificações de renderizador/câmera; **não** rodou no pack completo; aprovação pendente.

## [0.5.2-alpha.1] — 2026-09-21 13:28 (N09)
- BGM embutida (streaming Ogg/Vorbis via `SoundHandler`, categoria MUSIC); filtro de música alheia só no frontend. **Música confirmada como funcional pelo dono** (linha de base).
- *Obs.:* a faixa é de terceiros e permanece no repositório e nos JARs oficiais, com permissão obtida por outro contribuinte — ver OI-07.

## [0.5.0-alpha.1] — 2026-09-21 01:41 (N08 R1) *(rótulo duplicado: existe outro JAR 0.5.0-alpha.1, variante parcial)*
- Motor de BGM (sem a faixa no JAR); aviso de fade/ciclo de vida; 15 verificações de runtime.
- Ramos paralelos preservados: `variant/n08-partial`, `variant/n08-bgm-6c79c577` (0.5.1-alpha.1..3).

## [0.4.0-alpha.3] — 2026-09-20 22:31 (N07)
- Compositor responsivo por pixels do framebuffer; tipografia em atlas; paleta do launcher mobile; mínimos de legibilidade; moldura corrigida.
- `n07release2` 34+10+20 OK. Risco: `n07release` exit=1 sem causa (OI-03).

## [0.3.0-alpha.3] — 2026-09-20 20:48 (N06)
- Resize do splash, SFX de UI, aviso Bogo/InventoryTweaks, telas Connecting/Disconnected. alpha.1 reprovada em runtime (`IllegalAccessError` em `LateBogoNoticeGate`), corrigida em alpha.2/3.

## [0.2.0-alpha.4] — 2026-09-20 15:44 (N04)
- Hook após `OpenGlHelper.initializeTextures`: 1ª arte visível 7,479 s → 2,432 s. Foco medido ≤ 406 ms. Teste de falha com imagens ausentes.

## [0.2.0-alpha.3] — 2026-09-20 14:08 (N03)
- 1ª execução no modpack completo (cópia isolada). Corrigido canal de conexão que ficava aberto >10 s após cancelar.

## [0.2.0-alpha.2] — 2026-09-20 12:58 (N02)
- Retira exclusão do pacote `render` para o Angelica emular o GL.

## [0.2.0-alpha.1] — 2026-09-20 12:31 (N01)
- Cena nativa Java/LWJGL com 52 ativos aprovados; sem navegador em runtime. Não liberada.

## [0.1.0-alpha.2] — 2026-09-20 03:51 (R04 navegação)
- Jogar / Singleplayer / Opções / Sair; rota Jogar → `br2.xmxcloud.net:10511` verificada no bytecode.

## [0.1.0-alpha.1] — 2026-09-20 02:26
- Primeiro JAR: núcleo (tween, fluxo), 27 classes, compilado contra APIs reais; não homologado.

## Não promovido
- **N05** (`experiment/n05-blocked`): renderização antes do `OpenGlHelper`; bloqueado pela camada de ferramentas; sem build.
