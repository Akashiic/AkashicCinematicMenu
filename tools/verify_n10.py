from pathlib import Path
import os,json,subprocess,sys,hashlib,zipfile
R=Path(__file__).resolve().parents[1];B=R.parent/'native-n09-audio-fixed';E=R/'evidence';env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m';steps=[]
commands=[('core',[sys.executable,str(R/'tools/test_core.py')])]
for name in ['CeremonialTests','PixelLayoutTests','UiFeaturesTests','MusicCoreTests','BgmPlaybackTests','MenuSoundGateTests']:
    commands.append((name,['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.'+name]))
commands.append(('native-build',[sys.executable,str(R/'tools/build_native.py')]))
for name,args in commands:
    p=subprocess.run(args,capture_output=True,text=True,timeout=85,env=env);(E/(name+'-n10.log')).write_text(p.stdout+p.stderr,encoding='utf-8');steps.append({'step':name,'exit':p.returncode,'tail':p.stdout[-200:]});print(name,p.returncode,(p.stdout+p.stderr)[-280:],flush=True);assert p.returncode==0,name
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();bgm='src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg';assert sha(R/bgm)==sha(B/bgm)
for name in ['client/MenuBgm.java','client/MenuBgmSettings.java','core/BgmPlayback.java','core/MenuSoundGate.java']:
    path='src/main/java/br/com/redeakashic/cinematic/'+name;assert sha(R/path)==sha(B/path),name
b=json.loads((E/'native-build.json').read_text());jar=R/'dist'/b['jar']
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
report={'build':b,'tests':steps,'BGM_bytes_sha256':sha(R/bgm),'BGM_code_unchanged':True,'runtime':'NOT_YET_EXECUTED','baseline_sha256':sha(B/'dist/AkashicCinematicMenu-0.5.2-alpha.1.jar')}
(E/'build-audit.json').write_text(json.dumps(report,indent=2));print('CANDIDATE_BUILT',b['sha256'],b['bytes'],flush=True)
