from pathlib import Path
import subprocess,sys,json,hashlib,zipfile
r=Path(__file__).resolve().parents[1];old=r.parent/'native-n02';checks=[]
steps=[('core-retest',[sys.executable,str(r/'tools/test_core.py')]),('connection-policy',['java','-ea','-cp',str(r/'build/core-tests'),'br.com.redeakashic.cinematic.core.ConnectionWatchTests']),('native-build',[sys.executable,str(r/'tools/build_native.py')])]
for label,args in steps:
    res=subprocess.run(args,capture_output=True,text=True,timeout=60);(r/'evidence'/(label+'-n03.log')).write_text(res.stdout+res.stderr,encoding='utf-8');print(label,res.returncode,(res.stdout+res.stderr)[-180:],flush=True);assert res.returncode==0,label;checks.append({'test':label,'passed':True})
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
assets=r/'src/main/resources/assets';files=[p for p in assets.rglob('*') if p.is_file()]
assert all(sha(p)==sha(old/'src/main/resources/assets'/p.relative_to(assets)) for p in files)
checks.append({'test':'approved_art_unchanged','passed':True,'files':len(files)})
b=json.loads((r/'evidence/native-build.json').read_text());jar=r/'dist'/b['jar']
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;assert not any('cinematicqa' in n for n in z.namelist());assert not any(n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
checks.append({'test':'jar_no_QA_and_no_font_binaries','passed':True})
(r/'evidence/n03-build-gates.json').write_text(json.dumps({'jar':b,'checks':checks,'real_runtime':'PENDING'},indent=2));print(json.dumps(b),flush=True)
