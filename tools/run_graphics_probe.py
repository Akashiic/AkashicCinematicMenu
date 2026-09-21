from pathlib import Path
import json,os,subprocess,sys,zipfile,time
R=Path(__file__).resolve().parents[1];H=Path.home();b=json.loads((R/'evidence/native-build.json').read_text());jar=R/'dist'/b['jar'];deps=[Path(x['path']) for x in json.loads((R/'evidence/native-classpath.json').read_text())]
cp=os.pathsep.join(map(str,[jar]+deps));out=R/'build/ceremonial-probe';out.mkdir(exist_ok=True);env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
a=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',cp,'-d',str(out),str(R/'tools/CeremonialGraphicsProbe.java')],capture_output=True,text=True,timeout=35,env=env)
(R/'evidence/probe-compile.log').write_text(a.stdout+a.stderr,encoding='utf-8');assert a.returncode==0,a.stdout+a.stderr
nat=R/'build/probe-natives';nat.mkdir(exist_ok=True);archive=next((H/'.gradle/caches/modules-2/files-2.1/org.lwjgl.lwjgl/lwjgl-platform/2.9.1').glob('*/*natives-windows.jar'))
with zipfile.ZipFile(archive) as z:
    for n in ['lwjgl64.dll','OpenAL64.dll']:(nat/n).write_bytes(z.read(n))
java=H/'.jdks/corretto-1.8.0_422/bin/java.exe';assert java.exists();evidence=R/'evidence'/('native-probe-'+b['version']);evidence.mkdir(exist_ok=True)
args=[str(java),'-Xms32m','-Xmx192m','-XX:MaxDirectMemorySize=128m','-Djava.library.path='+str(nat),'-cp',str(out)+os.pathsep+cp,'br.com.redeakashic.cinematic.render.CeremonialGraphicsProbe',str(evidence)]
with (R/'evidence'/('native-probe-'+b['version']+'.log')).open('w',encoding='utf-8') as log:
    start=time.time();p=subprocess.Popen(args,stdout=log,stderr=subprocess.STDOUT,env=env);(R/'evidence'/('probe-process-'+b['version']+'.json')).write_text(json.dumps({'pid':p.pid,'started':start,'kind':'isolated pbuffer, not Minecraft'},indent=2))
    try:code=p.wait(timeout=70)
    except subprocess.TimeoutExpired:p.terminate();code=p.wait(timeout=10)
(R/'evidence'/('probe-process-'+b['version']+'.json')).write_text(json.dumps({'pid':p.pid,'started':start,'ended':time.time(),'exit':code,'kind':'isolated pbuffer, not Minecraft'},indent=2));print('PROBE_EXIT',code,flush=True)
if code:sys.exit(code)
from PIL import Image
for f in [p for p in evidence.glob('*.png') if not p.name.startswith('motion-')]:
    im=Image.open(f);im.thumbnail((1400,900));im.save(f.with_suffix('.jpg'),quality=96)
print('NATIVE_PROBE_CAPTURES_READY',flush=True)

frames=[Image.open(p).convert("RGB") for p in sorted(evidence.glob("motion-*.png"))]
frames[0].save(evidence/"N10-Movimento-Nativo.webp",save_all=True,append_images=frames[1:],duration=162,loop=0,quality=88)
