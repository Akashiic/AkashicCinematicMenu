from pathlib import Path
import os,json,subprocess,zipfile
R=Path(__file__).resolve().parents[1];E=R/'evidence'
b=json.loads((E/'native-build.json').read_text())
jar=R/'dist'/b['jar'];deps=[x['path'] for x in json.loads((E/'native-classpath.json').read_text())]
cp=os.pathsep.join([str(jar)]+deps);env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
out=R/'build/wave2-probes-r2';out.mkdir(exist_ok=False)
src=R/'tools/SecondaryGraphicsProbe.java'
c=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',cp,'-d',str(out),str(src)],capture_output=True,text=True,timeout=35,env=env)
(E/'secondary-probe-r2-compile.log').write_text(c.stdout+c.stderr,encoding='utf-8');assert c.returncode==0,c.stderr
natives=R/'build/probe-natives';assert natives.exists()
captures=E/'secondary-probe-r2';captures.mkdir(exist_ok=False)
java=Path.home()/'.jdks/corretto-1.8.0_422/bin/java.exe'
args=[str(java),'-Xms32m','-Xmx192m','-XX:MaxDirectMemorySize=128m','-Djava.library.path='+str(natives),
      '-cp',str(out)+os.pathsep+cp,'br.com.redeakashic.cinematic.render.SecondaryGraphicsProbe',str(captures)]
r=subprocess.run(args,capture_output=True,text=True,timeout=70,env=env)
(E/'secondary-graphics-r2.log').write_text(r.stdout+r.stderr,encoding='utf-8')
print(r.stdout+r.stderr,flush=True);assert r.returncode==0
from PIL import Image
for f in captures.glob('*.png'):
    im=Image.open(f);im.thumbnail((1400,900));im.save(f.with_suffix('.jpg'),quality=96)
print('SECONDARY_PROBE_R2_PASSED',flush=True)
