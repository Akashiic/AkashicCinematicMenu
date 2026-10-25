# HANDOFF — como continuar a partir da 0.7.0-alpha.4

> Público: qualquer pessoa (ou agente de IA) que vá trabalhar no mod. Leia **na ordem**: este arquivo → [`QUALITY_CONTRACT.md`](QUALITY_CONTRACT.md) → [`OPEN-ISSUES.md`](OPEN-ISSUES.md) → [`CRONOLOGIA.md`](CRONOLOGIA.md).

## 1. Estado em uma frase

**0.7.0-alpha.4** (SHA-256 `7dbf5177c4e26edea3c0d5caceafac6d4ec013eee6d543d2dbe80066d4c0fb4a`, 81 classes) está **compilada, reproduzível e testada em escopo dirigido no modpack completo (exit=0)**, mas a **aprovação visual/sonora do dono ainda está PENDENTE**, não há "liberação geral" e há um problema conhecido de coexistência com o Angelica (OI-01).

O código-fonte em `src/` **é** o da 0.7.0-alpha.4. O histórico git reconstrói, commit a commit, cada candidata anterior (`git log --oneline`, tags `v0.1.0-alpha.1 … v0.7.0-alpha.4`).

## 2. Pré-requisitos para trabalhar

| Item | Versão usada pelo autor | Para quê |
|---|---|---|
| JDK com `javac` | Temurin 21 (compila com `--release 8`) | build e testes |
| JRE 8 | Corretto 1.8.0_422 | probes gráficos com natives LWJGL |
| Python 3 + Pillow (+ numpy/opencv só para regerar arte/áudio) | 3.11+ | `tools/*.py` |
| GPU com OpenGL ≥ 2.1 | Intel UHD (GL 4.6) | probes de renderização em *pbuffer* privado |
| Dependências de compilação (SRG!) | ver `evidence/**/native-classpath.json` | `build_native.py` |
| **A faixa `menu.ogg`** | já em `src/main/resources/assets/akashiccinematic/sounds/bgm/` | incluída no repositório (ver §5) |

**Dependências de compilação**: `srg_merged_minecraft.jar` + `recompiled_minecraft-1.7.10.jar` (build RetroFuturaGradle do 1.7.10 com nomes **SRG** — o código usa `func_*`/`field_*` de propósito), `lwjgl-2.9.1`, `log4j-api-2.0-beta9`, `asm-9.8` + `asm-tree-9.8`, `launchwrapper-1.13` (CrucibleMC) e `netty-all-4.0.10.Final`. Nada disso é redistribuído aqui. Aponte para os seus arquivos com:

```bash
# deps.json = array JSON de caminhos absolutos dos 8 jars acima
python tools/build_native.py --classpath-json deps.json
```

> `build_native.py` sem argumento usa os caminhos da máquina do autor (`C:\Users\User\AkashicDev\CubicChunks-Crucible\…`, `~/.gradle/caches/…`). Em outra máquina **sempre** passe `--classpath-json`.

## 3. Comandos de verificação (do mais barato ao mais caro)

```bash
# 3.1 Núcleo puro (sem Minecraft) — roda em qualquer JDK 8+; também roda no GitHub Actions
python tools/repo/run_core_gates.py          # compila core+tests e executa todas as suítes *Tests

# 3.2 Build do JAR contra os artefatos reais
python tools/build_native.py --classpath-json deps.json     # gera dist/AkashicCinematicMenu-0.7.0-alpha.4.jar e evidence/native-*.json

# 3.3 Gates da Wave 2 (probes de bytecode + gráfico) — exigem deps, JRE 8 e GPU
python tools/verify_wave2_alpha4.py
```

Observações:
* O build é **reproduzível** (zip com data fixa `2026-09-20`, entradas ordenadas, `--release 8`): o autor rebuildou e obteve o **mesmo SHA-256**. Para reproduzir `7dbf5177…` byte a byte você precisa dos mesmos jars **e** do mesmo `menu.ogg` (`6e91e747…`).
* Vários scripts em `tools/` são **históricos** de candidatas anteriores (`integrate_n06.py`, `package_n07_verified.py`, …) e **não devem ser reexecutados** (o próprio README da N10 avisa). Os que valem hoje: `test_core.py`, `build_native.py`, `verify_wave2_alpha4.py`, `final_audit_n11.py`, `finish_n11_delivery.py`, os `*Probe.java` e `run_*probe*.py`.
* `verify_wave2_alpha4.py` assume a estrutura original do workspace (`R.parent/'native-n10-ceremonial'` para comparar a BGM com a N10; Java 8 em `~/.jdks/corretto-1.8.0_422`). Em outra máquina ajuste esses caminhos e leia o script antes de rodar.

