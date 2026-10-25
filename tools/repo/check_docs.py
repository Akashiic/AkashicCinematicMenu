#!/usr/bin/env python3
"""Checagem leve dos docs: blocos ``` fechados e links relativos existentes."""
import re, sys
from pathlib import Path

root = Path(__file__).resolve().parents[2]
bad = []
for md in list(root.glob('*.md')) + list((root / 'docs').rglob('*.md')):
    text = md.read_text(encoding='utf-8')
    if text.count('```') % 2:
        bad.append(f'{md.relative_to(root)}: bloco ``` nao fechado')
    for m in re.finditer(r'\]\((?!https?:|#|\.\./\.\./)([^)#\s]+)', text):
        target = (md.parent / m.group(1)).resolve()
        if not target.exists():
            bad.append(f'{md.relative_to(root)}: link quebrado -> {m.group(1)}')
print('\n'.join(bad) or 'docs ok')
sys.exit(1 if bad else 0)
