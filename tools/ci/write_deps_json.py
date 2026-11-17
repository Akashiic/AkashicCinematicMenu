#!/usr/bin/env python3
"""Monta deps.json (ordem exigida por tools/build_native.py) a partir das saidas de tools/ci/rfg-deps."""
import json, sys
from pathlib import Path
d = Path(sys.argv[1] if len(sys.argv) > 1 else 'tools/ci/rfg-deps/build').resolve()
rfg, libs = d / 'rfg', d / 'acm-deps'
def one(folder, pattern):
    found = sorted(folder.glob(pattern))
    if len(found) != 1: raise SystemExit(f'esperado 1 arquivo {pattern} em {folder}, achados: {[p.name for p in found]}')
    return str(found[0])
deps = [one(rfg, 'srg_merged_minecraft.jar'), one(rfg, 'recompiled_minecraft*.jar'),
        one(libs, 'lwjgl-2.9.1.jar'), one(libs, 'log4j-api-2.0-beta9.jar'), one(libs, 'asm-9.8.jar'),
        one(libs, 'asm-tree-9.8.jar'), one(libs, 'launchwrapper-*.jar'), one(libs, 'netty-all-4.0.10.Final.jar')]
Path('deps.json').write_text(json.dumps(deps, indent=2))
print('\n'.join(deps))
