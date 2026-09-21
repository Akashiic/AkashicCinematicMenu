from pathlib import Path
import subprocess,json,os
R=Path(__file__).resolve().parents[1];Q=R.parents[1]/'qa-n08-bgm-6c79c577';m=json.loads((Q/'evidence/instance-manifest.json').read_text());cp=os.pathsep.join(m['libraries']);out=R/'build/jorbis-probe';out.mkdir(exist_ok=False)
a=subprocess.run(['javac','--release','8','-Xlint:-options','-cp',cp,'-d',str(out),str(R/'tools/VorbisRuntimeProbe.java')],capture_output=True,text=True,timeout=30);assert a.returncode==0,a.stderr
b=subprocess.run([m['java'],'-Xmx128m','-cp',str(out)+os.pathsep+cp,'VorbisRuntimeProbe',str(R/'local-assets/akashiccinematic/bgm/menu.ogg')],capture_output=True,text=True,timeout=35);(R/'evidence/runtime-jorbis-probe.log').write_text(b.stdout+b.stderr,encoding='utf-8');print(b.stdout+b.stderr,flush=True);assert b.returncode==0