## 4. Como testar no jogo (procedimento do projeto)

1. **Nunca** teste no modpack de uso diário. Faça uma **cópia isolada** (`qa-*/instance`) e use identidade de teste (`ACMenuQA`), **sem credenciais reais**.
2. Instale **apenas um** JAR do mod por instância (não deixe duas versões).
3. Java legado do Technic (`jre-legacy`), heap ~3 GB. Cuidado com memória: a N10 não foi rodada no pack justamente por haver só 1–2 GiB livres.
4. Antes de qualquer novo run: confira `process.json` do run anterior, o SHA do JAR na instância e se há processo Minecraft ativo. **Não reexecute** scripts de preparação/migração se o recibo existir.
5. Testes de foco/janela: use o HWND **atual** (`Display.setDisplayMode` destrói e recria a janela) e coloque a janela em *foreground* real — o 1º gate de QUIT da N11 falhou exatamente por janela inativa.
6. Capturas: valide o tamanho real do PNG e que a gravação terminou (`CAPTURE_WRITTEN`); uma captura truncada já enganou um teste.
7. Registre cada falha como evidência (não apague); só corrija o teste quando a falha for do teste e diga isso no parecer.

## 5. A faixa de música (`menu.ogg`)

* O código espera `src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg` (Ogg/Vorbis, 44,1 kHz, estéreo, 161,88 s, SHA-256 `6e91e747a99148084b854012c48ce93974beb6275dc4add2951989beff0ae4a6`) — **o arquivo está no repositório** (commit "mantem a faixa original"); é byte a byte o embutido nos JARs 0.5.2-alpha.1, 0.6.0-alpha.3 e 0.7.0-alpha.4.
* A faixa é de terceiros (Fluxxwave, *Slowed + Reverb*); **por decisão do dono (24/10/2026) ela permanece no repositório e nos JARs oficiais**, pois a permissão de uso foi obtida por **outro contribuinte** do projeto. O documento/prova dessa permissão **não está neste repositório** e o assistente que montou o repositório não pôde verificá-lo. Origem: `BGM.mp3` fornecido pelo dono (`1e7a442d…`, não incluído) convertido para Ogg.
* **Não remova nem troque a faixa sem o dono.** Os tags anteriores a esse commit (v0.5.2-alpha.1 e v0.6.0-alpha.3) *não* têm o arquivo na árvore (o repositório foi montado antes da decisão); o JAR do Release de cada um o contém.
* Sem o arquivo o JAR compila e roda (estado `MISSING`, não suprime a música vanilla). Esse caminho foi exercitado na N08 R1; nas versões 0.5.2+ **não** foi reexecutado.
* `tools/repo/strip_bgm.py` gera uma variante sem a faixa (para quem for redistribuir sem ela); `tools/repo/verify_release.py` confere os hashes.

## 6. Regras que não podem ser quebradas

1. Ler `QUALITY_CONTRACT.md` antes de aprovar/entregar. **Implementado ≠ compilado ≠ testado ≠ aprovado pelo usuário ≠ homologado.**
2. Autorrevisão ≠ auditoria independente — diga quem revisou.
3. Evidência ausente **bloqueia** aprovação; cada critério é `PASSOU` / `FALHOU` / `NÃO EXECUTADO` com referência.
4. Não declare aprovação visual/sonora do usuário. Não declare "liberação geral".
5. Não altere o pack diário; instalação lá é ação explícita, com backup e rollback.
6. Preserve checkpoints históricos (`CHECKPOINT.json` da alpha.1 nunca foi sobrescrito); crie `CHECKPOINT_<id>.json` novo.
7. Preserve a composição aprovada (Santuário painel B + O Despertar). Não expanda para inventário/HUD/outras GUIs sem pedido.
8. Falhas de ferramenta/transporte: **reduza a operação e revalide o último estado confirmado**; não repita às cegas; **não contorne bloqueios de segurança** (foi assim que a N05 parou).
9. Transformers ASM: sempre *fail-open*, âncoras exatas, com sonda de bytecode contra o jar SRG real.
10. Não sobrescreva uma pasta de candidata existente: use workspace/QA com nome único (ver o caso `native-n08-bgm-6c79c577`).

