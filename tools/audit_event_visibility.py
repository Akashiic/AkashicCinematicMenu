from pathlib import Path
import json,subprocess,zipfile
R=Path(__file__).resolve().parents[1];meta=json.loads((R/'evidence/native-build.json').read_text());jar=R/'dist'/meta['jar'];checks=[]
with zipfile.ZipFile(jar) as z:
    classes=[n[:-6].replace('/','.') for n in z.namelist() if n.endswith('.class') and b'Lcpw/mods/fml/common/eventhandler/SubscribeEvent;' in z.read(n)]
assert classes,'No event listeners discovered'
for name in classes:
    p=subprocess.run(['javap','-p','-classpath',str(jar),name],capture_output=True,text=True,timeout=8)
    assert p.returncode==0,p.stderr
    declaration=next(l for l in p.stdout.splitlines() if 'class '+name in l)
    ok=declaration.startswith('public ');checks.append({'class':name,'public':ok});assert ok,'Forge generated adapters need accessible listener '+name
(R/'evidence/event-visibility-audit.json').write_text(json.dumps({'jar_sha256':meta['sha256'],'listeners':checks,'all_passed':True},indent=2));print('PUBLIC_FORGE_LISTENERS_CHECKED',len(checks),flush=True)
