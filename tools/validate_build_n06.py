from pathlib import Path
import subprocess,sys,json,hashlib,zipfile
R=Path(__file__).resolve().parents[1];checks=[]
steps=[('core',[sys.executable,str(R/'tools/test_core.py')]),('connection-policy',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.ConnectionWatchTests']),('ui-policy',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.UiFeaturesTests']),('native-build',[sys.executable,str(R/'tools/build_native.py')])]
for name,args in steps:
    result=subprocess.run(args,capture_output=True,text=True,timeout=60);(R/'evidence'/(name+'-n06.log')).write_text(result.stdout+result.stderr,encoding='utf-8');checks.append({'name':name,'exit':result.returncode});print(name,result.returncode,(result.stdout+result.stderr)[-400:],flush=True);assert result.returncode==0,name
b=json.loads((R/'evidence/native-build.json').read_text());jar=R/'dist'/b['jar']
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
    manifest=json.loads(z.read('assets/akashiccinematic/sounds.json'));assert len(manifest)==5
    for name in manifest:assert 'assets/akashiccinematic/sounds/ui/'+name.split('.')[1]+'.ogg' in z.namelist()
(R/'evidence/build-gates-n06.json').write_text(json.dumps({'checks':checks,'candidate':b,'runtime':'PENDING'},indent=2));print(json.dumps(b),flush=True)
