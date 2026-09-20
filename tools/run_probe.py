from pathlib import Path
import json,os,subprocess,sys,zipfile
r=Path(__file__).resolve().parents[1];h=Path.home()
b=json.loads((r/'evidence/native-build.json').read_text());jar=r/'dist'/b['jar']
deps=[Path(x['path']) for x in json.loads((r/'evidence/native-classpath.json').read_text())]
cp=os.pathsep.join(map(str,[jar]+deps));out=r/'build/probe-classes';out.mkdir(exist_ok=True)
run=subprocess.run(['javac','--release','8','-encoding','UTF-8','-Xlint:-options','-cp',cp,'-d',str(out),str(r/'tools/NativeProbe.java')],capture_output=True,text=True,timeout=60)
print(run.stdout+run.stderr,flush=True);assert run.returncode==0,'probe compile'
nat=r/'build/probe-natives';nat.mkdir(exist_ok=True)
cache=h/'.gradle/caches/modules-2/files-2.1/org.lwjgl.lwjgl/lwjgl-platform/2.9.1'
archive=next(cache.glob('*/*natives-windows.jar'))
with zipfile.ZipFile(archive) as z:
    for n in z.namelist():
        if n in ['lwjgl64.dll','OpenAL64.dll','lwjgl.dll','OpenAL32.dll']:(nat/n).write_bytes(z.read(n))
assert (nat/'lwjgl64.dll').exists()
java=h/'.jdks/corretto-1.8.0_422/bin/java.exe'
args=[str(java),'-Xms64m','-Xmx384m','-Djava.library.path='+str(nat),'-cp',str(out)+os.pathsep+cp,'br.com.redeakashic.cinematic.render.NativeProbe',str(r/'evidence/native-probe')]
res=subprocess.run(args,capture_output=True,text=True,timeout=40);(r/'evidence/native-probe.log').write_text(res.stdout+res.stderr,encoding='utf-8');print(res.stdout+res.stderr,flush=True);print('PROBE_EXIT',res.returncode,flush=True);sys.exit(res.returncode)
