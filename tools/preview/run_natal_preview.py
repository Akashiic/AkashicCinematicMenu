#!/usr/bin/env python3
"""Compila o pacote render + core contra os stubs de software e gera as amostras da edicao de Natal.

Uso: python tools/preview/run_natal_preview.py [pasta-de-saida]   (padrao: build/preview-natal)
Nao exige Minecraft/Forge/GPU. NAO substitui o teste no jogo: RawScope, Angelica e drivers nao sao exercitados.
"""
import subprocess, sys
from pathlib import Path

R = Path(__file__).resolve().parents[2]
J = R / 'src/main/java/br/com/redeakashic/cinematic'
out_classes = R / 'build/preview-classes'
dest = Path(sys.argv[1]) if len(sys.argv) > 1 else R / 'build/preview-natal'
render = ['NativeVisuals', 'BackdropRenderer', 'NativeLayout', 'NativeTexture', 'UiTypography', 'HeroButton', 'SecondaryFrameRenderer']
files = [J / 'render' / f'{n}.java' for n in render] + sorted((J / 'core').glob('*.java'))
files += sorted((R / 'tools/preview/stubs').rglob('*.java')) + [R / 'tools/preview/NatalPreview.java']
out_classes.mkdir(parents=True, exist_ok=True)
subprocess.run(['javac', '--release', '8', '-encoding', 'UTF-8', '-nowarn', '-d', str(out_classes)] + [str(f) for f in files], check=True)
sep = ';' if sys.platform.startswith('win') else ':'
subprocess.run(['java', '-Xmx3g', '-cp', f'{out_classes}{sep}{R / "src/main/resources"}', 'NatalPreview', str(dest)], check=True)
