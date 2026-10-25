from pathlib import Path
import hashlib,json,shutil,subprocess,sys,time,zipfile
P=Path(__file__).resolve().parents[1];Q=P/'qa-n02';R=P/'candidates/native-n08-bgm-r1';E=Q/'evidence/n08-setup';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
track=Path.home()/'Downloads/menu.ogg';expectedTrack='66db58689c1745385109a2ee9e5b5757f2d57f3f692d20ed13e9a7d084999db3';hasTrack=track.exists()
if hasTrack:
    assert sha(track)==expectedTrack,'Audio does not match the exact supplied recording conversion'
    destination=R/'src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg';destination.parent.mkdir(exist_ok=True);assert not destination.exists();shutil.copy2(track,destination)
    result=subprocess.run([sys.executable,str(R/'tools/build_native.py')],capture_output=True,text=True,timeout=50);(R/'evidence/build-with-audio.log').write_text(result.stdout+result.stderr);assert result.returncode==0
b=json.loads((R/'evidence/native-build.json').read_text());candidate=R/'dist'/b['jar'];assert sha(candidate)==b['sha256'];old=Q/'instance/mods/AkashicCinematicMenu-0.4.0-alpha.3.jar';assert sha(old)=='14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac'
assert not (E/'deployment.json').exists(),'Previous deployment must be revalidated'
qa=Q/'instance/mods/zz-AkashicCinematic-QA-only.jar';shutil.copy2(qa,E/'previous-qa-driver.jar');old.rename(E/old.name);shutil.copy2(candidate,old.parent/candidate.name);shutil.copy2(E/'qa-driver-v8.jar',qa)
assert sha(old.parent/candidate.name)==b['sha256'];assert sha(qa)==sha(E/'qa-driver-v8.jar')
report={'candidate':b,'source_mp3_received':True,'audio_in_runtime':hasTrack,'audio_sha256':expectedTrack if hasTrack else None,'track_transfer_pending':not hasTrack,'daily_pack_modified':False,'at':time.time()}
(E/'deployment.json').write_text(json.dumps(report,indent=2));(R/'evidence/runtime-candidate.json').write_text(json.dumps(report,indent=2));print('DEPLOYMENT',json.dumps(report),flush=True)
