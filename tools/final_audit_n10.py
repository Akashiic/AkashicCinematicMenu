from pathlib import Path
import json,hashlib,zipfile,subprocess,os,time
import numpy as np
from PIL import Image,ImageDraw
R=Path(__file__).resolve().parents[1];P=R.parents[1];B=R.parent/'native-n09-audio-fixed';E=R/'evidence';A=R/'src/main/resources/assets/akashiccinematic/native';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
b=json.loads((E/'native-build.json').read_text());assert b['version']=='0.6.0-alpha.3';jar=R/'dist'/b['jar'];expected='a15e18a206e89aa7736d71cae22813ecb4be0e7cb0dffd75d4c10e0939785859';assert sha(jar)==expected
probe=json.loads((E/('probe-process-'+b['version']+'.json')).read_text());assert probe['exit']==0
log=(E/('native-probe-'+b['version']+'.log')).read_text();assert 'RESULT checks=276 failures=0' in log
assert 'pixel_equal=true textures_released=true errors=0' in (E/'depth-fallback-probe.log').read_text();assert 'cues=6 failures=0' in (E/'jorbis-cues.log').read_text()
results=json.loads((E/'build-audit.json').read_text());assert all(t['exit']==0 for t in results['tests'])
original=Image.open(A/'backdrop.jpg').convert('RGB');plates=[Image.open(A/'depth'/n).convert('RGBA') for n in ['sky.png','architecture.png','angel.png','occluder.png']]
neutral=Image.new('RGBA',original.size,(0,0,0,255))
for image in plates:assert image.size==(1255,878);neutral=Image.alpha_composite(neutral,image.crop((24,24,1231,854)))
diff=np.abs(np.array(neutral.convert('RGB'),dtype='int16')-np.array(original,dtype='int16'));mean=float(diff.mean());assert mean<1.0,mean
neutral.convert('RGB').save(E/'depth/final-neutral.png');board=Image.new('RGB',(1208,830),(20,24,31))
for i,image in enumerate(plates):
    tile=Image.new('RGBA',image.size,(27,32,41,255));tile=Image.alpha_composite(tile,image);tile=tile.convert('RGB').resize((604,415));board.paste(tile,((i%2)*604,(i//2)*415))
ImageDraw.Draw(board).text((12,10),'SKY / ARCHITECTURE / ANGEL / FOREGROUND HOLD-OUT',fill='white');board.save(E/'depth/actual-plates.jpg',quality=94)
changes=[]
for f in (R/'src/main/java').rglob('*.java'):
    other=B/f.relative_to(R)
    if not other.exists() or sha(f)!=sha(other):changes.append(f.relative_to(R).as_posix())
assert all('Bgm' not in n and 'Music' not in n and 'SoundGate' not in n for n in changes)
bgm='src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg';assert sha(R/bgm)==sha(B/bgm)
assert json.loads((R/'src/main/resources/assets/akashiccinematic/sounds.json').read_text())['bgm.menu']==json.loads((B/'src/main/resources/assets/akashiccinematic/sounds.json').read_text())['bgm.menu']
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
codec=json.loads((E/'ceremonial-sfx.json').read_text());assert len(codec['cues'])==6
for cue in codec['cues']:
    f=R/'src/main/resources/assets/akashiccinematic/sounds/ritual'/(cue['name']+'.ogg');assert sha(f)==cue['sha256'];assert cue['peak_dbfs']<-8 and cue['mono_rms_ratio']>.75
observed=[];daily=Path.home()/'AppData/Roaming/.technic/modpacks/forbidden-series/mods'
for f in daily.glob('*CinematicMenu*.jar'):observed.append({'name':f.name,'sha256':sha(f)})
summary={'checkpoint':'ACM-N10-CEREMONIAL-2026-09-21','version':b['version'],'jar':str(jar),'jar_sha256':expected,'jar_bytes':jar.stat().st_size,'classes':b['classes'],'baseline_user_approved':'native-n09-audio-fixed/0.5.2-alpha.1','baseline_sha256':sha(B/'dist/AkashicCinematicMenu-0.5.2-alpha.1.jar'),'BGM_recording_unchanged':True,'BGM_recording_sha256':sha(R/bgm),'BGM_java_source_unchanged':True,'BGM_event_unchanged':True,'changed_java_sources':changes,'depth_planes':3,'draw_layers':4,'occluder':'shares architecture camera; protects the piers in front of the seated statue','source_dimensions':[1207,830],'padded_dimensions':[1255,878],'neutral_mean_absolute_channel_error':mean,'five_sizes_tested':[[854,480],[1024,768],[1366,768],[1920,1001],[2560,1440]],'new_unit_checks':4807,'native_graphics_checks':276,'pbuffer_process':probe,'graphics_driver_log':log,'reduced_motion_pixel_stability':True,'missing_layer_fallback_pixel_identical':True,'six_sfx_actual_jorbis_decode_passed':True,'audio_preview_remote_sha256':sha(E/'audio/Ceremonial-SFX-Audition.wav'),'daily_observed':observed,'daily_mutation_by_this_work':False,'qa_modpack_launched':False,'reason_no_full_pack_test':'Other user Java clients were open; free physical memory about1-2GiB. A private pbuffer and low-heap tests were used instead.','user_visual_audio_approval':'PENDING','verdict':'BUILD_AND_NATIVE_RENDERER_GATES_PASSED__FULL_PACK_RUNTIME_PENDING','general_release_approved':False,'limitations':['Three depth planes and an occlusion mask are2.5D, not a newly modelled3D statue','Masked hidden regions restored only for bounded camera travel; not unrestricted movement','Original source remains1207x830; no fabricated4K detail','Wordless choir is synthesized, not a recording of a Gregorian choir','No new cue was auditioned through Minecraft SoundHandler in this run','Pbuffer timing is not a modpack FPS benchmark','Existing unexplained earlier runtime exit remains historical; this does not resolve it']}
(R/'CHECKPOINT_N10.json').write_text(json.dumps(summary,indent=2),encoding='utf-8');(E/'final-audit.json').write_text(json.dumps(summary,indent=2),encoding='utf-8')
print(json.dumps({k:v for k,v in summary.items() if k not in ['changed_java_sources','graphics_driver_log','limitations']},indent=2),flush=True)
