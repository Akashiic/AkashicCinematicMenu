from pathlib import Path
import hashlib,json,shutil,time
P=Path(__file__).resolve().parents[1]
B=P/'candidates/native-n10-ceremonial'
R=P/'candidates/native-n11-gui-wave2'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
base=B/'dist/AkashicCinematicMenu-0.6.0-alpha.3.jar'
assert sha(base)=='a15e18a206e89aa7736d71cae22813ecb4be0e7cb0dffd75d4c10e0939785859'
assert not R.exists(),'Existing N11 must be inspected, not overwritten'
R.mkdir();(R/'evidence').mkdir();(R/'docs').mkdir()
for n in ['src','tools']:shutil.copytree(B/n,R/n)
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java',
             'src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF',
             'tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8')
    assert '0.6.0-alpha.3' in s,name
    p.write_text(s.replace('0.6.0-alpha.3','0.7.0-alpha.1'),encoding='utf-8',newline='\n')
daily=Path.home()/'AppData/Roaming/.technic/modpacks/forbidden-series/mods'
observed=[{'name':p.name,'sha256':sha(p)} for p in daily.glob('*CinematicMenu*.jar')]
report={'baseline':'native-n10-ceremonial','baseline_sha256':sha(base),
        'version':'0.7.0-alpha.1','daily_observed':observed,
        'scope':['Options','Singleplayer','Create World','Language','Resource Packs',
                 'Controls','Video','Sound','Chat','Snooper','Flat World','Rename World'],
        'quit_confirmation':'remove only main-menu quit confirmation',
        'destructive_confirmations':'preserve delete-world and other irreversible confirmations',
        'created':time.time(),'status':'ISOLATED_IN_DEVELOPMENT'}
(R/'evidence/baseline.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print(json.dumps(report,indent=2),flush=True)
