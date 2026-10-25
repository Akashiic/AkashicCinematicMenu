# Angelica 2.2.37 × AkashicCinematicMenu 0.7.0-alpha.4 — investigação (25/10/2026)

**Pergunta:** depois de atualizar o Angelica para a 2.2.37 o menu/splash "bugou"; o `splash.properties` também foi afetado?

**Resposta curta:** o CinematicMenu **não** é quebrado pelo Angelica 2.2.37. O que quebrou foi o NecroTempus (crash no início da thread de splash do Forge); a correção está na ThePatcher **1.1.0-beta.39** (a beta.38 não funciona no cliente real). Nenhuma mudança de código no mod foi necessária.

## Evidências
Cliente real *headless* (Linux, Mesa llvmpipe GL 4.5 core — o mesmo tipo de contexto que o Angelica cria —, Xvfb + openbox, usuário offline `ACMenuQA`), Forge 10.13.4.1614 + UniMixins 0.3.1 + GTNHLib 0.11.51 + este mod 0.7.0-alpha.4 (SHA-256 `7dbf5177…`, Release oficial). Scripts: `versions/Akashic-ThePatcher-1.1.0-beta.39-AngelicaFontMixinTrust/tools/headless-client/` no repositório AkashicThePatcher. `runs.json` tem os hashes dos mods e as linhas-chave do log; `*-frames.jpg` são capturas a cada 12 s.

| Execução | Além do mod | Resultado |
|---|---|---|
| A | Angelica 2.2.18 | `EARLY_FRAME_PRESENTED`, `FORGE_OVERLAY_STARTED`, menu cinemático |
| B | Angelica 2.2.37 | igual à A; quadro do menu difere da A em 28 de 921.600 pixels (partículas animadas); quadros do splash idênticos |
| C | Angelica 2.2.37 + NecroTempus 2.0.0, sem ThePatcher | reproduz o crash do pack: `Mixin apply for mod necrotempus failed`, `Splash thread Exception`, sem `FORGE_OVERLAY_STARTED`, crash report |
| E | como C + ThePatcher beta.39 | overlay do splash, menu cinemático, sem crash |

* **Por que o splash "sumiu" no pack:** o `NoClassDefFoundError: BatchingFontRenderer` acontece no construtor do `SplashFontRenderer`, logo no começo da thread de splash do Forge, **antes** do primeiro `SplashHooks.frame()`. Por isso o log do pack tem `Forge splash overlay hooks installed` mas nenhum `FORGE_OVERLAY_STARTED`.
* **`splash.properties`:** o Angelica não mexe nele (idêntico nas execuções A/B, exceto a data). O Forge 1614 grava `enabled=false` sozinho em `SplashProgress.finish()` → `disableSplash()` quando a thread de splash morreu e o carregamento chega ao fim. No crash do pack o jogo cai antes disso, e nas execuções C/E o arquivo ficou `enabled=true`. **Se no pack estiver `enabled=false`, volte para `true`**: o overlay deste mod desenha por cima do splash do Forge e não aparece com ele desligado.
* **Achado novo, independente do Angelica (OI-16):** sem foco na janela, o fade do boot para o menu não avança (o `FrameClock` pausa com `Display.isActive()==false`) e a tela fica no último quadro do splash ("Loading 100%") até a janela receber foco. Acontece igual com o Angelica 2.2.18.

Estado pelo QUALITY_CONTRACT: compatibilidade **testada** em ambiente headless; **não** testada no pack completo/Windows/Intel; aprovação visual do dono **não** se aplica (nenhuma mudança visual).
