from pathlib import Path
import json,hashlib,subprocess,sys,zipfile
C=Path(__file__).resolve().parents[1];P=C.parents[1];Q=P/'qa-n02';E=Q/'evidence/n07release2';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
a=json.loads((C/'CHECKPOINT_N07.json').read_text());assert a['version']=='0.4.0-alpha.3' and a['all_final_checks_passed'] and a['runtime_process']['exit']==0
expected='14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac';jar=C/'dist/AkashicCinematicMenu-0.4.0-alpha.3.jar';assert sha(jar)==expected
res=subprocess.run([sys.executable,str(C/'tools/build_native.py')],capture_output=True,text=True,timeout=90);(C/'evidence/reproducible-build-final.log').write_text(res.stdout+res.stderr,encoding='utf-8');assert res.returncode==0 and sha(jar)==expected
files=[(jar,'dist/'+jar.name),(C/'README.md','README.md'),(C/'CHECKPOINT_N07.json','CHECKPOINT_N07.json')]
files += [(p,p.relative_to(C).as_posix()) for n in ['src','docs'] for p in (C/n).rglob('*') if p.is_file()]
for n in ['build_native.py','test_core.py','audit_fidelity_resources.py','build_fidelity_assets.py','audit_event_visibility.py']:files.append((C/'tools'/n,'tools/'+n))
for n in ['fidelity-assets.json','design-decisions.json','alpha1-visual-review.json','footer-inspection-fix.json','fidelity-build-gates.json','resource-fidelity-audit.json','core-fidelity.log','geometry-fidelity.log','connection-fidelity.log','ui-features-retained.log','event-visibility-audit.json','reproducible-build-final.log','native-classpath.json']:
    files.append((C/'evidence'/n,'evidence/'+n))
for n in ['n07-acceptance.json','resolution-acceptance.json','focused-recovery.json','process.json','suite-chain.json','final-audit.json']:
    files.append((E/n,'runtime/n07release2/'+n))
for n in ['initial-menu.jpg','menu-final.jpg','connecting.jpg','disconnected.jpg','pixel-2560x1440.jpg','pixel-854x480.jpg','after-stall-800.jpg','after-stall-6500.jpg']:
    files.append((E/'review'/n,'runtime/captures/'+n))
files.append((Q/'evidence/n07release/early-exit-review.json','history/early-exit-review.json'))
files.append((Q/'evidence/n07-recovery-test-correction.json','history/qa-window-identity-correction.json'))
files.append((Q/'evidence/n07final/startup-resize/3-maximize.jpg','history/alpha2-startup-maximized.jpg'))
out=C/'dist/AkashicCinematicMenu-N07-Fidelity-engineering.zip';assert not out.exists(),'Existing archive must be inspected before replacement'
with zipfile.ZipFile(out,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p,name in files:
        assert p.is_file(),str(p);assert p.suffix.lower() not in ['.ttf','.otf','.woff','.woff2'];assert 'cinematicqa' not in name.lower()
        z.write(p,'AkashicCinematicMenu-N07/'+name)
with zipfile.ZipFile(out) as z:assert z.testzip() is None
receipt={'archive':str(out),'archive_sha256':sha(out),'archive_bytes':out.stat().st_size,'jar_sha256':sha(jar),'version':'0.4.0-alpha.3','reproducible_build':True,'files':len(files),'no_QA_mod_or_font_binaries':True,'general_release_approved':False,'visual_approval':'USER_PENDING','earlier_unexplained_exit_recorded':True}
(C/'evidence/archive-receipt-final.json').write_text(json.dumps(receipt,indent=2),encoding='utf-8');print(json.dumps(receipt,indent=2),flush=True)
