#!/usr/bin/env python3
"""Gera uma copia do JAR SEM a faixa de terceiros (assets/akashiccinematic/sounds/bgm/menu.ogg).
Uso: python tools/repo/strip_bgm.py ENTRADA.jar SAIDA-nobgm.jar
O zip e regravado de forma reprodutivel (ordem original, data fixa). Imprime o novo SHA-256."""
import hashlib, sys, zipfile

TRACK = 'assets/akashiccinematic/sounds/bgm/menu.ogg'
src, dst = sys.argv[1], sys.argv[2]
removed = False
with zipfile.ZipFile(src) as zin, zipfile.ZipFile(dst, 'w', zipfile.ZIP_DEFLATED) as zout:
    for info in zin.infolist():
        if info.filename == TRACK:
            removed = True
            continue
        n = zipfile.ZipInfo(info.filename, (1980, 1, 1, 0, 0, 0))
        n.compress_type = zipfile.ZIP_DEFLATED
        n.external_attr = info.external_attr
        zout.writestr(n, zin.read(info.filename))
digest = hashlib.sha256(open(dst, 'rb').read()).hexdigest()
print(f'{"removida" if removed else "faixa nao presente"}: {TRACK}\n{digest}  {dst}')