## 7. Backlog sugerido (ordem de prioridade)

| Prio | Item | Observação |
|---|---|---|
| **P0** | Aprovação visual/sonora do dono da N10/N11 | não é tarefa de código; pendente desde 21/09 |
| **P0** | **OI-01** — `RawScope` × Angelica (blocos brancos, GL_TEXTURE_2D) | corrigir *no mod* e remover a dependência do fence externo da ThePatcher |
| P1 | OI-02 — ~2,4 s sem arte antes da camada gráfica | N05 mostrou que o caminho "antes do OpenGlHelper" via pipeline programável precisa de outra estratégia |
| P1 | OI-03 — `exit=1` inexplicado (`n07release`) | reproduzir com telemetria; nunca foi atribuído a causa |
| P1 | OI-04 — latência de minimizar/foco | 8,5 s no N03; N04 mediu <1 s por HWND direto, mas o código de foco **não mudou** |
| P2 | Build público reproduzível (Gradle/RFG) | hoje depende de caminhos da máquina do autor |
| P2 | CI com os gates de núcleo (já há workflow) e cache de deps | |
| P2 | Rodar a N11 + N10 numa sessão de jogo longa com mundo/servidor reais | nunca foi feito com mundo ou conexão |
| P3 | Telas de outros mods (hoje intocadas por design) | só por allow-list exata |

## 8. Armadilhas conhecidas

* **Rótulo de versão repetido**: dois JARs `0.5.0-alpha.1` distintos (ver `LINEAGE.md`).
* `CURRENT_STATUS.json` é **estado vivo** e, em partes, **defasado** (ainda aponta `active_candidate: native-n07`; o bloco `gui_wave2_candidate` é o correto). O README original do acervo começa em "0.1.0-alpha.1" e acumula seções — está em `docs/reviews/README.workspace-original.md`; este repositório tem um README novo.
* Contagens de "verificações" nos pareceres incluem pré-condições/repetições; não são "tipos de defeito".
* Medições de tempo (7,479 s → 2,432 s; 8,5 s de foco; 0,0804 de erro médio de recomposição) são **pontuais**, não benchmarks.
* Pack diário observado variando entre leituras (20 hashes de config; JAR extra `akashicindex-tablet-music-online-r2.jar`): homologações valem para a **cópia testada**.
* A hipótese de que o Angelica reescreve o `RawScope` está **comprovada** (2.559→2.646 B no LaunchWrapper); a hipótese de que isso causa os blocos brancos é **forte, mas não confirmada** (aceitação em GPU real pendente).

## 9. Onde está cada coisa

```
src/                      fonte da 0.7.0-alpha.4 (main + test)
tools/                    scripts e probes da N11 (cumulativos) · tools/repo/ (deste repositório). Scripts de cada candidata: no tag dela
docs/CRONOLOGIA.md        linha do tempo + fluxograma geral
docs/FLUXOGRAMA.md        diagramas (linhagem, governança, boot, jogar, áudio, ecossistema)
docs/LINEAGE.md           todos os JARs com SHA-256
docs/ARCHITECTURE.md      como o mod funciona
docs/OPEN-ISSUES.md       pendências e riscos
docs/PROVENANCE.md        fontes, lacunas, confiança
docs/QUALITY_CONTRACT.md  contrato de qualidade original (regra cardinal)
docs/reviews/             pareceres e relatórios originais (+ README/AGENTS do acervo, com acentos corrompidos na origem)
docs/design/R03/          HTML aprovado R03 (+ R02 base) e evidências
docs/research/            análise de bytecode (resize/SFX)
.github/workflows/core-tests.yml  workflow de CI (núcleo puro + docs), ativo desde 25/10/2026
checkpoints/<id>/         CHECKPOINT_*.json e README de cada candidata; CURRENT_STATUS.json (defasado)
candidate-docs/<id>/      docs/ e research/ que acompanhavam cada candidata
evidence/<id>/            recibos JSON, logs e capturas de cada candidata (≤2,5 MB por arquivo); LARGE-FILES.json = excluídos com SHA
history/                  scripts usados para transferir o código ao PC
releases/                 RELEASES.md e SHA256SUMS
```
