from pathlib import Path
import json,hashlib,shutil,subprocess,zipfile,os,time
P=Path(__file__).resolve().parents[3]
R=P/'candidates/native-n11-gui-wave2';BASE=P/'qa-n02';Q=P/'qa-n11-wave2'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
assert not Q.exists(),'Existing QA must be inspected, not overwritten'
manifest=json.loads((BASE/'evidence/instance-manifest.json').read_text())
assert manifest.get('credentials_read') is False
b=json.loads((R/'evidence/native-build.json').read_text());assert b['version']=='0.7.0-alpha.2'
candidate=R/'dist'/b['jar'];assert sha(candidate)==b['sha256']
Q.mkdir();(Q/'tools').mkdir();(Q/'evidence').mkdir();(Q/'build').mkdir()
I=Q/'instance';I.mkdir()
skip={'saves','screenshots','logs','crash-reports','cache','.asm'}
for entry in (BASE/'instance').iterdir():
    if entry.name in skip:continue
    dest=I/entry.name
    if entry.is_dir():shutil.copytree(entry,dest)
    elif entry.is_file():shutil.copy2(entry,dest)
    print('COPIED',entry.name,flush=True)
for name in ['saves','screenshots','logs']:(I/name).mkdir(exist_ok=True)
mods=I/'mods';old=list(mods.glob('*CinematicMenu*.jar'));assert len(old)==1,old
old[0].rename(Q/'evidence'/old[0].name);shutil.copy2(candidate,mods/candidate.name)
assert sha(mods/candidate.name)==b['sha256']
shutil.copy2(BASE/'tools/launch_review.py',Q/'tools/launch_review.py')
source=(BASE/'tools/QAHarnessV7.java').read_text(encoding='utf-8')
source=source.replace('QAHarnessV7','QAHarnessWave2').replace('HARNESS_V7_REGISTERED','HARNESS_WAVE2_REGISTERED')
anchor='        if(!observedBoot){';assert source.count(anchor)==1
source=source.replace(anchor,'        s+=Wave2QaActions.status(mc);\n'+anchor)
anchor='                default:throw new IllegalArgumentException("unapproved QA verb");';assert source.count(anchor)==1
source=source.replace(anchor,'                default:if(!Wave2QaActions.command(a,mc,g,root))throw new IllegalArgumentException("unapproved QA verb");break;')
harness=Q/'tools/QAHarnessWave2.java';harness.write_text(source,encoding='utf-8',newline='\n')
shutil.copy2(R/'tools/Wave2QaActions.java',Q/'tools/Wave2QaActions.java')
out=Q/'build/harness';deps=[x['path'] for x in json.loads((R/'evidence/native-classpath.json').read_text())]
run=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8',
                    '-cp',os.pathsep.join(deps),'-d',str(out),str(harness),str(Q/'tools/Wave2QaActions.java')],
                   capture_output=True,text=True,timeout=50)
(Q/'evidence/harness-compile.log').write_text(run.stdout+run.stderr,encoding='utf-8')
assert run.returncode==0,run.stderr
qaJar=mods/'zz-AkashicCinematic-QA-only.jar'
if qaJar.exists():qaJar.rename(Q/'evidence/previous-qa-driver.jar')
with zipfile.ZipFile(qaJar,'w',compression=zipfile.ZIP_DEFLATED) as z:
    for f in out.rglob('*.class'):z.write(f,f.relative_to(out).as_posix())
new_manifest=dict(manifest);new_manifest['instance_root']=str(I)
new_manifest['candidate_sha256']=b['sha256'];new_manifest['source_saves_copied']=False;new_manifest['credentials_read']=False
(Q/'evidence/instance-manifest.json').write_text(json.dumps(new_manifest,indent=2))
options=I/'options.txt'
if options.is_file():
    lines=options.read_bytes().splitlines()
    for key,value in [(b'soundCategory_master',b'0.08'),(b'soundCategory_music',b'0.30')]:
        lines=[x for x in lines if not x.startswith(key+b':')]+[key+b':'+value]
    options.write_bytes(b'\n'.join(lines)+b'\n')
receipt={'source_qa':str(BASE),'candidate':b,'qa_candidate_hash':sha(mods/candidate.name),
         'qa_driver_hash':sha(qaJar),'skipped_top_level':sorted(skip),
         'source_saves_copied':False,'credentials_read':False,'daily_pack_touched':False,'created':time.time()}
(Q/'evidence/setup-receipt.json').write_text(json.dumps(receipt,indent=2))
(R/'evidence/qa-setup-history.json').write_text(json.dumps({'first_attempt':'aborted because qa-n02/saves contained NEI cache only; nothing copied',
    'second_attempt':receipt},indent=2))
print('WAVE2_SANITIZED_QA_READY',json.dumps(receipt),flush=True)
