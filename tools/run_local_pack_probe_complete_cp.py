from pathlib import Path
import json,subprocess,os
R=Path(__file__).resolve().parents[1];Q=R.parents[1]/'qa-n08-bgm-6c79c577';b=json.loads((R/'evidence/native-build.json').read_text());deps=[x['path'] for x in json.loads((R/'evidence/native-classpath.json').read_text())]+json.loads((Q/'evidence/instance-manifest.json').read_text())['libraries'];cp=os.pathsep.join([str(R/'build/pack-probe'),str(R/'dist'/b['jar'])]+deps)
fixture=R/'build/pack-probe-fixture-complete';assert not fixture.exists()
run=subprocess.run(['java','-Xmx128m','-cp',cp,'LocalMusicPackProbe',str(fixture),str(R/'local-assets/akashiccinematic/bgm/menu.ogg')],capture_output=True,text=True,timeout=20)
(R/'evidence/local-pack-probe-complete.log').write_text(run.stdout+run.stderr,encoding='utf-8');print(run.stdout+run.stderr,flush=True);assert run.returncode==0
