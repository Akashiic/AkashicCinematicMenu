from pathlib import Path
import hashlib,json,subprocess,sys,zipfile
C=Path(__file__).resolve().parents[1];P=C.parents[1];R=P/'qa-n02';E=R/'evidence/run04';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
a=json.loads((E/'final-audit.json').read_text());assert a['process']['exit']==0 and not a['failed_tests']
expected='154c95045e70b15798988358ff4a38db75c57523b4946e4e4b786a0414b104ea';jar=C/'dist/AkashicCinematicMenu-0.2.0-alpha.3.jar';assert sha(jar)==expected
res=subprocess.run([sys.executable,str(C/'tools/build_native.py')],capture_output=True,text=True,timeout=90);(C/'evidence/reproducible-build-n03.log').write_text(res.stdout+res.stderr,encoding='utf-8');assert res.returncode==0 and sha(jar)==expected,'Rebuild must match runtime-tested artifact'
files=[(jar,'dist/'+jar.name),(C/'README.md','README.md'),(C/'CHECKPOINT_N03.json','CHECKPOINT_N03.json'),(P/'NATIVE_REVIEW_N03.md','NATIVE_REVIEW_N03.md')]
files += [(p,p.relative_to(C).as_posix()) for p in (C/'src').rglob('*') if p.is_file()]
for n in ['build_native.py','test_core.py','verify_n03.py']:files.append((C/'tools'/n,'tools/'+n))
for n in ['final-audit.json','acceptance-v3.json','retry-connection.json','window-input.json','window-input-attempt1.json','network-after-cancel.json','network-after-second-cancel.json','process.json','events.log']:files.append((E/n,'runtime-evidence/'+n))
for n in ['core-tests.log','connection-policy-n03.log','native-build.json','native-classpath.json','reproducible-build-n03.log']:files.append((C/'evidence'/n,'evidence/'+n))
files += [(p,'runtime-captures/'+p.name) for p in (E/'review-v3').glob('*.jpg')]
files += [(p,'runtime-captures/'+p.name) for p in E.glob('own-window-*.jpg')]
out=C/'dist/AkashicCinematicMenu-N03-QA-validated-engineering.zip';assert not out.exists(),'Existing receipt must be inspected before replacement'
with zipfile.ZipFile(out,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p,name in files:
        assert not name.endswith(('.ttf','.otf','.woff','.woff2')) and 'cinematicqa' not in name
        z.write(p,'AkashicCinematicMenu-N03/'+name)
with zipfile.ZipFile(out) as z:assert z.testzip() is None
receipt={'archive':str(out),'archive_sha256':sha(out),'archive_bytes':out.stat().st_size,'jar_sha256':sha(jar),'reproducible_build':True,'game_qa_driver_included':False,'general_release_approved':False,'focus_latency_pending':True}
(C/'evidence/archive-receipt-n03.json').write_text(json.dumps(receipt,indent=2));print(json.dumps(receipt,indent=2),flush=True)
