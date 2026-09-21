from pathlib import Path
import json,hashlib,subprocess,sys,zipfile
C=Path(__file__).resolve().parents[1];P=C.parents[1];Q=P/'qa-n02';E=Q/'evidence/n07final';ER=Q/'evidence/n07recovery2';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
a=json.loads((C/'CHECKPOINT_N07.json').read_text());assert a['verdict']=='TARGETED_TESTS_PASSED_VISUAL_REVIEW_PENDING';assert a['process']['exit']==0 and a['focused_recovery_process']['exit']==0
jar=C/'dist/AkashicCinematicMenu-0.4.0-alpha.2.jar';expected='226b5c13ed0052a07e379feeab5745841c06f890346fb8cdb499f5ae03f3e8a5';assert sha(jar)==expected
res=subprocess.run([sys.executable,str(C/'tools/build_native.py')],capture_output=True,text=True,timeout=90);(C/'evidence/reproducible-build.log').write_text(res.stdout+res.stderr,encoding='utf-8');assert res.returncode==0 and sha(jar)==expected,'Rebuild mismatch'
files=[(jar,'dist/'+jar.name),(C/'README.md','README.md'),(C/'CHECKPOINT_N07.json','CHECKPOINT_N07.json')]
files += [(p,p.relative_to(C).as_posix()) for n in ['src','docs'] for p in (C/n).rglob('*') if p.is_file()]
for n in ['build_native.py','test_core.py','audit_fidelity_resources.py','build_fidelity_assets.py','audit_event_visibility.py']:files.append((C/'tools'/n,'tools/'+n))
for n in ['fidelity-assets.json','design-decisions.json','alpha1-visual-review.json','fidelity-build-gates.json','resource-fidelity-audit.json','core-fidelity.log','geometry-fidelity.log','connection-fidelity.log','ui-features-retained.log','event-visibility-audit.json','reproducible-build.log','native-classpath.json','runtime-audit.json']:
    files.append((C/'evidence'/n,'evidence/'+n))
for n in ['n07-acceptance.json','recovery-acceptance.json','startup-resize.json','process.json']:
    files.append((E/n,'runtime/n07final/'+n))
for n in ['focused-recovery.json','process.json']:
    files.append((ER/n,'runtime/n07recovery2/'+n))
for n in ['maximized-menu.jpg','connecting.jpg','disconnected.jpg','pixel-2560x1440.jpg','pixel-854x480.jpg']:
    files.append((E/'review'/n,'runtime/captures/'+n))
for n in ['after-stall-800.jpg','after-stall-6500.jpg']:
    files.append((ER/'review'/n,'runtime/captures/'+n))
files.append((E/'startup-resize/3-maximize.jpg','runtime/captures/startup-maximized.jpg'))
files.append((E/'startup-resize/4-restore.jpg','runtime/captures/startup-compact.jpg'))
out=C/'dist/AkashicCinematicMenu-N07-Fidelity-engineering.zip';assert not out.exists(),'Existing archive must be revalidated before replacing'
with zipfile.ZipFile(out,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p,name in files:
        assert not p.suffix.lower() in ['.ttf','.otf','.woff','.woff2'];assert 'cinematicqa' not in name.lower();assert p.is_file(),str(p)
        z.write(p,'AkashicCinematicMenu-N07/'+name)
with zipfile.ZipFile(out) as z:assert z.testzip() is None
receipt={'archive':str(out),'archive_sha256':sha(out),'archive_bytes':out.stat().st_size,'jar_sha256':sha(jar),'reproducible_build':True,'no_qa_mod_or_font_binary':True,'included_files':len(files),'visual_approval':'USER_PENDING','general_release_approved':False}
(C/'evidence/archive-receipt.json').write_text(json.dumps(receipt,indent=2),encoding='utf-8');print(json.dumps(receipt,indent=2),flush=True)
