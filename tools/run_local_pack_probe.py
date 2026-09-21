from pathlib import Path
import json,subprocess,os
R=Path(__file__).resolve().parents[1];b=json.loads((R/'evidence/native-build.json').read_text());cp=os.pathsep.join([str(R/'dist'/b['jar'])]+[x['path'] for x in json.loads((R/'evidence/native-classpath.json').read_text())]);out=R/'build/pack-probe';out.mkdir(exist_ok=False)
run=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',cp,'-d',str(out),str(R/'tools/LocalMusicPackProbe.java')],capture_output=True,text=True,timeout=30);assert run.returncode==0,run.stdout+run.stderr
fixture=R/'build/pack-probe-fixture';assert not fixture.exists();cmd=['java','-Xmx128m','-cp',str(out)+os.pathsep+cp,'LocalMusicPackProbe',str(fixture),str(R/'local-assets/akashiccinematic/bgm/menu.ogg')];res=subprocess.run(cmd,capture_output=True,text=True,timeout=20)
(R/'evidence/local-pack-probe.log').write_text(res.stdout+res.stderr,encoding='utf-8');print(res.stdout+res.stderr,flush=True);assert res.returncode==0
