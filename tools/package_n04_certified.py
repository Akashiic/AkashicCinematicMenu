from pathlib import Path
import hashlib,json,zipfile
C=Path(__file__).resolve().parents[1];P=C.parents[1];E=P/'qa-n02/evidence';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
cert=json.loads((C/'CHECKPOINT_N04_RECOVERY.json').read_text());assert cert['fault_test_passed']==cert['fault_test_total']==12
jar=C/'dist/AkashicCinematicMenu-0.2.0-alpha.4.jar';assert sha(jar)==cert['jar_sha256'];assert sha(P/'qa-n02/instance/mods'/jar.name)==cert['jar_sha256']
files=[(jar,'dist/'+jar.name),(C/'README.md','README.md'),(P/'NATIVE_REVIEW_N04.md','NATIVE_REVIEW_N04.md'),(C/'CHECKPOINT_N04.json','CHECKPOINT_N04.json'),(C/'CHECKPOINT_N04_RECOVERY.json','CHECKPOINT_N04_RECOVERY.json')]
files += [(p,p.relative_to(C).as_posix()) for p in (C/'src').rglob('*') if p.is_file()]
for name in ['build_native.py','test_core.py','certify_n04.py']:files.append((C/'tools'/name,'tools/'+name))
for name in ['certification-build.json','source-manifest-certified.sha256','core-certification.log','connection-certification.log','native-classpath.json']:files.append((C/'evidence'/name,'evidence/'+name))
for name in ['run08/fault-acceptance.json','run08/process.json','fault-run08/restoration.json','run06/focus-direct.json','run07/focus-direct.json','run06/acceptance-v3.json','run07/startup-acceptance.json','n04-startup-comparison.json']:
    files.append((E/name,'runtime-evidence/'+name))
for source,name in [('run07/early-window/frame-009.jpg','startup-before-forge.jpg'),('run07/early-window/frame-028.jpg','startup-forge-progress.jpg'),('run07/review/menu-1366x768.jpg','menu-native.jpg'),('run08/fallback-review.jpg','fallback-missing-assets.jpg')]:
    files.append((E/source,'captures/'+name))
out=C/'dist/AkashicCinematicMenu-N04-verified-engineering.zip';assert not out.exists(),'Inspect existing archive before replacing'
with zipfile.ZipFile(out,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p,name in sorted(files,key=lambda pair:pair[1]):
        assert p.is_file() and not name.endswith(('.ttf','.otf','.woff','.woff2'))
        assert 'native-n05' not in name and 'QA-ONLY.jar' not in name
        z.write(p,'AkashicCinematicMenu-N04/'+name)
with zipfile.ZipFile(out) as z:assert z.testzip() is None
receipt={'archive':str(out),'bytes':out.stat().st_size,'sha256':sha(out),'jar_sha256':sha(jar),'report_sha256':sha(P/'NATIVE_REVIEW_N04.md'),'included_files':len(files),'general_release_approved':False,'fault_artifact_included':False,'partial_n05_included':False}
(C/'evidence/archive-certified.json').write_text(json.dumps(receipt,indent=2));print(json.dumps(receipt,indent=2),flush=True)
