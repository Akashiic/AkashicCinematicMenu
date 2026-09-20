from pathlib import Path
import json,subprocess,sys,hashlib
R=Path(__file__).resolve().parents[1]
p=R/'src/main/java/br/com/redeakashic/cinematic/bootstrap/CinematicLoadingPlugin.java';s=p.read_text();old='new String[]{"br.com.redeakashic.cinematic.bootstrap.SplashTransformer"}';assert s.count(old)==1;s=s.replace(old,'new String[]{"br.com.redeakashic.cinematic.bootstrap.EarlyWindowTransformer","br.com.redeakashic.cinematic.bootstrap.SplashTransformer"}');p.write_text(s,newline='\n')
p=R/'src/main/java/br/com/redeakashic/cinematic/bootstrap/SplashHooks.java';s=p.read_text(encoding='utf-8');old='if(started==0)started=now;';assert s.count(old)==1;s=s.replace(old,'if(started==0){started=EarlyWindowHooks.startedNanos>0?EarlyWindowHooks.startedNanos:now;System.out.println("[AkashicCinematic] FORGE_OVERLAY_STARTED epoch="+System.currentTimeMillis());}')
p.write_text(s,encoding='utf-8',newline='\n')
for name,args in [('core',[sys.executable,str(R/'tools/test_core.py')]),('policy',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.ConnectionWatchTests']),('build',[sys.executable,str(R/'tools/build_native.py')])]:
    res=subprocess.run(args,capture_output=True,text=True,timeout=60);(R/'evidence'/(name+'-n04.log')).write_text(res.stdout+res.stderr,encoding='utf-8');print(name,res.returncode,(res.stdout+res.stderr)[-250:],flush=True);assert res.returncode==0
old=R.parent/'native-n03/src/main/resources/assets';new=R/'src/main/resources/assets';files=[f for f in new.rglob('*') if f.is_file()];assert all(hashlib.sha256(f.read_bytes()).digest()==hashlib.sha256((old/f.relative_to(new)).read_bytes()).digest() for f in files)
print('ASSET_HASHES_UNCHANGED',len(files));print((R/'evidence/native-build.json').read_text(),flush=True)
