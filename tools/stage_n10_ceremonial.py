from pathlib import Path
import hashlib,json,shutil,importlib.util,time
P=Path(__file__).resolve().parents[1];B=P/'candidates/native-n09-audio-fixed';R=P/'candidates/native-n10-ceremonial'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
base=B/'dist/AkashicCinematicMenu-0.5.2-alpha.1.jar';assert sha(base)=='da5bb5579545e9716590547fbf80efbfc05771f47dba38a62999cfa9e4c0460a'
assert not R.exists(),'Candidate already exists; inspect checkpoint, do not repeat setup'
R.mkdir();(R/'evidence').mkdir();(R/'docs').mkdir()
for n in ['src','tools']:shutil.copytree(B/n,R/n)
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.5.2-alpha.1' in s,name;p.write_text(s.replace('0.5.2-alpha.1','0.6.0-alpha.1'),encoding='utf-8',newline='\n')
mods=Path.home()/'AppData/Roaming/.technic/modpacks/forbidden-series/mods';observed=[]
for p in mods.glob('*CinematicMenu*.jar'):observed.append({'name':p.name,'sha256':sha(p)})
libs={name:bool(importlib.util.find_spec(name)) for name in ['PIL','numpy','scipy','cv2','soundfile']}
r={'baseline':str(base),'baseline_sha256':sha(base),'daily_observed':observed,'libraries':libs,'created':time.time(),'user_approved':'BGM functional; visual palette preserved except crimson-white PLAY; real-depth layers and liturgical SFX requested','user_bgm_bytes_must_remain_identical':True,'status':'ISOLATED_IN_DEVELOPMENT'}
(R/'evidence/baseline.json').write_text(json.dumps(r,indent=2),encoding='utf-8');print(json.dumps(r,indent=2),flush=True)
