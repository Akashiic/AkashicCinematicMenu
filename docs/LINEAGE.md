# Linhagem de artefatos — todos os JARs e pacotes (SHA-256)

Medidos em 24/10/2026 diretamente nos arquivos do PC do autor (`C:\Users\User\AkashicDev\AkashicCinematicMenu`). Horários = *mtime* do arquivo (America/Sao_Paulo).
Coluna **Pai** = linha de base declarada no `baseline.json`/parecer da própria candidata.

> ⚠️ **Dois JARs diferentes têm o rótulo `0.5.0-alpha.1`** (`9d1fa4e1…` em `native-n08` e `c9248a30…` em `native-n08-bgm-r1`). Identifique sempre pelo **SHA-256**, nunca só pela versão.
> ⚠️ Do **N09 (0.5.2) em diante** os JARs oficiais embutem a gravação de terceiros `menu.ogg`; os Releases públicos deste repositório publicam a variante **`-nobgm`** (veja [`../releases/RELEASES.md`](../releases/RELEASES.md)). Os hashes oficiais abaixo continuam sendo a referência de integridade para quem tem o arquivo.

## JARs do mod

| # | Versão | Candidata | Data/hora | Bytes | SHA-256 | Pai | Estado |
|---|---|---|---|---:|---|---|---|
| 1 | 0.1.0-alpha.1 | `dist/` (raiz) | 20/09 02:26 | 28.132 | `6d7f5b0a005343fa6188d282fe2742d32c8705fe2398e63b38283644fc86c047` | — | núcleo + cena procedural; não homologado no jogo |
| 2 | 0.1.0-alpha.2 | `navigation-r04` | 20/09 03:51 | 28.745 | `c8930796eb1e3b1ec9ef4799eec868db3a3b7ee1809033abdd5b9e6c2c54e7a2` | #1 | rotas R04 no bytecode; não homologado |
| 3 | 0.2.0-alpha.1 | `native-n01` | 20/09 12:31 | 5.764.672 | `7e5c5b23932eff2011d464cfc4e5d6c352f20d750e6ee1a0a01108baf121731a` | #2 | arte nativa; só probe GL; falha no contexto Angelica |
| 4 | 0.2.0-alpha.2 | `native-n02` | 20/09 12:58 | 5.764.721 | `377e842e41ed949f4474335f6a9e56aac7b917b39afe7420a3e98bdf85cd655c` | #3 | compat. gráfica Angelica |
| 5 | 0.2.0-alpha.3 | `native-n03` | 20/09 14:08 | 5.771.076 | `154c95045e70b15798988358ff4a38db75c57523b4946e4e4b786a0414b104ea` | #4 | 1º run no modpack (exit=0); latência de foco pendente |
| 6 | 0.2.0-alpha.4 | `native-n04` | 20/09 15:44 | 5.774.983 | `3adfd2cc102eed60666d5594afc0a375e2a1fc7047ed409c7afc81c5969c190e` | #5 | **certificada** (boot, foco, falta de recurso) |
| 7 | 0.3.0-alpha.1 | `native-n06` | 20/09 20:25 | 6.195.795 | `b038bf26f0197e70d2dbd36f930f49e45c032d050fcea42c462dac9a45df90f2` | #6 | **reprovada em runtime** (`IllegalAccessError`) |
| 8 | 0.3.0-alpha.2 | `native-n06` | 20/09 20:38 | 6.195.796 | `a5a37075fb429373b9dc3fc7d67017010b1e8d11b1b3e342331230741f3cfde0` | #7 | cartão *Disconnected* excedia a largura |
| 9 | 0.3.0-alpha.3 | `native-n06` | 20/09 20:48 | 6.196.006 | `5fdab2cf1b4d8b68876bf20550907fed6343f3349f78af4f0e0df045a0da3f06` | #8 | base da N07 |
| 10 | 0.4.0-alpha.1 | `native-n07` | 20/09 21:26 | 7.238.097 | `fa0a9ab0c77b1f12e9ec994eba6daf614d75c01f37dd430142d3a424f19fd0bd` | #9 | reprovada visualmente (texto 5–7 px) |
| 11 | 0.4.0-alpha.2 | `native-n07` | 20/09 21:33 | 7.238.264 | `226b5c13ed0052a07e379feeab5745841c06f890346fb8cdb499f5ae03f3e8a5` | #10 | moldura do rodapé sobreposta |
| 12 | **0.4.0-alpha.3** | `native-n07` | 20/09 22:31 | 7.238.260 | `14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac` | #11 | n07release2 exit=0; risco `exit=1` anterior sem causa |
| 13 | 0.5.0-alpha.1 (**parcial**) | `native-n08` | 21/09 01:35 | 7.253.935 | `9d1fa4e166d076df35279ebc78b04f5c59769e29106aaf4ddb4a9100641918ef` | #12 | ramo interrompido; **não promover** |
| 14 | 0.5.1-alpha.1 | `native-n08-bgm-6c79c577` | 21/09 01:40 | 7.253.935 | `2de7bd9a00fd9fe5687e41d85013ab797694749d802c4e6ad6bbe07d6e257b4c` | #12 | ramo paralelo (BGM fora do JAR) |
| 15 | **0.5.0-alpha.1** (**R1**) | `native-n08-bgm-r1` | 21/09 01:41 | 7.264.366 | `c9248a3060139e5334facd6186e9c09f6826af650508e6c20d1438bc52975b0d` | #12 | **linha principal**; sem a faixa; 15 checks runtime |
| 16 | 0.5.1-alpha.2 | `native-n08-bgm-6c79c577` | 21/09 02:05 | 7.254.169 | `ee22b71663a8aa94e935e9681ed919c3b02bd8ea951a6b897e7adb5f7cfd9a4c` | #14 | guarda de fronteira de áudio |
| 17 | 0.5.1-alpha.3 | `native-n08-bgm-6c79c577` | 21/09 02:19 | 7.256.912 | `4f12227f232c53ae221cc9676113f5a325e0620f5c16ce4d74d9bce316303247` | #16 | rota Et Futurum; runtime final pendente |
| 18 | **0.5.2-alpha.1** | `native-n09-audio-fixed` | 21/09 13:28 | 11.510.805 | `da5bb5579545e9716590547fbf80efbfc05771f47dba38a62999cfa9e4c0460a` | #15 | **música confirmada funcional pelo usuário** |
| 19 | 0.6.0-alpha.1 | `native-n10-ceremonial` | 21/09 15:55 | 15.066.927 | `918526606f56e12279ff581d9e2fac4ce2125d1f1b237dc62edae64f15855d5e` | #18 | faixa de brilho com marcas |
| 20 | 0.6.0-alpha.2 | `native-n10-ceremonial` | 21/09 16:00 | 15.066.940 | `429b41236dd5ebe9913ce651ba907d32a5699a4608daf45c76d09a119ac81e57` | #19 | — |
| 21 | **0.6.0-alpha.3** | `native-n10-ceremonial` | 21/09 16:18 | 16.498.716 | `a15e18a206e89aa7736d71cae22813ecb4be0e7cb0dffd75d4c10e0939785859` | #18 | pbuffer 276 checks; **sem run no pack**; aprovação do usuário pendente. *Observado na pasta diária.* |
| 22 | 0.7.0-alpha.1 | `native-n11-gui-wave2` | 21/09 18:23 | 16.508.062 | `2ed2b906e42312fe77107884eee9fc0dd2851f90142fbed5d2564d802a0bc312` | #21 | — |
| 23 | 0.7.0-alpha.2 | `native-n11-gui-wave2` | 21/09 18:25 | 16.508.146 | `2138cd5c4e0bc071ca167c31a9ecbd03fb3f8d8bcd6c094755d8b520a525bf7e` | #22 | — |
| 24 | 0.7.0-alpha.3 | `native-n11-gui-wave2` | 21/09 18:42 | 16.508.224 | `6640852dfe1e53e43625c3778268777f9b41675350f621495c400043858f2b4b` | #23 | — |
| 25 | **0.7.0-alpha.4 ★** | `native-n11-gui-wave2` | 21/09 22:46 | 16.508.324 | `7dbf5177c4e26edea3c0d5caceafac6d4ec013eee6d543d2dbe80066d4c0fb4a` | #21 | **ÚLTIMA** · full-pack exit=0 · aprovação visual do usuário pendente |

