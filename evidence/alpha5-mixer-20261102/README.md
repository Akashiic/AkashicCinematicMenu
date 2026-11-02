# 0.7.0-alpha.5 — BGM/SFX no AkashicMixer (02/11/2026)

Status: **compilado e núcleo testado; NÃO rodou no jogo.** Aprovação sonora do dono pendente (OI-17).

## Build (`tools/build_native.py`)
* JAR `AkashicCinematicMenu-0.7.0-alpha.5.jar` — SHA-256 `799385c5ba9534d26c880e27986ffb2fc147ae2521a668ca8a6375e4658520bb`, 16.511.727 B, 85 classes, todas major 52 ([`native-build.json`](native-build.json)).
* Compilado duas vezes (contra o mixer da árvore suja e contra o `0ff47b7` limpo): mesmo SHA-256.
* `javac 21 --release 8 -Werror`, Linux. **Classpath diferente do autor** ([`native-classpath.json`](native-classpath.json)): SRG do RetroFuturaGradle gerado pelo build do AkashicMixer (Forge 10.13.4.1614, MCP 12), LWJGL 2.9.4-nightly (autor: 2.9.1), log4j-api 2.0-beta9-fixed, ASM 9.10.1 (autor: 9.8), launchwrapper 1.12 da Mojang (autor: 1.13 CrucibleMC), netty 4.0.10; mais `akashicmixer-0ff47b7-dev.jar` (SHA-256 `56e6e80c615e3806b4b592f77a889ca11d64dc549d803688d1b3d95dcd7375d2`), só para compilar — nenhuma classe do mixer entra no JAR (checagem *Foreign class* do script).
* O JAR não foi comparado byte a byte com um build na máquina do autor.

## Núcleo puro ([`core-suites.log`](core-suites.log))
`run_core_gates.py` (CoreTests 48 cenários / 17.489 asserções) e as demais suítes rodadas à mão, todas `failures=0`, incluindo a nova `MenuVolumeTests` (11). Linux, JDK 21 com `--release 8`.

## O que foi testado num cliente (no repositório do AkashicMixer)
O roteamento em si (som de mod com categoria *music*/*master*, slider Música do Minecraft em 0, canal a 50 %, mudo, tela do mixer com 6 canais em 427x240) foi observado num cliente de desenvolvimento Linux com um mod de teste no lugar deste menu: `Akashiic/AkashicMixer`, `docs/evidence/2026-11-02-sound-routing-smoke.md`. **Este JAR não estava nesse cliente.**

## Não executado
Cliente real ou do pack com este JAR; Windows; Angelica/ThePatcher; teste sem o AkashicMixer e com o mixer `bab631d` (caminhos *fail-open*); escuta pelo dono.
