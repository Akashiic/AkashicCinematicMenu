# Proveniência — de onde vem cada afirmação deste repositório

Montado em **24/10/2026** pelo Claude (Anthropic) a pedido do dono do projeto, a partir de:

1. o JAR anexado `AkashicCinematicMenu-0.7.0-alpha.4.jar`;
2. o acervo local `C:\Users\User\AkashicDev\AkashicCinematicMenu` (docs, candidatas `candidates/*`, `evidence`, `tools`, `transfer*`, `analysis-resize-sfx-20260920`);
3. `C:\Users\User\Desktop\Codex\Forbidden-Santuario-R03` (design HTML aprovado);
4. registros de sessão do Codex em `C:\Users\User\.codex\sessions\2026\09\19..23`;
5. o checkpoint `Akashic-ThePatcher-beta10-Cinematic-Angelica` (23/09).

## Níveis de confiança

| Nível | Fonte | Como tratar |
|---|---|---|
| A | JAR/arquivo medido (SHA-256, tamanho, mtime, conteúdo do zip) | fato |
| B | `CHECKPOINT_*.json`, `evidence/*.json`, logs de teste do próprio projeto | fato *sobre o que o teste registrou*, no escopo declarado |
| C | Pareceres `.md` (autorrevisão do assistente que os escreveu) | declaração do autor; confira com B |
| D | Registros do Codex (UTC → convertidos) | contexto histórico; texto pode ter sido compactado |
| E | Inferência do documentador (marcada **[INF]**) | hipótese |

## Como o histórico git foi construído (importante)

* O dono **não mantinha git** no acervo (o handoff do launcher mobile diz "o trabalho acumulado permanece sem commit"; o mesmo vale aqui — não há `.git` em `AkashicCinematicMenu`).
* O histórico deste repositório é **reconstruído**: um commit por candidata, na ordem cronológica, cada um contendo exatamente o `src/` (e `tools/`, docs, evidência) daquela candidata, copiado do disco.
* **Data do autor (`GIT_AUTHOR_DATE`) = *mtime* do JAR final da candidata** (America/Sao_Paulo); **data do commit = hora real da reconstrução**. Ambas aparecem em `git log --format=fuller`. As datas de autor **não** são horas de commits originais — esses nunca existiram.
* Candidatas com várias alphas (N06, N07, N10, N11…) têm **um** commit com o fonte da **alpha final**; o fonte das alphas intermediárias, quando preservado, está nos zips `*-before-*` (pequenos em `evidence/`).
* Ramos `experiment/n05-blocked`, `variant/n08-partial` e `variant/n08-bgm-6c79c577` preservam os caminhos não promovidos.
* Tags `vX.Y.Z-alpha.N` marcam cada candidata no commit que contém seu fonte.

## O que foi incluído / excluído

| Incluído no git | Fora do git |
|---|---|
| fonte (`src/`), testes, `tools/`, READMEs, CHECKPOINTs, pareceres, recibos JSON, logs, capturas ≤ 2,5 MB, pesquisas de bytecode (texto) | JARs e zips (vão para **Releases** ou ficam no acervo) |
| design R03 (HTML, fonte, testes, capturas) | `build/` (reproduzível), `qa-*/instance` (cópias completas do modpack, ~GB) |
| ativos de arte, fontes rasterizadas e SFX sintetizados | **`menu.ogg` / `BGM.mp3`** (terceiros), `local-assets/`, arquivos > 2,5 MB (listados com SHA em `evidence/LARGE-FILES.json`) |

Nada do modpack (JARs de terceiros, configs, saves, logs de jogo, contas) foi copiado. Foi feita varredura de segredos/dados pessoais antes do push (ver `docs/PRIVACY-SCAN.md`).

## Lacunas conhecidas

1. **A conversa do ChatGPT onde ocorreu o trabalho R02 → N11 (20–21/09) não existe no disco.** Só restam os artefatos e os documentos que ela gerou. Tudo que se sabe dela vem dos `.md`/`.json` do acervo (nível B/C). A sessão do Codex `01a0bcf6` guarda apenas a pergunta de gênese e a resposta de viabilidade (20/09 00:58–01:11).
2. **HTMLs R04 (`61be2364…`) e S01 (`c0158907…`)** foram entregues na conversa e nunca copiados ao PC; só os hashes sobreviveram. O **R02.1** (`32fdf021…`) e o **R03** estão em `docs/design/R03/`.
3. **Imagens de concept** (`catedral_proibida_menu_do_fim.png`, `04_passagem_industrial_pontes.png`) citadas no contrato **não foram localizadas**; em `Desktop\Codex` há imagens de 10/09 (p.ex. `THEENDOFFORBIDDEN.png`) cujo vínculo com o menu **não está confirmado** — não incluídas.
4. **Autoria da música** e termos de uso: ver OI-07.
5. **Quem instalou o quê no pack diário**: não atribuído (ver OI-14).
6. **Mobile launcher**: este repositório descreve só a relação (arte reaproveitada; entrega da apresentação ao mod). O código do launcher é outro projeto.
7. **Fontes TTF/OTF** nunca fizeram parte do mod (atlas rasterizados); não há licenças de fonte a declarar no JAR.

## Varredura das demais pastas de `Desktop\Codex` (24/10/2026)
Busca textual (`.md/.json/.txt` < 3 MB) por `AkashicCinematicMenu|CinematicMenu|akashiccinematic` em todas as ~130 pastas irmãs. Só três citam o menu, todas do **ThePatcher**:
* `…beta.9-MPS-Angelica` — `README-TESTE.txt` (23/09 16:45) manda fazer novo restart "para validar o AkashicCinematicMenu" (contexto de teste do Angelica/MPS que antecede a beta.10).
* `…beta10-Cinematic-Angelica` — o checkpoint/auditoria já usado neste repositório (`RawScope` reescrito; *state fence*).
* `…beta.11-Async-Forestry` — carrega o *delta* da beta.10 (`CinematicAngelicaCompatTransformer`, `CinematicAngelicaStateFence`, `AngelicaVSyncGuard`) em `beta10-delta/reports/final-build-audit.json`.
As betas 12–34, `Angelica-VAO-*`, `forbidden-mobile-launcher` etc. **não** citam o menu nos arquivos de texto varridos (JARs/binários não foram abertos). Pastas posteriores a 23/09 existem, mas nada indica nova versão do AkashicCinematicMenu depois da 0.7.0-alpha.4.

## Marcos de integridade usados

`evidence/source-manifest.sha256` (alpha.1), `SHA256SUMS.txt` do R03, `CHECKPOINT_*.json` com `jar_sha256`, `LINEAGE.md` (calculado em 24/10/2026 sobre os arquivos do PC).