Também existe `candidates/native-n10-ceremonial/build/depth-missing-test-only.jar` (`626d227c…`, 15.304.118 B): **fixture de teste** (camada ausente simulada), *não* é distribuição.

## Pacotes de engenharia (zips) — não versionados no git

| Pacote | Data | Bytes | SHA-256 |
|---|---|---:|---|
| `AkashicCinematicMenu-N01-engineering.zip` | 20/09 12:31 | 24.233.768 | `c99c6f42811f677f32a5f77d0a4735f7d942f4777e9f0ab3db5f100fb53ead9b` |
| `AkashicCinematicMenu-N03-QA-validated-engineering.zip` | 20/09 14:08 | 12.462.089 | `d04dd1f8ab26e46dc6f4cdc6ae32e0318a132b3bb1b32511ec3c928f9ae28ce9` |
| `AkashicCinematicMenu-N04-verified-engineering.zip` | 20/09 15:52 | 11.946.928 | `d15f6dd938cc9a902f4870f3e766974af29c5324c00b1ad8dba469672b57db34` |
| `AkashicCinematicMenu-N07-Fidelity-engineering.zip` | 20/09 22:31 | 16.026.855 | `4af3409245f0da443212ac105321400625e016fd08056c0447cb48ed69ee4664` |
| `AkashicCinematicMenu-N08-BGM-code-only-engineering.zip` | 21/09 01:41 | 14.945.819 | `587fcbc43603b75d6d80aa0dab1925d63314b6438083981d8c20b2f5ba475c76` |
| `Forbidden-N10-Ceremonial-engineering.zip` | 21/09 16:18 | 40.218.053 | `bd2389e15fd627fe1d64b7641e59281f882781233f0ef3a9bbf521ee16c6b86b` |
| `Forbidden-N11-GUI-Wave2-engineering.zip` | 21/09 22:49 | 36.058.060 | `6e8d4414086e65ad49f0a132d6e48ee3f9afa919278809c17b6e63c5935197be` |

