# Releases

Todos os Releases são **pré-lançamentos** (alpha). O único marcado como *Latest* é `v0.7.0-alpha.4`.
Hashes: `releases/SHA256SUMS` (confira com `python tools/repo/verify_release.py <pasta-dos-jars>`).

## Por que alguns JARs são `-nobgm`
A faixa de música do menu (`assets/akashiccinematic/sounds/bgm/menu.ogg`) é uma gravação **de terceiros** (Fluxxwave, *Slowed + Reverb*) e o registro do projeto diz para não redistribuí-la. Os JARs oficiais 0.5.2-alpha.1, 0.6.0-alpha.3 e 0.7.0-alpha.4 a contêm; **os públicos aqui foram gerados com `tools/repo/strip_bgm.py`** (mesmo conteúdo, só sem esse arquivo, zip regravado de forma reprodutível). Sem a faixa o mod funciona e cai no caminho "sem gravação" (menu silencioso, fallback testado na N08 R1 — **não** reverificado para 0.5.2+; ver OI-07).

| Versão | JAR oficial (com a faixa) — SHA-256 | JAR público `-nobgm` — SHA-256 |
|---|---|---|
| 0.5.2-alpha.1 | `da5bb5579545e9716590547fbf80efbfc05771f47dba38a62999cfa9e4c0460a` | `5c01f17a86c36fe5900c33dde6e770df71b81ea2072c9ffb036a0904e2fad83d` |
| 0.6.0-alpha.3 | `a15e18a206e89aa7736d71cae22813ecb4be0e7cb0dffd75d4c10e0939785859` | `4553496b0c328bb3f660f780e8dce501ec65d4f97500d3b7397ec9fbc8573707` |
| 0.7.0-alpha.4 | `7dbf5177c4e26edea3c0d5caceafac6d4ec013eee6d543d2dbe80066d4c0fb4a` | `29e086d181048d1f1bd7c617fdc145d5c47cde00cd6b98fa6185622d1eda1453` |

Para reconstruir o JAR **oficial**, o dono coloca a faixa autorizada em `src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg` e roda o build (ver `docs/HANDOFF.md`). O hash do áudio embutido na N09 era `6e91e747…` (ver `docs/LINEAGE.md`).

## Outros JARs (0.1.0 … 0.5.0-alpha.1)
São os arquivos originais, byte a byte (nenhum contém a faixa). Variantes N08 paralelas (0.5.0-alpha.1 parcial, 0.5.1-alpha.1..3), alphas intermediárias (0.3.0-alpha.1/2, 0.4.0-alpha.1/2, 0.6.0-alpha.1/2, 0.7.0-alpha.1..3) e os pacotes de engenharia (.zip, até 40 MB) **não** foram publicados; seus hashes estão em `docs/LINEAGE.md`.

## Aviso
Software alpha, **sem garantia**, sem aprovação visual do dono nas versões ≥ 0.4.0, sem licença definida (OI-08). Instalar apenas em cópia de teste do modpack.
