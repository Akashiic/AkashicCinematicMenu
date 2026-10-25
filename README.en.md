# AkashicCinematicMenu

A native (Java + LWJGL 2 / OpenGL) **cinematic main menu and startup overlay** for **Minecraft 1.7.10 + Forge 10.13.4.1614 (Java 8)**, built for the Akashic / "Forbidden Series" (TEOF) modpack. Client-side only.

* **Latest state:** `0.7.0-alpha.4` — passed directed tests on the full pack; **owner's visual approval still pending**.
* **Type:** FMLCorePlugin (fail-open ASM coremod) + mod. Modid `akashiccinematicmenu`.
* **License:** not yet defined (see `docs/OPEN-ISSUES.md`, OI-08) — all rights reserved until the owner decides.
* **Docs are written in Portuguese** (the owner's language). Start here:
  1. `docs/HANDOFF.md` — state, build/test, next steps
  2. `docs/CRONOLOGIA.md` + `docs/FLUXOGRAMA.md` — full history and Mermaid diagrams
  3. `docs/OPEN-ISSUES.md` — open issues (main one: `RawScope` vs. Angelica GLSM, OI-01)
  4. `docs/ARCHITECTURE.md`, `docs/LINEAGE.md` (JAR hashes), `docs/PROVENANCE.md`
  5. `AGENTS.md` — working rules

**Git history is reconstructed** (one commit per candidate build; author date = JAR mtime) because the original work was never under version control. Tags `v0.1.0-alpha.1 … v0.7.0-alpha.4`.

**Third-party audio:** the menu music track (`menu.ogg`, by a third-party artist) is kept in the repository and in the official JARs; per the owner, the permission to use it was obtained by another contributor (the permission itself is not in the repo — see `docs/OPEN-ISSUES.md`, OI-07).

Build: `python tools/build_native.py --classpath-json deps.json` (dependencies are not redistributed; see `docs/HANDOFF.md`).
