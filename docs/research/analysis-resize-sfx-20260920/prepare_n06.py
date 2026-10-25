from pathlib import Path
import hashlib,json,shutil
P=Path(__file__).resolve().parents[1];old=P/'candidates/native-n04';new=P/'candidates/native-n06'
assert not new.exists(),'Existing candidate requires inspection, not replay'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();source=old/'dist/AkashicCinematicMenu-0.2.0-alpha.4.jar'
assert sha(source)=='3adfd2cc102eed60666d5594afc0a375e2a1fc7047ed409c7afc81c5969c190e'
new.mkdir();(new/'evidence').mkdir();(new/'tools').mkdir()
shutil.copytree(old/'src',new/'src')
for n in ['build_native.py','test_core.py','export_art.py']:shutil.copy2(old/'tools'/n,new/'tools'/n)
for n in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','tools/build_native.py','src/main/resources/META-INF/MANIFEST.MF']:
    p=new/n;s=p.read_text(encoding='utf-8');assert '0.2.0-alpha.4' in s;p.write_text(s.replace('0.2.0-alpha.4','0.3.0-alpha.1'),encoding='utf-8',newline='\n')
(new/'evidence/baseline.json').write_text(json.dumps({'baseline_jar':sha(source),'baseline':'native-n04','unused_partial_n05':True,'scope':['splash resize','targeted Bogo warning','UI audio','connecting and disconnect visuals'],'phase':'ANALYSIS_COMPLETED_IMPLEMENTATION_STARTED','runtime_approved':False},indent=2))
print('N06_WORKSPACE_CREATED',new,flush=True)
