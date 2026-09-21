from pathlib import Path
import zipfile,json,hashlib
R=Path(__file__).resolve().parents[1];E=R/'evidence';backup=E/'alpha1-source-before-legibility.zip';assert not backup.exists()
with zipfile.ZipFile(backup,'x',compression=zipfile.ZIP_DEFLATED) as z:
    for f in (R/'src/main/java').rglob('*.java'):z.write(f,f.relative_to(R).as_posix())
p=R/'src/main/java/br/com/redeakashic/cinematic/render/UiTypography.java';s=p.read_text();anchor='    public double width(String s,boolean serif,double size,double tracking){Face f=face(serif,size);';assert s.count(anchor)==1
s=s.replace(anchor,'    public double width(String s,boolean serif,double size,double tracking){size=Math.max(serif?12:9,size);Face f=face(serif,size);')
anchor='        double measured=width(text,serif,size,tracking);';assert s.count(anchor)==1
s=s.replace(anchor,'        size=Math.max(serif?12:9,size);\n'+anchor);p.write_text(s,encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','tools/build_native.py','src/main/resources/META-INF/MANIFEST.MF']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.4.0-alpha.1' in s,name;p.write_text(s.replace('0.4.0-alpha.1','0.4.0-alpha.2'),encoding='utf-8',newline='\n')
(E/'alpha1-visual-review.json').write_text(json.dumps({'verdict':'REJECT_SMALL_METADATA_LEGIBILITY','maximized':'full-bleed and new palette visible; no old contain gap','compact':'metadata at 5-7 physical pixels too small','fix':'minimum nominal body9px / serif12px before width-fit','source_backup':str(backup),'runtime_jar_alpha1_preserved':True},indent=2))
print('COMPACT_REVIEW_FIX_READY; earlier artifact remains unchanged',flush=True)
