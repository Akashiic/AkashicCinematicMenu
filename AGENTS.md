# AGENTS.md — regras para quem trabalhar neste repositório (humano ou IA)

Leia primeiro: `docs/HANDOFF.md`, `docs/OPEN-ISSUES.md`, `docs/CRONOLOGIA.md`.

## Regras inegociáveis (QUALITY_CONTRACT ACM-QUALITY-2026-09-20-v1)
1. **Implementado ≠ compilado ≠ testado ≠ aprovado pelo usuário ≠ homologado.** Use essas palavras com precisão.
2. **Autorrevisão não é auditoria independente.** Evidência ausente bloqueia a aprovação.
3. **Aprovação visual/sonora é só do dono** (Gabriel / Akashiic). Nunca marque como "aprovado".
4. **Nunca teste no pack diário.** QA somente em **cópia isolada** do modpack. Não altere o pack original.
5. **Preserve falhas como evidência** (logs, recibos JSON). Não apague nem "limpe" resultados vermelhos.
6. **Fail-open:** todo transformer ASM conta âncoras exatas; se divergir, devolve a classe original e registra.
7. Sem `sleep`, sem percentual fictício, sem thread nova no splash.
8. Tema apenas por *allow-list* exata de classes; não toque GUIs desconhecidas de outros mods.
9. Não "conserte" o mute do usuário (Música = 0 %).
10. **Não commite** JARs/zips (use Releases), `build/`, cópias do modpack, **`menu.ogg`/`BGM.mp3`** (terceiros), contas, tokens, logs de jogo com dados pessoais.

## Fluxo de trabalho
* Partir de `main` (= 0.7.0-alpha.4). Nova candidata ⇒ nova versão (`0.7.0-alpha.5`…), **nunca** sobrescrever artefato/hash existente.
* Atualizar: `src/`, `docs/CHANGELOG.md`, `docs/LINEAGE.md` (hash novo), `docs/CRONOLOGIA.md` (fase nova), `docs/OPEN-ISSUES.md`, e criar um `CHECKPOINT_*.json` novo (não sobrescrever os antigos).
* Antes do PR: `python tools/repo/run_core_gates.py` e preencher o checklist de `.github/PULL_REQUEST_TEMPLATE.md` com **PASSOU / FALHOU / NÃO EXECUTADO** — nunca omita o que não rodou.
* Mensagens de commit em português; marque fatos novos com [DOC]/[JAR]/[LOG]/[INF].

## Primeira tarefa sugerida
**OI-01** — reproduzir e corrigir o conflito `RawScope` × Angelica no próprio mod (ver `docs/OPEN-ISSUES.md`).
