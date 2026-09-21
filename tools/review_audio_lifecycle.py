from pathlib import Path
import json,hashlib
R=Path(__file__).resolve().parents[1];J=R/'src/main/java/br/com/redeakashic/cinematic/client'
p=J/'LocalMenuMusicPack.java';s=p.read_text();assert 'private void refresh()' in s;s=s.replace('private void refresh()','public void refresh()');p.write_text(s,newline='\n')
p=J/'MenuBgm.java';s=p.read_text();anchor='if(reloaded){reloaded=false;stop(mc);';assert s.count(anchor)==1;s=s.replace(anchor,'if(reloaded){reloaded=false;stop(mc);pack.refresh();')
anchor='        try{\n            if(mc.field_71441_e!=null)';assert s.count(anchor)==1;s=s.replace(anchor,'        try{\n            if(sound!=null&&sound.done){stop(mc);retryAt=now;}\n            if(mc.field_71441_e!=null)')
s=s.replace('&&!Boolean.getBoolean("akashic.cinematic.disabled")&&mc.func_147118_V()', '&&!Boolean.getBoolean("akashic.cinematic.disabled")&&!Boolean.getBoolean("akashic.cinematic.disableBgm")&&mc.func_147118_V()')
old='status=failed?"PLAYBACK_FAILED":"TRACK_"+pack.status();';assert s.count(old)==1;s=s.replace(old,'status=failed?"PLAYBACK_FAILED":pack.ready()?"WAITING_SOUND_REGISTRY_OR_DISABLED":"TRACK_"+pack.status();')
p.write_text(s,encoding='utf-8',newline='\n')
(R/'evidence/bgm-lifecycle-review.json').write_text(json.dumps({'found':'A fade that reached done before a rapid return must not leave an unplayable stale sound until startup timeout.','fix':'Release finished tickable sound before deciding a fresh start; preserve continuous reversal before silence.','reload':'Revalidate supplied file header at sound reload, not every frame. Missing/corrupt file releases vanilla music.','kill_switch':'akashic.cinematic.disableBgm','runtime':'PENDING'},indent=2));print('LIFECYCLE_EDGE_CASES_REVIEWED',flush=True)
