# AkashicCinematicMenu

Menu principal e tela de abertura **cinematográficos e nativos** (Java + LWJGL 2 / OpenGL) para **Minecraft 1.7.10 + Forge 10.13.4.1614 (Java 8)**, criados para o modpack Akashic / "Forbidden Series" (TEOF). Cliente apenas.

| | |
|---|---|
| Último estado | **0.7.0-alpha.4** (21/09/2026) — testada no pack completo; **aprovação visual do dono pendente** |
| Modid / pacote | `akashiccinematicmenu` / `br.com.redeakashic.cinematic` |
| Tipo | FMLCorePlugin (coremod ASM, *fail-open*) + mod |
| Ambiente-alvo | Angelica 2.2.18, Reese Sodium, RTG, Technic |
| Licença | **ainda não definida** — ver `docs/OPEN-ISSUES.md` (OI-08) |

> 🌐 English summary: [README.en.md](README.en.md)

## O que ele faz
* Substitui o menu principal por **"Santuário"**: céu, arquitetura e anjo em parallax 2.5D, botão **Jogar** carmesim (conecta direto a `br2.xmxcloud.net:10511`), Singleplayer, Opções, Sair.
* Substitui a tela de carregamento por **"O Despertar"**, com progresso real do Forge (sem percentuais fictícios).
* Estende o tema a 17 GUIs secundárias por *allow-list* exata (GUIs de outros mods ficam intactas).
* Música de menu via `SoundHandler` e 6 SFX rituais sintetizados.

## ▶ Comece por aqui (quem vai continuar o trabalho)
1. **[docs/HANDOFF.md](docs/HANDOFF.md)** — estado, pré-requisitos, como compilar, testar e o que fazer em seguida.
2. **[docs/CRONOLOGIA.md](docs/CRONOLOGIA.md)** — história completa, da primeira à última versão, com fluxograma.
3. **[docs/FLUXOGRAMA.md](docs/FLUXOGRAMA.md)** — 6 diagramas (linhagem, governança, boot, Jogar, áudio, ecossistema).
4. **[docs/OPEN-ISSUES.md](docs/OPEN-ISSUES.md)** — pendências (principal: OI-01 *RawScope × Angelica*).
5. **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — pacotes, transformers, configs.
6. **[docs/LINEAGE.md](docs/LINEAGE.md)** — tabela de JARs com SHA-256 e parentesco.
7. **[docs/PROVENANCE.md](docs/PROVENANCE.md)** — de onde vem cada fato, lacunas e **como o histórico git foi reconstruído**.
8. **[AGENTS.md](AGENTS.md)** — regras de trabalho para agentes de IA / contribuidores.

## Estrutura
```
src/                 código-fonte da ÚLTIMA candidata (main = 0.7.0-alpha.4)
tools/               scripts da última candidata (build_native.py, test_core.py, verify_*.py) + tools/repo/*
docs/                documentação (acima) + docs/reviews (pareceres originais), docs/design/R03, docs/research
checkpoints/<id>/    CHECKPOINT_*.json e README de cada candidata (+ CURRENT_STATUS.json defasado)
evidence/<id>/       recibos JSON, logs, capturas pequenas, zips de fonte intermediários; LARGE-FILES.json
candidate-docs/<id>/ docs/ e research/ que acompanhavam cada candidata
history/             scripts de transferência usados para levar código ao PC
releases/            RELEASES.md e SHA256SUMS
```
O fonte (`src/`, `tools/`) de **cada** candidata está em seu commit/tag — `git checkout v0.4.0-alpha.3` etc.

## Histórico git e tags
O dono não usava git; o histórico foi **reconstruído** (um commit por candidata, data do autor = *mtime* do JAR). Cada versão tem uma tag `v0.1.0-alpha.1 … v0.7.0-alpha.4`. Ramos `experiment/n05-blocked`, `variant/n08-partial`, `variant/n08-bgm-6c79c577` guardam caminhos não promovidos. Detalhes em `docs/PROVENANCE.md`.

## JARs
Estão em **[Releases](../../releases)** (`v0.7.0-alpha.4` = Latest; os demais são pré-lançamentos). Os JARs de 0.5.2+ **incluem a faixa de música `menu.ogg`** (de terceiros, mantida com a permissão obtida por outro contribuinte — ver `docs/OPEN-ISSUES.md` OI-07). Hashes: `releases/SHA256SUMS` e `docs/LINEAGE.md`.

## Compilar (resumo)
```
python tools/build_native.py --classpath-json deps.json
python tools/test_core.py
```
As dependências (Forge/MC SRG, LaunchWrapper, Angelica…) **não são redistribuídas**; veja `docs/HANDOFF.md` para montar o `deps.json`.

## Regra cardinal
*Implementado ≠ compilado ≠ testado ≠ aprovado pelo usuário ≠ homologado.* Nenhum agente declara "aprovado" sem a palavra do dono. QA somente em cópia isolada do modpack.
