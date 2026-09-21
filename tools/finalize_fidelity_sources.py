from pathlib import Path
import hashlib,json
R=Path(__file__).resolve().parents[1];java=R/'src/main/java/br/com/redeakashic/cinematic'
for name in ['client/CinematicMenu.java','bootstrap/SplashHooks.java']:
    p=java/name;s=p.read_text(encoding='utf-8');old='if(w<1||h<1)return;' if name.startswith('bootstrap') else 'if(w<1||h<1)return;'
    if old not in s:old='if(w<1||h<1)return;'
    assert old in s,(name,'dimension check changed');s=s.replace(old,'if(w<160||h<120)return;');p.write_text(s,encoding='utf-8',newline='\n')
p=java/'client/ConnectionScreenTheme.java';s=p.read_text(encoding='utf-8')
colours={'0x020907':'0x060911','0x06100C':'0x10141D','0x94AA7A':'0xC4AA7C','0xDDE8D1':'0xE8E4D8','0xC6D3C0':'0xACB2BB','0x213523':'0x30333A','0x101B14':'0x171C25','0xA2BC82':'0xC4AA7C','0xE3EDD7':'0xE8E4D8','0x738070':'0x767E89'}
for old,new in colours.items():assert old in s,old;s=s.replace(old,new)
p.write_text(s,encoding='utf-8',newline='\n')
# Retain the previous metadata as an archived asset format, not an active pixel renderer.
(R/'evidence/design-decisions.json').write_text(json.dumps({'navigation_width_ratio':.27,'background_mapping':'single top-anchored full-bleed cover','geometry':'framebuffer pixel native, live resize; no contain centered composite','typography':'nine bucket atlases, pixel-positioned glyphs; ornaments vector quads','source_scene_native_resolution':[1207,830],'scene_is_not_4K':'no synthetic detail claim','palette':'mobile-derived obsidian slate ivory muted brass and celeste','stall':'skip invalid surfaces and reuse GPU resources across resize; cannot draw when all rendering is stopped','previous_SFX_Bogo_connection_work':'preserved and requires repeat runtime tests','user_visual_approval':'pending new colour version'},indent=2),encoding='utf-8')
print('N07_SOURCE_REVIEW_READY',flush=True)
