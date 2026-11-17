#!/usr/bin/env python3
"""Roda tools/SecondaryTransformerProbe.java contra a GuiSlot/GuiScreen/GuiButton REAIS (Minecraft+Forge 1.7.10 do
RetroFuturaGradle), usando o deps.json da CI. Valida as ancoras exatas do SecondaryThemeTransformer antes do JAR."""
import json, subprocess, sys
from pathlib import Path
R = Path(__file__).resolve().parents[2]
deps = [Path(p) for p in json.loads((R / 'deps.json').read_text())]
pick = lambda key: next(str(p) for p in deps if key in p.name)
mc = pick('recompiled_minecraft')
cp = [pick('asm-tree'), next(str(p) for p in deps if p.name.startswith('asm-') and 'tree' not in p.name), pick('launchwrapper')]
out = R / 'build/transformer-probe'; out.mkdir(parents=True, exist_ok=True)
sep = ';' if sys.platform.startswith('win') else ':'
subprocess.run(['javac', '--release', '8', '-nowarn', '-cp', sep.join(cp), '-d', str(out),
                str(R / 'src/main/java/br/com/redeakashic/cinematic/bootstrap/SecondaryThemeTransformer.java'),
                str(R / 'tools/SecondaryTransformerProbe.java')], check=True)
r = subprocess.run(['java', '-cp', sep.join([str(out)] + cp), 'SecondaryTransformerProbe', mc], capture_output=True, text=True)
print(r.stdout + r.stderr)
(R / 'evidence').mkdir(exist_ok=True)
(R / 'evidence/transformer-probe-ci.log').write_text(r.stdout + r.stderr, encoding='utf-8')
sys.exit(r.returncode)
