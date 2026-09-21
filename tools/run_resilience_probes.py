from pathlib import Path
import json,zipfile,os,subprocess,hashlib
R=Path(__file__).resolve().parents[1];P=R.parents[1];b=json.loads((R/'evidence/native-build.json').read_text());jar=R/'dist'/b['jar'];deps=[x['path'] for x in json.loads((R/'evidence/native-classpath.json').read_text())]
libs=json.loads((P/'qa-n02/evidence/instance-manifest.json').read_text())['libraries'];out=R/'build/resilience-probes';out.mkdir(exist_ok=True);cp=os.pathsep.join([str(jar)]+deps+libs)
java=Path.home()/'.jdks/corretto-1.8.0_422/bin/java.exe';env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx160m'
r=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',cp,'-d',str(out),str(R/'tools/DepthFallbackProbe.java'),str(R/'tools/CeremonialCodecProbe.java')],capture_output=True,text=True,timeout=30,env=env);assert r.returncode==0,r.stdout+r.stderr
variant=R/'build/depth-missing-test-only.jar'
with zipfile.ZipFile(jar) as source,zipfile.ZipFile(variant,'w',compression=zipfile.ZIP_DEFLATED) as dest:
    for n in source.namelist():
        if n!='assets/akashiccinematic/native/depth/angel.png':dest.writestr(n,source.read(n))
args=[str(java),'-Djava.library.path='+str(R/'build/probe-natives'),'-cp',os.pathsep.join([str(out),str(variant)]+deps),'br.com.redeakashic.cinematic.render.DepthFallbackProbe']
r=subprocess.run(args,capture_output=True,text=True,timeout=25,env=env);(R/'evidence/depth-fallback-probe.log').write_text(r.stdout+r.stderr,encoding='utf-8');print(r.stdout+r.stderr,flush=True);assert r.returncode==0
args=[str(java),'-cp',str(out)+os.pathsep+cp,'CeremonialCodecProbe',str(R/'src/main/resources/assets/akashiccinematic/sounds/ritual')]
r=subprocess.run(args,capture_output=True,text=True,timeout=20,env=env);(R/'evidence/jorbis-cues.log').write_text(r.stdout+r.stderr,encoding='utf-8');print(r.stdout+r.stderr,flush=True);assert r.returncode==0
preview=R/'evidence/audio/Ceremonial-SFX-Audition.wav';print('AUDITION_SHA256',hashlib.sha256(preview.read_bytes()).hexdigest(),preview.stat().st_size,flush=True)
assert hashlib.sha256(jar.read_bytes()).hexdigest()==b['sha256'];print('FINAL_JAR_UNCHANGED',b['sha256'],flush=True)
