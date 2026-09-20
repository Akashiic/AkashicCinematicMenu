from pathlib import Path
import json,os,subprocess,sys
r=Path(__file__).resolve().parents[1];h=Path.home()
b=json.loads((r/'evidence/native-build.json').read_text());jar=r/'dist'/b['jar']
deps=[Path(x['path']) for x in json.loads((r/'evidence/native-classpath.json').read_text())]
deps += [h/'AkashicDev/ThePatcher-1.1.0-beta.4/lib'/n for n in ['asm-analysis-9.8.jar','asm-util-9.8.jar']]
cp=os.pathsep.join(map(str,[jar]+deps));out=r/'build/transformer-probe';out.mkdir(exist_ok=True)
res=subprocess.run(['javac','--release','8','-encoding','UTF-8','-Xlint:-options','-cp',cp,'-d',str(out),str(r/'tools/TransformerProbe.java')],capture_output=True,text=True,timeout=40)
assert res.returncode==0,res.stdout+res.stderr
args=[str(h/'.jdks/corretto-1.8.0_422/bin/java.exe'),'-cp',str(out)+os.pathsep+cp,'TransformerProbe',str(h/'AppData/Roaming/.technic/modpacks/forbidden-series/bin/modpack.jar')]
res=subprocess.run(args,capture_output=True,text=True,timeout=20);(r/'evidence/transformer-probe.log').write_text(res.stdout+res.stderr,encoding='utf-8');print(res.stdout+res.stderr);sys.exit(res.returncode)
