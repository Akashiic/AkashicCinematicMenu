#!/usr/bin/env python3
"""Roda tools/SecondaryTransformerProbe.java contra as classes REAIS do Minecraft 1.7.10, usando o deps.json da CI.

* A sonda roda contra `srg_merged_minecraft.jar`: bytecode original com nomes SRG, os mesmos que o transformer vê
  em runtime (o `recompiled_minecraft*.jar` do RFG tem nomes MCP, ex. `optionsBackground`, e não serve de alvo).
* Guarda contra o Forge: a `GuiSlot` do jar com os patches do Forge (`recompiled`) é listada; se o Forge tiver tirado
  o fundo da lista de `drawScreen` (ex. `drawContainerBackground`), as âncoras do vanilla não valeriam no jogo e a
  sonda falha.
Log em evidence/transformer-probe-ci.log (artifact da CI)."""
import json, subprocess, sys
from pathlib import Path
R = Path(__file__).resolve().parents[2]
deps = [Path(p) for p in json.loads((R / 'deps.json').read_text())]
pick = lambda key: next(str(p) for p in deps if key in p.name)
srg, forge = pick('srg_merged_minecraft'), pick('recompiled_minecraft')
cp = [pick('asm-tree'), next(str(p) for p in deps if p.name.startswith('asm-') and 'tree' not in p.name), pick('launchwrapper')]
out = R / 'build/transformer-probe'; out.mkdir(parents=True, exist_ok=True)
sep = ';' if sys.platform.startswith('win') else ':'
log = []
def run(args, **kw):
    r = subprocess.run(args, capture_output=True, text=True, **kw)
    log.append('$ ' + ' '.join(Path(a).name if a.endswith('.jar') else a for a in args) + '\n' + r.stdout + r.stderr)
    return r
forge_slot = run(['javap', '-p', '-classpath', forge, 'net.minecraft.client.gui.GuiSlot'])
moved = 'drawContainerBackground' in forge_slot.stdout
log.append('FORGE_GUISLOT_MOVES_BACKGROUND=' + str(moved))
c = run(['javac', '--release', '8', '-nowarn', '-cp', sep.join(cp), '-d', str(out),
         str(R / 'src/main/java/br/com/redeakashic/cinematic/bootstrap/SecondaryThemeTransformer.java'),
         str(R / 'tools/SecondaryTransformerProbe.java')])
p = run(['java', '-cp', sep.join([str(out)] + cp), 'SecondaryTransformerProbe', srg]) if c.returncode == 0 else None
text = '\n'.join(log)
print(text)
(R / 'evidence').mkdir(exist_ok=True)
(R / 'evidence/transformer-probe-ci.log').write_text(text, encoding='utf-8')
if forge_slot.returncode != 0 or moved or c.returncode != 0 or p is None or p.returncode != 0:
    sys.exit(1)
