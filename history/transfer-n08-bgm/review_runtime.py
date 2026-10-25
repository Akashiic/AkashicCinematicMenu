from pathlib import Path
import json,time,sys
from PIL import Image
Q=Path(__file__).resolve().parents[1]/'qa-n02';E=Q/'evidence'/sys.argv[1];seq=int(time.time()*1000);checks=[]
expected_audio=json.loads((Q/'evidence/n08-setup/deployment.json').read_text())['audio_in_runtime']
def state():
    try:return dict(l.split('=',1) for l in (E/'state.txt').read_text().splitlines() if '=' in l)
    except OSError:return {}
def events():return (E/'events.log').read_text() if (E/'events.log').exists() else ''
def wait(f,seconds=15):
    end=time.monotonic()+seconds
    while time.monotonic()<end:
        if f():return True
        if 'exit' in json.loads((E/'process.json').read_text()):return False
        time.sleep(.1)
    return False
def record(name,passed,detail=None):
    checks.append({'name':name,'passed':bool(passed),'detail':detail});(E/'acceptance.json').write_text(json.dumps(checks,indent=2));print(name,passed,str(detail)[:230],flush=True);assert passed,name
def command(value):
    global seq;seq+=1;(E/'command.tmp').write_text(str(seq)+' '+value);(E/'command.tmp').replace(E/'command.txt');assert wait(lambda:'DONE '+str(seq)+'\n' in events() or 'FAILED '+str(seq)+' ' in events()),value;assert 'FAILED '+str(seq)+' ' not in events(),value
    time.sleep(.3)
def picture(command_name,marker,label):
    folder=E/'screenshots';before=set(folder.glob('*.png'));count=events().count(marker);command(command_name);assert wait(lambda:events().count(marker)>count)
    path=next(iter(set(folder.glob('*.png'))-before));im=Image.open(path);im.load();out=E/'review';out.mkdir(exist_ok=True);im.thumbnail((1200,800));im.save(out/(label+'.jpg'),quality=95);record(label+'_captured',True,{'file':path.name,'size':Image.open(path).size})
try:
    record('whole_pack_reaches_menu',wait(lambda:state().get('screen','').endswith('CinematicMenu'),1000),state())
    record('startup_rendered',state().get('splashRendered')=='true' and state().get('splashFailed')=='false')
    command('volume 0.15');command('musicvolume 1');time.sleep(3)
    if expected_audio:record('exact_track_confirmed_playing',state().get('bgmStatus')=='PLAYING' and state().get('bgmPlaying')=='true',state())
    else:record('missing_track_keeps_vanilla_available',state().get('bgmStatus')=='MISSING' and state().get('bgmStarts')=='0' and state().get('bgmBlocked')=='0',state())
    picture('loadingdemo','LOADING_CAPTURE_COMPLETE aether','aether-original-loader-themed');record('actual_aether_hook_executed',int(state().get('aetherFrames','0'))>0,state())
    picture('vanillaloadingdemo','LOADING_CAPTURE_COMPLETE vanilla','vanilla-original-loader-themed');record('actual_vanilla_loading_hook_executed',int(state().get('vanillaLoadingFrames','0'))>0,state())
    picture('capture','CAPTURE_WRITTEN ','menu-returned');record('no_world_created',state().get('worldPresent')=='false')
    command('musicvolume 0');record('music_mute_stops_own_track',wait(lambda:state().get('bgmStatus')=='MUTED' and state().get('bgmPlaying')=='false'),state())
    command('musicvolume 1');time.sleep(3)
    if expected_audio:record('unmute_restarts_one_track',state().get('bgmStatus')=='PLAYING' and state().get('bgmPlaying')=='true',state())
    else:record('unmute_missing_track_no_retry_storm',state().get('bgmStarts')=='0' and state().get('bgmBlocked')=='0' and state().get('bgmStatus')=='MISSING',state())
    command('disconnectdemo');picture('capture','CAPTURE_WRITTEN ','disconnect-continuity');command('button 0')
    record('return_to_our_menu',wait(lambda:state().get('screen','').endswith('CinematicMenu')),state())
    record('no_monitored_gl_errors',state().get('glErrors')=='0',state())
    command('volume 0');command('close');record('normal_shutdown_requested',True)
except Exception as ex:
    checks.append({'name':'suite_stopped','passed':False,'detail':repr(ex),'state':state()});(E/'acceptance.json').write_text(json.dumps(checks,indent=2));print('STOPPED',repr(ex),state(),flush=True)
    try:command('volume 0');command('close')
    except Exception:pass
    sys.exit(1)
