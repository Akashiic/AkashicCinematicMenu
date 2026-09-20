from pathlib import Path
import hashlib,json,subprocess,sys,zipfile
r=Path(__file__).resolve().parents[1];project=r.parents[1];h=Path.home()
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def logtext(p):
    b=p.read_bytes();return b.decode("utf-16" if b.startswith((bytes([255,254]),bytes([254,255]))) else "utf-8")
b=json.loads((r/'evidence/native-build.json').read_text());jar=r/'dist'/b['jar'];expected=b['sha256']
assert expected=='7e5c5b23932eff2011d464cfc4e5d6c352f20d750e6ee1a0a01108baf121731a' and sha(jar)==expected
res=subprocess.run([sys.executable,str(r/'tools/build_native.py')],capture_output=True,text=True,timeout=90);(r/'evidence/rebuild.log').write_text(res.stdout+res.stderr);assert res.returncode==0 and sha(jar)==expected,'Rebuild mismatch'
assert 'RESULT scenarios=48 assertions=17489 failures=0' in logtext(r/'evidence/core-final.log')
assert 'RESULT checks=511 failures=0' in logtext(r/'evidence/probe-release-candidate.log')
previous={'CHECKPOINT.json':'881eceba39efc0b2f622a42a64586dcbe0040d3712a20bf1308ca9767ab35bd9','dist/AkashicCinematicMenu-0.1.0-alpha.1.jar':'6d7f5b0a005343fa6188d282fe2742d32c8705fe2398e63b38283644fc86c047','candidates/navigation-r04/dist/AkashicCinematicMenu-0.1.0-alpha.2.jar':'c8930796eb1e3b1ec9ef4799eec868db3a3b7ee1809033abdd5b9e6c2c54e7a2'}
for n,digest in previous.items():assert sha(project/n)==digest,n
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None
    assert not any(n.lower().endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
    assert all(n.startswith('br/com/redeakashic/cinematic/') for n in z.namelist() if n.endswith('.class'))
rows=sorted(p.relative_to(r).as_posix()+' '+sha(p) for p in (r/'src').rglob('*') if p.is_file());manifest='\n'.join(rows)+'\n';(r/'evidence/source-manifest.sha256').write_text(manifest,newline='\n')
pack=h/'AppData/Roaming/.technic/modpacks/forbidden-series';assert not list((pack/'mods').glob('*CinematicMenu*.jar'))
audit={'checkpoint':'ACM-NATIVE-N01-2026-09-20','version':b['version'],'jar_sha256':expected,'jar_bytes':jar.stat().st_size,'classes':b['classes'],'source_manifest_sha256':hashlib.sha256(manifest.encode()).hexdigest(),'source_files':len(rows),'reproducible_build':True,'previous_checkpoints_unchanged':True,'installed_in_pack':False,'actual_minecraft_run':False,'network_connection_tested':False,'native_graphics_probe_checks':511,'native_graphics_probe_failures':0,'core_scenarios':48,'core_assertions':17489,'transformer_probe':'NOT_EXECUTED_TOOL_SAFETY_STATUS_BLOCK','verdict':'GRAPHICS_PROBE_PASSED__PACK_RUNTIME_BLOCKED','visuals_approved':['SANTUARIO_R04','O_DESPERTAR_S01.2'],'limitations':['2D art','prerendered blur','not full Minecraft runtime','Android untested']}
(r/'evidence/final-audit.json').write_text(json.dumps(audit,indent=2));(r/'CHECKPOINT_N01.json').write_text(json.dumps(audit,indent=2))
incomplete=r/'research/patch-0.b64'
if incomplete.exists():incomplete.rename(r/'research/DO_NOT_USE-incomplete-transfer.txt')
names=['README.md','CHECKPOINT_N01.json','evidence/final-audit.json','evidence/source-manifest.sha256','evidence/art-export.json','evidence/native-build.json','evidence/native-classpath.json','evidence/core-final.log','evidence/probe-release-candidate.log','evidence/render-comparison.json','evidence/rebuild.log']
for n in ['build_native.py','test_core.py','export_art.py','NativeProbe.java','run_probe.py','TransformerProbe.java','run_transformer_probe.py']:names.append('tools/'+n)
files=[r/n for n in names]+[jar]+[p for p in (r/'src').rglob('*') if p.is_file()]+[p for p in (r/'evidence/native-probe').glob('*.png')]
archive=r/'dist/AkashicCinematicMenu-N01-engineering.zip'
assert not archive.exists(),'Existing archive must be revalidated before overwrite'
with zipfile.ZipFile(archive,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p in sorted(set(files)):z.write(p,'AkashicCinematicMenu-N01/'+p.relative_to(r).as_posix())
with zipfile.ZipFile(archive) as z:assert z.testzip() is None
receipt={'archive':str(archive),'bytes':archive.stat().st_size,'sha256':sha(archive),'contains':len(files),'release_approved':False};(r/'evidence/archive-receipt.json').write_text(json.dumps(receipt,indent=2))
print(json.dumps(audit,indent=2));print(json.dumps(receipt,indent=2),flush=True)
