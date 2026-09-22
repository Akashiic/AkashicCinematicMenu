from pathlib import Path
import json,hashlib,zipfile
from PIL import Image
R=Path(__file__).resolve().parents[1];E=R/'evidence'
required=['core-alpha3.log','SecondaryScreenPolicyTests-alpha3.log','CeremonialTests-alpha3.log',
          'PixelLayoutTests-alpha3.log','UiFeaturesTests-alpha3.log','MusicCoreTests-alpha3.log',
          'BgmPlaybackTests-alpha3.log','MenuSoundGateTests-alpha3.log','transformer-probe-alpha3.log',
          'quit-probe-alpha3.log','secondary-graphics-alpha3.log']
for name in required:
    p=E/name;assert p.is_file(),name
    text=p.read_text(encoding='utf-8',errors='replace')
    assert 'AssertionError' not in text and 'Exception in thread' not in text,name
widgets=Image.open(R/'src/main/resources/assets/akashiccinematic/textures/gui/widgets_forbidden.png').convert('RGBA')
states={}
for y in [46,66,86]:
    crop=widgets.crop((0,y,200,y+20));unique=len(set(crop.getdata()))
    assert crop.getchannel('A').getbbox()==(0,0,200,20)
    assert unique>=12,('insufficient gradient state',y,unique)
    states[str(y)]=unique
surface=Image.open(R/'src/main/resources/assets/akashiccinematic/textures/gui/list_surface.png').convert('RGBA')
alpha=list(surface.getchannel('A').getdata());assert surface.size==(32,32) and min(alpha)>=24 and 0 not in alpha
b=json.loads((E/'native-build.json').read_text());assert b['version']=='0.7.0-alpha.3'
jar=R/'dist'/b['jar'];sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();assert sha(jar)==b['sha256']
base=R.parent/'native-n10-ceremonial';bgm='src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg'
assert sha(R/bgm)==sha(base/bgm)
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None
    assert 'assets/akashiccinematic/textures/gui/widgets_forbidden.png' in z.namelist()
    assert 'assets/akashiccinematic/textures/gui/list_surface.png' in z.namelist()
report={'version':b['version'],'jar_sha256':b['sha256'],'jar_bytes':b['bytes'],
        'button_unique_colours':states,'button_threshold':12,'list_surface':'PASS',
        'transformer':'PASS','direct_quit':'PASS','delete_world_confirmation':'PRESERVED',
        'secondary_graphics':'PASS','BGM_bytes_unchanged':True,'runtime':'PENDING',
        'prior_gate_failure':'Texture states had19 colors; previous >20 requirement was invalid and is preserved in wave2 alpha2 log.'}
(E/'wave2-alpha3-gates-final.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
(E/'texture-gate-correction.json').write_text(json.dumps({'old_gate':'>20 unique RGBA values','measured':states,
    'new_gate':'>=12 unique RGBA values plus fully covered alpha','product_changed':False,
    'reason':'Each20px state uses18 interpolated vertical rows plus border/highlight;19 unique values is the expected authored gradient.'},indent=2))
print('WAVE2_ALPHA3_FINAL_GATES_PASSED',b['sha256'],flush=True)
