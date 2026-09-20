from pathlib import Path
import json,hashlib,shutil,time,zipfile
P=Path(__file__).resolve().parents[1];old=P/'candidates/native-n06';R=P/'candidates/native-n07';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
assert not R.exists(),'Existing candidate must be inspected rather than overwritten'
jar=old/'dist/AkashicCinematicMenu-0.3.0-alpha.3.jar';assert sha(jar)=='5fdab2cf1b4d8b68876bf20550907fed6343f3349f78af4f0e0df045a0da3f06'
R.mkdir();(R/'evidence').mkdir();(R/'docs').mkdir()
for n in ['src','tools']:shutil.copytree(old/n,R/n)
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','tools/build_native.py','src/main/resources/META-INF/MANIFEST.MF']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.3.0-alpha.3' in s,name;p.write_text(s.replace('0.3.0-alpha.3','0.4.0-alpha.1'),encoding='utf-8',newline='\n')
(R/'evidence/baseline.json').write_text(json.dumps({'base':'native-n06','sha256':sha(jar),'base_runtime_not_promoted':True,'original_n04_intact':sha(P/'candidates/native-n04/dist/AkashicCinematicMenu-0.2.0-alpha.4.jar'),'started_at':time.time(),'user_scope':'responsive full-bleed, native-scale typography, mobile palette, stall recovery; retain targeted Bogo/SFX/secondary work'},indent=2))
print('ISOLATED_N07_PREPARED',R,flush=True)
