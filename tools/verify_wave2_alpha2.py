from pathlib import Path
import os,json,subprocess,sys,zipfile,hashlib
from PIL import Image
R=Path(__file__).resolve().parents[1];E=R/'evidence';env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
steps=[]
def run(name,args,timeout=80):
    r=subprocess.run(args,capture_output=True,text=True,timeout=timeout,env=env)
    (E/(name+'-alpha2.log')).write_text(r.stdout+r.stderr,encoding='utf-8')
    steps.append({'step':name,'exit':r.returncode,'tail':(r.stdout+r.stderr)[-260:]})
    print(name,r.returncode,(r.stdout+r.stderr)[-220:],flush=True);assert r.returncode==0,name
run('core',[sys.executable,str(R/'tools/test_core.py')])
for name in ['SecondaryScreenPolicyTests','CeremonialTests','PixelLayoutTests','UiFeaturesTests','MusicCoreTests','BgmPlaybackTests','MenuSoundGateTests']:
    run(name,['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.'+name])
run('native-build',[sys.executable,str(R/'tools/build_native.py')])
b=json.loads((E/'native-build.json').read_text());assert b['version']=='0.7.0-alpha.2'
jar=R/'dist'/b['jar'];sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();assert sha(jar)==b['sha256']
deps=[x['path'] for x in json.loads((E/'native-classpath.json').read_text())];cp=os.pathsep.join([str(jar)]+deps)
probe=R/'build/wave2-alpha2-probes';probe.mkdir(exist_ok=False)
sources=[R/'tools/SecondaryTransformerProbe.java',R/'tools/QuitBytecodeProbe.java',R/'tools/SecondaryGraphicsProbe.java']
run('probe-compile',['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',cp,'-d',str(probe)]+[str(x) for x in sources],40)
srg=Path.home()/'AkashicDev/CubicChunks-Crucible/source/build/rfg/srg_merged_minecraft.jar'
probe_cp=str(probe)+os.pathsep+cp
run('transformer-probe',['java','-ea','-cp',probe_cp,'SecondaryTransformerProbe',str(srg)],30)
run('quit-probe',['java','-ea','-cp',probe_cp,'QuitBytecodeProbe',str(jar)],20)
natives=R/'build/probe-natives';assert natives.exists()
captures=E/'secondary-probe-alpha2';captures.mkdir(exist_ok=False)
java=Path.home()/'.jdks/corretto-1.8.0_422/bin/java.exe'
run('secondary-graphics',[str(java),'-Xms32m','-Xmx192m','-XX:MaxDirectMemorySize=128m',
                         '-Djava.library.path='+str(natives),'-cp',probe_cp,
                         'br.com.redeakashic.cinematic.render.SecondaryGraphicsProbe',str(captures)],70)
for f in captures.glob('*.png'):
    im=Image.open(f);im.thumbnail((1400,900));im.save(f.with_suffix('.jpg'),quality=96)
gui=R/'src/main/resources/assets/akashiccinematic/textures/gui'
widgets=Image.open(gui/'widgets_forbidden.png').convert('RGBA');surface=Image.open(gui/'list_surface.png').convert('RGBA')
assert widgets.size==(256,256) and surface.size==(32,32)
for y in [46,66,86]:
    crop=widgets.crop((0,y,200,y+20));assert crop.getchannel('A').getbbox()==(0,0,200,20);assert len(set(crop.getdata()))>20
assert min(surface.getchannel('A').getdata())==255
base=R.parent/'native-n10-ceremonial';bgm='src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg'
assert sha(R/bgm)==sha(base/bgm)
for name in ['client/MenuBgm.java','client/MenuBgmSettings.java','core/BgmPlayback.java','core/MenuSoundGate.java']:
    rel='src/main/java/br/com/redeakashic/cinematic/'+name;assert sha(R/rel)==sha(base/rel),name
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None
    for name in ['assets/akashiccinematic/textures/gui/widgets_forbidden.png','assets/akashiccinematic/textures/gui/list_surface.png']:
        assert name in z.namelist()
    assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
report={'version':b['version'],'jar_sha256':b['sha256'],'jar_bytes':b['bytes'],'classes':b['classes'],
        'steps':steps,'BGM_bytes_unchanged':True,'BGM_code_unchanged':True,
        'direct_quit_proved':True,'delete_world_confirmation_preserved':True,
        'visual_probe':'PASS','runtime':'PENDING'}
(E/'wave2-alpha2-gates.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('WAVE2_ALPHA2_GATES_PASSED',b['sha256'],flush=True)
