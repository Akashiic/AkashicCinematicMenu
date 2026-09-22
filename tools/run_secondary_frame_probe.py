from pathlib import Path
import json,subprocess,os,zipfile,sys
from PIL import Image
R=Path(__file__).resolve().parents[1];H=Path.home();E=R/'evidence'
b=json.loads((E/'native-build.json').read_text());jar=R/'dist'/b['jar']
deps=[Path(x['path']) for x in json.loads((E/'native-classpath.json').read_text())]
cp=os.pathsep.join(map(str,[jar]+deps));out=R/'build/secondary-frame-probe';out.mkdir(exist_ok=True)
env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
run=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',cp,'-d',str(out),str(R/'tools/SecondaryFrameProbe.java')],
                   capture_output=True,text=True,timeout=30,env=env)
(E/'secondary-frame-compile.log').write_text(run.stdout+run.stderr,encoding='utf-8');assert run.returncode==0,run.stdout+run.stderr
nat=R/'build/probe-natives';nat.mkdir(exist_ok=True)
archive=next((H/'.gradle/caches/modules-2/files-2.1/org.lwjgl.lwjgl/lwjgl-platform/2.9.1').glob('*/*natives-windows.jar'))
with zipfile.ZipFile(archive) as z:
    for n in ['lwjgl64.dll','OpenAL64.dll']:(nat/n).write_bytes(z.read(n))
evidence=E/'secondary-frame-probe';evidence.mkdir(exist_ok=True)
java=H/'.jdks/corretto-1.8.0_422/bin/java.exe'
args=[str(java),'-Xms32m','-Xmx192m','-Djava.library.path='+str(nat),'-cp',str(out)+os.pathsep+cp,'br.com.redeakashic.cinematic.render.SecondaryFrameProbe',str(evidence)]
run=subprocess.run(args,capture_output=True,text=True,timeout=40,env=env)
(E/'secondary-frame-probe.log').write_text(run.stdout+run.stderr,encoding='utf-8');print(run.stdout+run.stderr,flush=True);assert run.returncode==0
for f in evidence.glob('*.png'):
    im=Image.open(f);im.thumbnail((1400,900));im.save(f.with_suffix('.jpg'),quality=96)
print('SECONDARY_FRAME_CAPTURES_READY',flush=True)
