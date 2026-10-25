#!/usr/bin/env python3
"""Gate do NUCLEO puro: javac --release 8 -Werror + CoreTests. Nao exige Minecraft/Forge.
Uso: python tools/repo/run_core_gates.py  (da raiz do repositorio)"""
import subprocess, sys
from pathlib import Path

root = Path(__file__).resolve().parents[2]
sys.exit(subprocess.run([sys.executable, str(root / 'tools' / 'test_core.py')], cwd=root).returncode)
