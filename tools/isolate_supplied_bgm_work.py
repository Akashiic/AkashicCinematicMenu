from pathlib import Path
import json,shutil,hashlib,time
P=Path(__file__).resolve().parents[1];source=P/'candidates/native-n08';C=P/'candidates/native-n08-bgm-r1';oldQ=P/'qa-n02';Q=P/'qa-n08-bgm-r1';assert not C.exists() and not Q.exists(),'Existing isolated run must be inspected'
base=json.loads((source/'evidence/native-build.json').read_text());assert base['sha256']=='9d1fa4e166d076df35279ebc78b04f5c59769e29106aaf4ddb4a9100641918ef'
C.mkdir();(C/'evidence').mkdir();(C/'docs').mkdir()
for name in ['src','tools','local-assets']:shutil.copytree(source/name,C/name)
for name in ['supplied-bgm.json','decoder-audit.json','bgm-lifecycle-review.json','resumption.json']:shutil.copy2(source/'evidence'/name,C/'evidence'/name)
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=C/name;s=p.read_text(encoding='utf-8');assert '0.5.0-alpha.1' in s;p.write_text(s.replace('0.5.0-alpha.1','0.5.1-alpha.1'),encoding='utf-8',newline='\n')
Q.mkdir();(Q/'tools').mkdir();(Q/'evidence').mkdir();(Q/'build').mkdir();inst=Q/'instance';inst.mkdir()
for name in ['bin','mods','config','scripts','resources','resourcepacks']:
    s=oldQ/'instance'/name
    if s.exists():
        assert not s.is_symlink(),name;shutil.copytree(s,inst/name)
(inst/'saves').mkdir();shutil.copy2(oldQ/'instance/options.txt',inst/'options.txt')
for name in ['QAHarnessV7.java','N08QaActions.java','launch_review.py']:shutil.copy2(oldQ/'tools'/name,Q/'tools'/name)
m=json.loads((oldQ/'evidence/instance-manifest.json').read_text());m['instance_root']=str(inst);m['isolated_from']=str(oldQ/'instance');(Q/'evidence/instance-manifest.json').write_text(json.dumps(m,indent=2))
(C/'evidence/isolation.json').write_text(json.dumps({'reason':'An existing n08-setup held a different73-class N08 artifact. Preserve it instead of overwriting, deploy to a new physical QA copy with a unique version.','previous_QA_untouched':True,'new_candidate':str(C),'new_QA':str(Q),'version':'0.5.1-alpha.1','created':time.time()},indent=2));print('ISOLATED_WORKSPACE_READY',C,Q,flush=True)
