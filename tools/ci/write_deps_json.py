#!/usr/bin/env python3
"""Monta deps.json (ordem exigida por tools/build_native.py) a partir das saidas de tools/ci/rfg-deps.

O RetroFuturaGradle pode substituir versoes de bibliotecas do Minecraft (ex.: log4j 2.0-beta9 ->
2.0-beta9-fixed da Mojang). Por isso os padroes aceitam sufixos/versoes trocadas; a versao realmente
usada e impressa e fica registrada em evidence/native-classpath.json pelo build_native.py."""
import json, sys
from pathlib import Path
d = Path(sys.argv[1] if len(sys.argv) > 1 else 'tools/ci/rfg-deps/build').resolve()
rfg, libs = d / 'rfg', d / 'acm-deps'

def one(folder, pattern, expected=None):
    found = sorted(p for p in folder.glob(pattern) if p.is_file())
    if len(found) != 1:
        listing = sorted(p.name for p in folder.iterdir()) if folder.is_dir() else 'pasta inexistente'
        raise SystemExit(f'esperado 1 arquivo {pattern} em {folder}; achados {[p.name for p in found]}; conteudo: {listing}')
    if expected and found[0].name != expected:
        print(f'AVISO: {expected} substituido por {found[0].name}', flush=True)
    return str(found[0])

deps = [one(rfg, 'srg_merged_minecraft.jar'), one(rfg, 'recompiled_minecraft*.jar'),
        one(libs, 'lwjgl-2*.jar', 'lwjgl-2.9.1.jar'),
        one(libs, 'log4j-api-*.jar', 'log4j-api-2.0-beta9.jar'),
        one(libs, 'asm-[0-9]*.jar', 'asm-9.8.jar'),
        one(libs, 'asm-tree-*.jar', 'asm-tree-9.8.jar'),
        one(libs, 'launchwrapper-*.jar', 'launchwrapper-1.12.jar'),
        one(libs, 'netty-all-*.jar', 'netty-all-4.0.10.Final.jar')]
Path('deps.json').write_text(json.dumps(deps, indent=2))
print('\n'.join(deps))
