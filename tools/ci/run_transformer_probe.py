#!/usr/bin/env python3
"""Roda tools/SecondaryTransformerProbe.java contra as classes REAIS do Minecraft 1.7.10, usando o deps.json da CI.

* `srg_merged_minecraft.jar`: bytecode original do vanilla com nomes SRG (os que o transformer vê em runtime).
* Também roda contra o `recompiled_minecraft*.jar` (com os patches do Forge, nomes MCP): o Forge 1.7.10 tira o fundo
  da lista de `drawScreen` para `drawContainerBackground`, e é essa a estrutura que o jogo tem. As duas precisam passar.
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
log.append('FORGE_GUISLOT_HAS_DRAWCONTAINERBACKGROUND=' + str(moved))
c = run(['javac', '--release', '8', '-nowarn', '-cp', sep.join(cp), '-d', str(out),
         str(R / 'src/main/java/br/com/redeakashic/cinematic/bootstrap/SecondaryThemeTransformer.java'),
         str(R / 'tools/SecondaryTransformerProbe.java')])
results = [run(['java', '-cp', sep.join([str(out)] + cp), 'SecondaryTransformerProbe', jar]) for jar in (srg, forge)] if c.returncode == 0 else []
text = '\n'.join(log)
print(text)
(R / 'evidence').mkdir(exist_ok=True)
(R / 'evidence/transformer-probe-ci.log').write_text(text, encoding='utf-8')
if forge_slot.returncode != 0 or c.returncode != 0 or len(results) != 2 or any(r.returncode != 0 for r in results):
    sys.exit(1)
