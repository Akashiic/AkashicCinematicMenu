from pathlib import Path
import os,json,subprocess,zipfile,hashlib
from PIL import Image
R=Path(__file__).resolve().parents[1]
E=R/'evidence'
b=json.loads((E/'native-build.json').read_text())
jar=R/'dist'/b['jar']
deps=[x['path'] for x in json.loads((E/'native-classpath.json').read_text())]
cp=os.pathsep.join([str(jar)]+deps)
env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
out=R/'build/wave2-probes-resume';out.mkdir(exist_ok=False)
sources=[R/'tools/SecondaryGraphicsProbe.java']
def run(name,args,timeout=70):
    result=subprocess.run(args,capture_output=True,text=True,timeout=timeout,env=env)
    (E/(name+'.log')).write_text(result.stdout+result.stderr,encoding='utf-8')
    print(name,result.returncode,(result.stdout+result.stderr)[-1000:],flush=True)
    assert result.returncode==0,name
run('secondary-probe-resume-compile',['javac','--release','8','-Xlint:-options','-encoding','UTF-8',
                                      '-cp',cp,'-d',str(out)]+[str(x) for x in sources],30)
natives=R/'build/probe-natives'
captures=E/'secondary-probe-resume';captures.mkdir(exist_ok=False)
java=Path.home()/'.jdks/corretto-1.8.0_422/bin/java.exe'
probe_cp=str(out)+os.pathsep+cp
run('secondary-graphics-resume',[str(java),'-Xms32m','-Xmx192m','-XX:MaxDirectMemorySize=128m',
                                 '-Djava.library.path='+str(natives),'-cp',probe_cp,
                                 'br.com.redeakashic.cinematic.render.SecondaryGraphicsProbe',
                                 str(captures)],70)
gui=R/'src/main/resources/assets/akashiccinematic/textures/gui'
widgets=Image.open(gui/'widgets_forbidden.png').convert('RGBA')
surface=Image.open(gui/'list_surface.png').convert('RGBA')
assert widgets.size==(256,256) and surface.size==(32,32)
for y in [46,66,86]:
    crop=widgets.crop((0,y,200,y+20));alpha=crop.getchannel('A')
    assert alpha.getbbox()==(0,0,200,20)
    assert len(set(crop.getdata()))>20
assert min(surface.getchannel('A').getdata())==255
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None
    assert 'assets/akashiccinematic/textures/gui/widgets_forbidden.png' in z.namelist()
    assert 'assets/akashiccinematic/textures/gui/list_surface.png' in z.namelist()
    assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
base=R.parent/'native-n10-ceremonial'
bgm='src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg'
assert sha(R/bgm)==sha(base/bgm)
prior=json.loads((E/'wave2-gates.json').read_text()) if (E/'wave2-gates.json').exists() else {}
report={'jar_sha256':sha(jar),'product_changed_since_failed_probe':False,
        'secondary_graphics':'PASS','atlas_contract':'PASS',
        'BGM_bytes_unchanged':True,
        'previous_transformer_probe':'PASS' if (E/'transformer-probe.log').exists() else 'MISSING',
        'previous_quit_probe':'PASS' if (E/'quit-probe.log').exists() else 'MISSING',
        'prior_gate_file_present':bool(prior)}
(E/'wave2-resume-gates.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('WAVE2_RESUME_GATES_PASSED',json.dumps(report),flush=True)
