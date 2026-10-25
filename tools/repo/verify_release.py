#!/usr/bin/env python3
"""Confere JARs de um diretorio contra releases/SHA256SUMS e checa que *-nobgm.jar nao contem a faixa.
Uso: python tools/repo/verify_release.py DIRETORIO_COM_JARS"""
import hashlib, sys, zipfile
from pathlib import Path

root = Path(__file__).resolve().parents[2]
jars = Path(sys.argv[1])
sums = {}
for line in (root / 'releases' / 'SHA256SUMS').read_text(encoding='utf-8').splitlines():
    if line.strip():
        h, name = line.split(None, 1)
        sums[name.strip().lstrip('*')] = h
ok = True
for name, h in sums.items():
    p = jars / name
    if not p.exists():
        print('AUSENTE ', name); ok = False; continue
    got = hashlib.sha256(p.read_bytes()).hexdigest()
    flag = 'OK      ' if got == h else 'DIVERGE '
    ok &= got == h
    if name.endswith('-nobgm.jar'):
        has = 'assets/akashiccinematic/sounds/bgm/menu.ogg' in zipfile.ZipFile(p).namelist()
        if has:
            flag, ok = 'TEM FAIXA', False
    print(flag, name)
sys.exit(0 if ok else 1)
