from pathlib import Path
import json,zipfile
R=Path(__file__).resolve().parents[1];p=R/'src/main/java/br/com/redeakashic/cinematic/render/NativeVisuals.java';s=p.read_text(encoding='utf-8')
old='Math.round(h*.91),GOLD,.14)';assert s.count(old)==1
backup=R/'evidence/source-alpha2-before-footer.zip';assert not backup.exists()
with zipfile.ZipFile(backup,'x',compression=zipfile.ZIP_DEFLATED) as z:
    for f in (R/'src/main/java').rglob('*.java'):z.write(f,f.relative_to(R).as_posix())
p.write_text(s.replace(old,'Math.round(h*.93),GOLD,.14)'),encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','tools/build_native.py','src/main/resources/META-INF/MANIFEST.MF']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.4.0-alpha.2' in s;p.write_text(s.replace('0.4.0-alpha.2','0.4.0-alpha.3'),encoding='utf-8',newline='\n')
checks=[]
for h in [480,600,768,800,1001,1080,1440,2160]:
    size=max(9,min(2.1,h/768)*7);bottom=h*.947+size;frame=h*(.045+.93);assert bottom<frame;checks.append({'height':h,'text_bottom_upper_bound':bottom,'frame_bottom':frame})
(R/'evidence/footer-inspection-fix.json').write_text(json.dumps({'observed':'Compact-menu decorative bottom border crossed the final footer line','correction':'Move only the border bottom from95.5% to97.5%; do not shift content or hitboxes','version':'0.4.0-alpha.3','checks':checks,'previous_source':str(backup)},indent=2))
print('FOOTER_VISUAL_DEFECT_CORRECTED; isolated alpha.3 ready to rebuild',flush=True)
