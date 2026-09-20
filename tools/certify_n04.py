from pathlib import Path
import hashlib,json,subprocess,sys,zipfile
R=Path(__file__).resolve().parents[1];E=R/'evidence';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
jar=R/'dist/AkashicCinematicMenu-0.2.0-alpha.4.jar';expected='3adfd2cc102eed60666d5594afc0a375e2a1fc7047ed409c7afc81c5969c190e';assert sha(jar)==expected
steps=[('core-certification',[sys.executable,str(R/'tools/test_core.py')]),('connection-certification',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.ConnectionWatchTests']),('rebuild-certification',[sys.executable,str(R/'tools/build_native.py')])]
results=[]
for label,args in steps:
    p=subprocess.run(args,capture_output=True,text=True,timeout=90);(E/(label+'.log')).write_text(p.stdout+p.stderr,encoding='utf-8');results.append({'test':label,'exit':p.returncode});print(label,p.returncode,(p.stdout+p.stderr)[-160:],flush=True);assert p.returncode==0
assert sha(jar)==expected,'Rebuild must match actually tested candidate'
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None
    names=z.namelist();classes=[n for n in names if n.endswith('.class')]
    assert all(n.startswith('br/com/redeakashic/cinematic/') for n in classes)
    assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in names)
    assert not any(n.startswith('br/com/redeakashic/cinematic/earlygl/') for n in names),'Blocked experiment must not leak into active artifact'
rows=sorted(p.relative_to(R).as_posix()+' '+sha(p) for p in (R/'src').rglob('*') if p.is_file());text='\n'.join(rows)+'\n';(E/'source-manifest-certified.sha256').write_text(text,newline='\n')
result={'version':'0.2.0-alpha.4','jar_sha256':expected,'jar_bytes':jar.stat().st_size,'classes':len(classes),'reproducible_build':True,'tests':results,'source_manifest_sha256':hashlib.sha256(text.encode()).hexdigest(),'no_QA_classes_or_font_files':True,'no_partial_n05_code':True}
(E/'certification-build.json').write_text(json.dumps(result,indent=2));print(json.dumps(result),flush=True)
