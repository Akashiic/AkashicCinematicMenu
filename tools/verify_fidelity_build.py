from pathlib import Path
import json,hashlib,subprocess,sys,zipfile
R=Path(__file__).resolve().parents[1];E=R/'evidence';steps=[]
commands=[('core',[sys.executable,str(R/'tools/test_core.py')]),('geometry',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.PixelLayoutTests']),('connection',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.ConnectionWatchTests']),('build',[sys.executable,str(R/'tools/build_native.py')])]
for label,argv in commands:
    res=subprocess.run(argv,capture_output=True,text=True,timeout=80);(E/(label+'-fidelity.log')).write_text(res.stdout+res.stderr,encoding='utf-8');steps.append({'step':label,'exit':res.returncode});print(label,res.returncode,(res.stdout+res.stderr)[-240:],flush=True)
    assert res.returncode==0,label
b=json.loads((E/'native-build.json').read_text());jar=R/'dist'/b['jar'];sha=hashlib.sha256(jar.read_bytes()).hexdigest();assert sha==b['sha256']
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;assert not any(n.endswith(('.ttf','.otf','.woff','.woff2')) or 'cinematicqa' in n for n in z.namelist());assert 'br/com/redeakashic/cinematic/render/UiTypography.class' in z.namelist()
(E/'fidelity-build-gates.json').write_text(json.dumps({'build':b,'steps':steps,'runtime':'NOT_YET_TESTED'},indent=2));print('CANDIDATE',json.dumps(b),flush=True)
