from pathlib import Path
from PIL import Image
import json,hashlib,subprocess
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/akashiccinematic';old=R.parent/'native-n06/src/main/resources/assets/akashiccinematic';sha=lambda f:hashlib.sha256(f.read_bytes()).hexdigest();checks=0;atlases=[]
for p in (A/'native/fonts').glob('*.properties'):
    props=dict(l.split('=',1) for l in p.read_text().splitlines());im=Image.open(p.with_suffix('.png'));im.load();assert im.size==(int(props['width']),int(props['height']))
    for c in range(32,256):
        x,y,w,h,bx,by,advance=map(float,props[str(c)].split(','));assert x>=0 and y>=0 and w>=1 and h>=1 and x+w<=im.width and y+h<=im.height and advance>=0;checks+=1
    atlases.append({'file':p.name,'size':im.size,'nominal_pixels':props['size']})
oggs=list((A/'sounds').rglob('*.ogg'));assert len(oggs)==5 and all(sha(f)==sha(old/f.relative_to(A)) for f in oggs)
res=subprocess.run(['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.UiFeaturesTests'],capture_output=True,text=True,timeout=20);assert res.returncode==0,res.stdout+res.stderr
(R/'evidence/ui-features-retained.log').write_text(res.stdout+res.stderr,encoding='utf-8')
report={'glyph_bounds_checks':checks,'atlases':atlases,'SFX_unchanged_by_hash':len(oggs),'old_audio_analysis_source':'native-n06/evidence/audio-validation.json','scene_source_pixels':[1207,830],'no_claim_of_4K_scene_detail':True,'no_font_binary_files':not any(f.suffix.lower() in ['.ttf','.otf','.woff','.woff2'] for f in (R/'src').rglob('*')),'ui_policy_result':res.stdout.splitlines()[-1]}
(R/'evidence/resource-fidelity-audit.json').write_text(json.dumps(report,indent=2));print('RESOURCE_AUDIT',checks,'glyphs,',len(oggs),'SFX; ',res.stdout.splitlines()[-1],flush=True)
