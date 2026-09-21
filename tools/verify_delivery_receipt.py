from pathlib import Path
import json,hashlib,re
P=Path(__file__).resolve().parents[3];R=P/'candidates/native-n10-ceremonial'
a=json.loads((R/'CHECKPOINT_N10.json').read_text());r=json.loads((R/'evidence/archive-receipt.json').read_text());sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
assert sha(Path(a['jar']))==a['jar_sha256']==r['jar_sha256'];assert sha(Path(r['archive']))==r['sha256'];assert a['pbuffer_process']['exit']==0
html=R/'docs/Revisao-N10.html';s=html.read_text(encoding='utf-8');refs=re.findall(r'(?:src|href)="([^"]+)"',s)
assert all((html.parent/x).resolve().is_file() for x in refs)
assert a['jar_sha256'] in (P/'AGENTS.md').read_text(encoding='utf-8');assert 'N10' in (P/'README.md').read_text(encoding='utf-8')
daily=[{'name':p.name,'sha256':sha(p)} for p in (Path.home()/'AppData/Roaming/.technic/modpacks/forbidden-series/mods').glob('*CinematicMenu*.jar')]
report={'version':a['version'],'jar_sha256':a['jar_sha256'],'archive_sha256':r['sha256'],'archive_bytes':r['bytes'],'gallery_links_verified':len(refs),'daily_observed':daily,'full_modpack_runtime':False,'all_delivery_artifacts_verified':True}
(R/'evidence/final-receipt-check.json').write_text(json.dumps(report,indent=2),encoding='utf-8');print(json.dumps(report,indent=2),flush=True)
