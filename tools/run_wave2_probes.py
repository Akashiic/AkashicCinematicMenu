from pathlib import Path
import json,subprocess,os,sys
R=Path(__file__).resolve().parents[1];H=Path.home();E=R/'evidence'
b=json.loads((E/'native-build.json').read_text());jar=R/'dist'/b['jar']
deps=[Path(x['path']) for x in json.loads((E/'native-classpath.json').read_text())]
extra=[H/'AkashicDev/ThePatcher-1.1.0-beta.4/lib/asm-analysis-9.8.jar',
       H/'AkashicDev/ThePatcher-1.1.0-beta.4/lib/asm-util-9.8.jar']
cp=os.pathsep.join(map(str,[jar]+deps+extra));env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
tests=['SecondaryScreenPolicyTests','CeremonialTests','PixelLayoutTests','UiFeaturesTests','MusicCoreTests','BgmPlaybackTests','MenuSoundGateTests']
results=[]
for name in tests:
    run=subprocess.run(['java','-ea','-cp',str(R/'build/core-tests')+os.pathsep+cp,'br.com.redeakashic.cinematic.core.'+name],
                       capture_output=True,text=True,timeout=30,env=env)
    (E/(name+'-wave2.log')).write_text(run.stdout+run.stderr,encoding='utf-8')
    print(name,run.returncode,(run.stdout+run.stderr)[-260:],flush=True);assert run.returncode==0,name
    results.append({'test':name,'exit':run.returncode})
out=R/'build/wave2-transformer-probe';out.mkdir(exist_ok=True)
src=R/'tools/Wave2TransformerProbe.java'
run=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',cp,'-d',str(out),str(src)],
                   capture_output=True,text=True,timeout=35,env=env)
(E/'wave2-transformer-compile.log').write_text(run.stdout+run.stderr,encoding='utf-8');assert run.returncode==0,run.stdout+run.stderr
minecraft=H/'AkashicDev/CubicChunks-Crucible/source/build/rfg/srg_merged_minecraft.jar'
run=subprocess.run(['java','-ea','-cp',str(out)+os.pathsep+cp,'Wave2TransformerProbe',str(minecraft),str(jar)],
                   capture_output=True,text=True,timeout=30,env=env)
(E/'wave2-transformer-probe.log').write_text(run.stdout+run.stderr,encoding='utf-8');print(run.stdout+run.stderr,flush=True);assert run.returncode==0
(E/'wave2-probes.json').write_text(json.dumps({'results':results,'transformer':'passed','jar_sha256':b['sha256']},indent=2))
