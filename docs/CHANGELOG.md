# Changelog

Reconstruído a partir dos pareceres, `CHECKPOINT_*.json` e JARs do acervo (ver `PROVENANCE.md`). Datas/horas = *mtime* do JAR final (America/Sao_Paulo). "Status" segue o `QUALITY_CONTRACT`: **compilado/testado ≠ aprovado pelo usuário**.
Detalhes e evidências por versão: `CRONOLOGIA.md` §2–3. Hashes: `LINEAGE.md`.

## [0.7.0-alpha.4] — 2026-09-21 22:46 — *último estado*
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
