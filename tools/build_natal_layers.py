#!/usr/bin/env python3
"""Camadas de paralaxe da edicao de Natal (0.7.0-alpha.5).

Fonte unica: docs/design/N12-natal/source/natal-source.jpg (2000x1116, arte do dono).

Fases:
  python tools/build_natal_layers.py --segment   # gera as mascaras (exige rembg + modelo birefnet-general-lite)
  python tools/build_natal_layers.py             # gera as camadas a partir das mascaras versionadas

As mascaras ficam versionadas em docs/design/N12-natal/masks/ para que a fase de build seja
reproduzivel sem rede nem modelo de IA. Planos (mesmo crop neutro, deslocamentos limitados):
  0 room    - corredor opaco; areas sob os personagens reconstruidas so para o corredor de movimento
  1 chasers - os dois perseguidores (plano medio)
  2 hero    - a garota, a arvore e o lanca-misseis (primeiro plano)
"""
import argparse, hashlib, json, sys
from pathlib import Path
import numpy as np
from PIL import Image, ImageFilter
import cv2

R = Path(__file__).resolve().parents[1]
D = R / 'docs/design/N12-natal'
SRC = D / 'source/natal-source.jpg'
MASKS = D / 'masks'
A = R / 'src/main/resources/assets/akashiccinematic/native'
O = A / 'depth'
E = R / 'evidence/natal'
W, H, PAD = 2000, 1116, 40
HERO_CROP = (780, 0, 2000, 1116)
# Deslocamento maximo por plano, em pixels da fonte (espelha DepthCamera.DX/DY).
DX, DY = (1.5, 4.6, 13.3), (.75, 2.3, 6.6)
sha = lambda p: hashlib.sha256(Path(p).read_bytes()).hexdigest()


def segment():
    from rembg import remove, new_session  # dependencia apenas desta fase
    import rembg
    src = Image.open(SRC).convert('RGB')
    s = new_session('birefnet-general-lite')
    MASKS.mkdir(parents=True, exist_ok=True)
    remove(src, session=s, only_mask=True).save(MASKS / 'full.png')
    remove(src.crop(HERO_CROP), session=s, only_mask=True).save(MASKS / 'hero-crop.png')
    info = {'model': 'birefnet-general-lite', 'rembg': getattr(rembg, '__version__', '?'), 'source_sha256': sha(SRC),
            'hero_crop': HERO_CROP, 'files': {p.name: sha(p) for p in MASKS.glob('*.png')}}
    (MASKS / 'masks.json').write_text(json.dumps(info, indent=2), encoding='utf-8')
    print('MASKS_WRITTEN', info['files'], flush=True)


def split(full, hero_crop):
    """Separa perseguidores (componentes a esquerda) do heroi; o recorte do heroi recupera a estrela."""
    binary = (full > 96).astype('uint8')
    n, labels, stats, cent = cv2.connectedComponentsWithStats(binary, 8)
    chaser_ids = [i for i in range(1, n) if cent[i][0] < 980 and stats[i][cv2.CC_STAT_TOP] > 280 and stats[i][cv2.CC_STAT_AREA] > 400]
    chaser_zone = np.isin(labels, chaser_ids)
    chaser_zone = cv2.dilate(chaser_zone.astype('uint8'), np.ones((15, 15), np.uint8)) > 0
    chasers = np.where(chaser_zone, full, 0).astype('uint8')
    hero = np.where(chaser_zone, 0, full).astype('uint8')
    crop = np.zeros_like(full); x0, y0, x1, y1 = HERO_CROP; crop[y0:y1, x0:x1] = hero_crop
    crop[:, :x0 + 24] = 0  # borda artificial do recorte
    hero = np.maximum(hero, crop)
    return hero, chasers


def solid(matte, threshold=64):
    """Interior opaco (sem furos) e borda suave original: evita personagens translucidos."""
    b = (matte > threshold).astype('uint8') * 255
    b = cv2.morphologyEx(b, cv2.MORPH_CLOSE, np.ones((7, 7), np.uint8))
    ff = b.copy(); m = np.zeros((H + 2, W + 2), np.uint8); cv2.floodFill(ff, m, (0, 0), 255)
    filled = b | cv2.bitwise_not(ff)
    core = cv2.erode(filled, np.ones((5, 5), np.uint8))
    return np.maximum(matte, core).astype('uint8')