O conteúdo dos zips (fonte, recursos, evidências) já está no git, extraído por candidata; o zip N11 inclui a gravação, por isso **não** é republicado.

## Zips de fonte "antes de…" (snapshots intermediários)

Guardados como evidência no acervo (≈45–67 KB cada os pequenos; ≈6 MB os da N06): `alpha1-source-before-legibility`, `source-alpha2-before-footer` (N07), `pre-integration-src` (N08), `alpha1-source-before-audio-boundary`, `alpha2-source-before-etfuturum` (N08 6c79c577), `pre-wave2-src` (N11), `source-alpha1-before-listener-fix`, `source-alpha2-before-textfit` (N06, 6 MB, **não** incluídos no git). Eles permitem reconstruir o fonte de alphas intermediárias; os pequenos estão em `evidence/<candidata>/`.

## Artefatos de terceiros mencionados

| Item | SHA-256 | Observação |
|---|---|---|
| `BGM.mp3` fornecido pelo usuário (Fluxxwave, *Slowed + Reverb*) | `1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408` (6.475.232 B) | **não distribuído** |
| `menu.ogg` embutido (Ogg/Vorbis 44,1 kHz estéreo, 161,88 s) | `6e91e747a99148084b854012c48ce93974beb6275dc4add2951989beff0ae4a6` (4.271.570 B) | **não distribuído**; é a entrada que o código espera em `assets/akashiccinematic/sounds/bgm/menu.ogg` |
| `!Akashic-ThePatcher-1.1.0-beta.10-Cinematic-Angelica.jar` | `3182e7e3d6b51854eae008ff8eb8f346a1d0a8b291740ceff8dab9c5fabc9dab` | repo irmão *AkashicThePatcher* |

## Bibliotecas usadas para compilar (não redistribuídas)

Hashes em `evidence/native-classpath.json` por candidata. Para a N11: `srg_merged_minecraft.jar` (`d253f210…`), `recompiled_minecraft-1.7.10.jar` (`449baa03…`), `lwjgl-2.9.1.jar` (`c118f8d8…`), `log4j-api-2.0-beta9.jar` (`eb0ca9bd…`), `asm-9.8.jar` (`876eab6a…`), `asm-tree-9.8.jar` (`14b7880c…`), `launchwrapper-1.13.jar` (`ebbee8e2…`), `netty-all-4.0.10.Final.jar` (`96eaa322…`).
