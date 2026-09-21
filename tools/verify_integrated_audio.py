from pathlib import Path
import subprocess,sys,json,zipfile,hashlib,os
C=Path(__file__).resolve().parents[1];E=C/'evidence';env=os.environ.copy();env['JAVA_TOOL_OPTIONS']='-Xmx160m';results=[]
steps=[('core',[sys.executable,str(C/'tools/test_core.py')])]
for name in ['MusicCoreTests','BgmPlaybackTests','MenuSoundGateTests']:steps.append((name,['java','-ea','-cp',str(C/'build/core-tests'),'br.com.redeakashic.cinematic.core.'+name]))
steps.append(('build',[sys.executable,str(C/'tools/build_native.py')]))
for name,cmd in steps:
    run=subprocess.run(cmd,capture_output=True,text=True,env=env,timeout=70);(E/(name+'-integrated.log')).write_text(run.stdout+run.stderr,encoding='utf-8');results.append({'test':name,'exit':run.returncode,'result_tail':(run.stdout+run.stderr)[-240:]});print(name,run.returncode,(run.stdout+run.stderr)[-200:],flush=True);assert run.returncode==0,name
b=json.loads((E/'native-build.json').read_text());jar=C/'dist'/b['jar'];sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();expected='6e91e747a99148084b854012c48ce93974beb6275dc4add2951989beff0ae4a6'
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;audio=z.read('assets/akashiccinematic/sounds/bgm/menu.ogg');assert hashlib.sha256(audio).hexdigest()==expected
    sound=json.loads(z.read('assets/akashiccinematic/sounds.json'));assert sound['bgm.menu']['category']=='music' and sound['bgm.menu']['sounds'][0]['stream'] is True
    assert not any(n.endswith(('.ttf','.otf','.woff','.woff2')) or 'cinematicqa' in n for n in z.namelist())
    assert 'br/com/redeakashic/cinematic/core/MenuSoundGate.class' in z.namelist()
report={'version':b['version'],'jar_path':str(jar),'jar_sha256':sha(jar),'jar_bytes':jar.stat().st_size,'audio_sha256':expected,'audio_bytes':len(audio),'source_user_mp3_sha256':'1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408','streaming':True,'music_category':'MUSIC','tests':results,'actual_runtime_this_version':False,'daily_pack_modified':False,'volume_changed':False,'runtime_constraint':'About1.28GiB free physical RAM and existing user Java clients; full modpack not launched alongside them.','verdict':'COMPILED_AND_UNIT_TESTED_RUNTIME_PENDING'}
(E/'integrated-audio-audit.json').write_text(json.dumps(report,indent=2),encoding='utf-8');print('ARTIFACT',json.dumps(report),flush=True)
