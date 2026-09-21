from pathlib import Path
import subprocess,json,hashlib,sys,zipfile
R=Path(__file__).resolve().parents[1];E=R/'evidence';results=[]
steps=[('core',[sys.executable,str(R/'tools/test_core.py')])]
for name in ['MenuMusicTests','ConnectionWatchTests','PixelLayoutTests','UiFeaturesTests']:steps.append((name,['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.'+name]))
steps.append(('build',[sys.executable,str(R/'tools/build_native.py')]))
for name,command in steps:
    run=subprocess.run(command,capture_output=True,text=True,timeout=80);(E/(name+'-n08.log')).write_text(run.stdout+run.stderr,encoding='utf-8');results.append({'test':name,'exit':run.returncode});print(name,run.returncode,(run.stdout+run.stderr)[-1400:],flush=True)
    assert run.returncode==0,name
b=json.loads((E/'native-build.json').read_text());jar=R/'dist'/b['jar'];assert hashlib.sha256(jar.read_bytes()).hexdigest()==b['sha256']
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;assert not any('cinematicqa' in n or n.endswith(('.mp3','.ttf','.otf','.woff','.woff2')) for n in z.namelist());assert 'assets/akashiccinematic/sounds/bgm/menu.ogg' not in z.namelist()
(E/'build-gates-n08.json').write_text(json.dumps({'results':results,'jar':b,'track_external':True,'real_runtime':'PENDING'},indent=2));print('BUILD_VERIFIED',b['sha256'],flush=True)
