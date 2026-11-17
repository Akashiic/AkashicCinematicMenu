#!/usr/bin/env python3
"""Recolore as texturas das telas secundarias para a edicao de Natal, a partir dos originais da alpha.4
(docs/design/N12-natal/source/gui-alpha4/). Deterministico e idempotente.
Layout vanilla de widgets.png: y 46-65 desabilitado, 66-85 normal, 86-105 hover (200 px de largura)."""
import hashlib, json
from pathlib import Path
import numpy as np
from PIL import Image
R = Path(__file__).resolve().parents[1]
SRC = R / 'docs/design/N12-natal/source/gui-alpha4'
OUT = R / 'src/main/resources/assets/akashiccinematic/textures/gui'
GOLD, RED = np.array([233, 191, 99]), np.array([200, 40, 60])
sha = lambda p: hashlib.sha256(Path(p).read_bytes()).hexdigest()

def warm_dark(px):
    """Grafite azulado -> cafe: troca R/B e puxa levemente para o vermelho."""
    r, g, b = px[..., 0].astype(float), px[..., 1].astype(float), px[..., 2].astype(float)
    return np.stack([b * 1.08 + 2, g * .92, r * .78], -1)

w = np.array(Image.open(SRC / 'widgets_forbidden.png').convert('RGBA')); out = w.copy().astype(float)
region = out[46:106, 0:200]
rgb = region[..., :3]; lum = rgb @ np.array([.2126, .7152, .0722]); opaque = region[..., 3] > 200
goldish = opaque & (rgb[..., 0] > rgb[..., 2] + 30)
dark = opaque & (lum < 70) & ~goldish
rgb[dark] = warm_dark(rgb[dark])
rgb[goldish] = GOLD * (lum[goldish] / 190.)[:, None]
region[..., :3] = rgb
out[46:106, 0:200] = region
out[87:105, 1:3, :3] = RED; out[87:105, 1:3, 3] = 255  # friso vermelho a esquerda no hover
w2 = np.clip(out + .5, 0, 255).astype('uint8')
Image.fromarray(w2).save(OUT / 'widgets_forbidden.png', optimize=True)

l = np.array(Image.open(SRC / 'list_surface.png').convert('RGBA')).astype(float)
l[..., :3] *= np.array([1.0, .93, .84])
Image.fromarray(np.clip(l + .5, 0, 255).astype('uint8')).save(OUT / 'list_surface.png', optimize=True)
report = {p.name: {'source_sha256': sha(SRC / p.name), 'sha256': sha(p)} for p in OUT.glob('*.png')}
(R / 'evidence/natal').mkdir(parents=True, exist_ok=True)
(R / 'evidence/natal/natal-gui.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
print('NATAL_GUI', report)
