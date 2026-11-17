# N12 — Edição de Natal / 0.7.0-alpha.6

Troca completa da estética: arte de Natal do dono no lugar da catedral/anjo, em menu, loading (splash), transição, conexão e telas secundárias. Mantém "THE END OF FORBIDDEN", os botões no mesmo lugar e toda a lógica (fluxo, áudio, política de telas, transformers) intacta.

## Estado (QUALITY_CONTRACT)
* **Implementado:** sim.
* **Compilado:** só núcleo + pacote `render` (contra stubs). **JAR não gerado** nesta sessão (dependências SRG ausentes).
* **Testado:** suítes do núcleo PASSOU; amostras renderizadas pelo código real em rasterizador por software. **Não rodou no jogo.**
* **Aprovado pelo dono:** PENDENTE.

Detalhes: `CHECKPOINT_N12.json`, `docs/design/N12-natal/README.md`, `evidence/natal/`.

## Para o próximo passo
1. `python tools/build_native.py --classpath-json deps.json` (gera `dist/AkashicCinematicMenu-0.7.0-alpha.6.jar`; registrar o SHA-256 em `docs/LINEAGE.md`).
2. `tools/verify_wave2_alpha4.py` adaptado (probes de bytecode/GPU).
3. Cópia **isolada** do modpack: splash, menu (paralaxe, hover, neve, guirlanda), Opções/Mundos, conexão, Sair.
4. Atenção especial: primeiro quadro do menu (3 texturas de 2080×1196 carregadas sob demanda) e OI-01 (Angelica).