def fill_plate(rgb, hole):
    """Reconstrucao em multiescala: interior grosso (nunca visivel) + faixa fina Telea na borda."""
    small = cv2.resize(rgb, (W // 8, H // 8), interpolation=cv2.INTER_AREA)
    hs = cv2.resize(hole, (W // 8, H // 8), interpolation=cv2.INTER_NEAREST)
    hs = cv2.dilate(hs, np.ones((3, 3), np.uint8))
    coarse = cv2.inpaint(small, hs, 6, cv2.INPAINT_TELEA)
    coarse = cv2.resize(coarse, (W, H), interpolation=cv2.INTER_CUBIC)
    base = rgb.copy(); base[hole > 0] = coarse[hole > 0]
    band = cv2.dilate(hole, np.ones((5, 5), np.uint8)) & ~cv2.erode(hole, np.ones((61, 61), np.uint8))
    fine = cv2.inpaint(base, band, 7, cv2.INPAINT_TELEA)
    out = base.copy(); out[band > 0] = fine[band > 0]
    return out


def cover(rgb, plate, alpha):
    """Texels transparentes recebem a cor suave do fundo reconstruido: filtragem bilinear sem franja e PNG menor."""
    return np.where(alpha[..., None] > 0, rgb, plate).astype('uint8')


def padded(a, mode):
    return cv2.copyMakeBorder(a, PAD, PAD, PAD, PAD, mode)


def glass(rgba):
    """Vidro em meia resolucao: blur em cor pre-multiplicada (sem franja escura)."""
    a = rgba.astype(np.float32) / 255.
    a = cv2.resize(a, (a.shape[1] // 2, a.shape[0] // 2), interpolation=cv2.INTER_AREA)
    if a.shape[2] == 3:
        return np.uint8(np.clip(cv2.GaussianBlur(a, (0, 0), 6.5), 0, 1) * 255 + .5)
    pa = cv2.GaussianBlur(a[:, :, 3], (0, 0), 6.5)
    prgb = cv2.GaussianBlur(a[:, :, :3] * a[:, :, 3:4], (0, 0), 6.5) / np.maximum(pa[:, :, None], 1 / 65535)
    return np.uint8(np.clip(np.dstack([prgb, pa]), 0, 1) * 255 + .5)


def build():
    O.mkdir(exist_ok=True); E.mkdir(parents=True, exist_ok=True)
    src = Image.open(SRC).convert('RGB'); rgb = np.array(src); assert rgb.shape[:2] == (H, W), rgb.shape
    full = np.array(Image.open(MASKS / 'full.png').convert('L'))
    hero_crop = np.array(Image.open(MASKS / 'hero-crop.png').convert('L'))
    hero, chasers = split(full, hero_crop)
    hero, chasers = solid(hero), solid(chasers)
    hole = ((cv2.dilate(np.maximum(hero, chasers), np.ones((9, 9), np.uint8)) > 10) * 255).astype('uint8')
    room = fill_plate(rgb, hole)

    # Arquivos antigos (catedral/anjo) deixam de ser carregados; removidos para nao irem no JAR.
    for old in ['sky', 'architecture', 'angel', 'occluder']:
        for suffix in ['', '-blur']:
            p = O / f'{old}{suffix}.png'
            if p.exists(): p.unlink()
    rgba = {'room': padded(room, cv2.BORDER_REFLECT_101),
            'chasers': padded(np.dstack([cover(rgb, room, chasers), chasers]), cv2.BORDER_REPLICATE),
            'hero': padded(np.dstack([cover(rgb, room, hero), hero]), cv2.BORDER_REPLICATE)}
    Image.fromarray(rgba['room']).save(O / 'room.jpg', quality=94, subsampling=0)
    Image.fromarray(glass(rgba['room'])).save(O / 'room-blur.jpg', quality=92)
    for name in ['chasers', 'hero']:
        Image.fromarray(rgba[name]).save(O / f'{name}.png', optimize=True)
        Image.fromarray(glass(rgba[name])).save(O / f'{name}-blur.png', optimize=True)
    (O / 'layers.properties').write_text(
        f'sourceWidth={W}\nsourceHeight={H}\npad={PAD}\nplanes=room,chasers,hero\nmaxRelativeTravelX={DX[2]-DX[0]:.2f}\nmaxRelativeTravelY={DY[2]-DY[0]:.2f}\n',
        encoding='ascii')
    # Fundo estatico (splash, transicoes, telas secundarias): a propria arte, sem recompressao.
    (A / 'backdrop.jpg').write_bytes(SRC.read_bytes())
    Image.fromarray(glass(rgb)).save(A / 'backdrop-blur.jpg', quality=92)

    # Evidencias: revisao da segmentacao, composicao neutra e extremos de camera.
    review = rgb.astype(np.float32)
    review = review * (1 - hero[..., None] / 255. * .5) + np.array([210, 50, 80]) * hero[..., None] / 255. * .5
    review = review * (1 - chasers[..., None] / 255. * .5) + np.array([40, 120, 220]) * chasers[..., None] / 255. * .5
    Image.fromarray(review.astype('uint8')).save(E / 'segmentation-review.jpg', quality=92)
    for name, m in [('hero-mask', hero), ('chasers-mask', chasers), ('room-hole', hole)]:
        Image.fromarray(m).save(E / f'{name}.png', optimize=True)
    plates = [Image.fromarray(rgba[n]).convert('RGBA') for n in ['room', 'chasers', 'hero']]

    def compose(mx, my):
        out = Image.new('RGBA', (W, H))
        for i, p in enumerate(plates):
            x, y = PAD - DX[i] * mx, PAD - DY[i] * my
            out.alpha_composite(p.transform((W, H), Image.AFFINE, (1, 0, x, 0, 1, y), Image.BILINEAR))
        return out.convert('RGB')
    neutral = compose(0, 0); delta = np.abs(np.array(neutral).astype('int16') - rgb)
    frames = [compose(x, y) for x, y in [(-1, -1), (1, -1), (-1, 1), (1, 1)]]
    strip = Image.new('RGB', (1000, 558))
    for i, f in enumerate(frames): strip.paste(f.resize((500, 279)), ((i % 2) * 500, (i // 2) * 279))
    strip.save(E / 'extremes.jpg', quality=92)
    report = {
        'source': str(SRC.relative_to(R)), 'source_sha256': sha(SRC), 'source_size': [W, H], 'author': 'dono do projeto (declarado em 17/11/2026)',
        'masks': json.loads((MASKS / 'masks.json').read_text()) if (MASKS / 'masks.json').exists() else None,
        'planes': ['room', 'chasers', 'hero'], 'pad': PAD, 'max_offset_source_px': {'x': DX, 'y': DY},
        'neutral_mean_abs_rgb': delta.mean(axis=(0, 1)).tolist(), 'neutral_changed_pixels_gt3': float(np.mean(delta.max(axis=2) > 3)),
        'hero_pixels': int((hero > 128).sum()), 'chaser_pixels': int((chasers > 128).sum()),
        'room_fill': 'multiescala: Telea 1/8 no interior + Telea fino na borda; interior so aparece no corredor de movimento',
        'glass': 'meia resolucao, blur sigma 6.5 em cor pre-multiplicada',
        'files': {p.relative_to(A).as_posix(): {'bytes': p.stat().st_size, 'sha256': sha(p)} for p in sorted(O.glob('*')) + [A / 'backdrop.jpg', A / 'backdrop-blur.jpg']},
        'visual_gate': 'PENDENTE (aprovacao exclusiva do dono)'}
    (E / 'natal-layers.json').write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding='utf-8')
    print('NATAL_LAYERS', report['neutral_changed_pixels_gt3'], {k: v['bytes'] for k, v in report['files'].items()}, flush=True)


if __name__ == '__main__':
    ap = argparse.ArgumentParser(); ap.add_argument('--segment', action='store_true'); a = ap.parse_args()
    if a.segment: segment()
    else: build()
