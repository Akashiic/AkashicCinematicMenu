from pathlib import Path
import json,hashlib,zipfile,shutil,subprocess,time
P=Path(__file__).resolve().parents[1];B=P/'candidates/native-n07';R=P/'candidates/native-n08';assert not R.exists(),'Inspect existing candidate instead of overwriting'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
assert sha(B/'dist/AkashicCinematicMenu-0.4.0-alpha.3.jar')=='14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac'
R.mkdir();(R/'research').mkdir();(R/'evidence').mkdir();(R/'docs').mkdir();[shutil.copytree(B/n,R/n) for n in ['src','tools']]
for n in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/n;s=p.read_text(encoding='utf-8');assert '0.4.0-alpha.3' in s;p.write_text(s.replace('0.4.0-alpha.3','0.5.0-alpha.1'),encoding='utf-8',newline='\n')
pack=Path.home()/'AppData/Roaming/.technic/modpacks/forbidden-series';matches=[]
for jar in sorted((pack/'mods').glob('*.jar')):
    with zipfile.ZipFile(jar) as z:
        for info in z.infolist():
            if info.file_size>1500000 or not info.filename.endswith(('.class','.lang','.json','.txt','.cfg')):continue
            data=z.read(info)
            if any(p in data for p in [b'Always bring tons of logs',b'Pro Tip:',b'Pro tip:']):matches.append({'jar':jar.name,'entry':info.filename,'exact_tip':b'Always bring tons of logs' in data})
(R/'research/tip-origin.json').write_text(json.dumps(matches,indent=2));print('TIP_ORIGIN',json.dumps(matches),flush=True)
cp=Path.home()/'AkashicDev/CubicChunks-Crucible/source/build/rfg';classes=['net.minecraft.client.gui.GuiDownloadTerrain','net.minecraft.client.LoadingScreenRenderer','net.minecraft.client.audio.SoundHandler','net.minecraft.client.audio.ITickableSound','net.minecraft.client.audio.PositionedSound','net.minecraft.client.audio.MusicTicker','net.minecraft.client.audio.ISound','net.minecraft.client.gui.GuiScreen']
for label,jar,names in [('minecraft',cp/'srg_merged_minecraft.jar',classes),('forge',cp/'recompiled_minecraft-1.7.10.jar',['net.minecraftforge.client.event.sound.PlaySoundEvent17','net.minecraftforge.client.event.sound.SoundLoadEvent','net.minecraftforge.client.event.GuiScreenEvent','net.minecraft.client.resources.IResourcePack','net.minecraft.client.audio.SoundEventAccessorComposite'])]:
    out=subprocess.run(['javap','-p','-c','-classpath',str(jar)]+names,capture_output=True,text=True,timeout=25);(R/'research'/(label+'-apis.txt')).write_text(out.stdout+out.stderr,encoding='utf-8');print('JAVAP',label,out.returncode,len(out.stdout),flush=True)
(R/'evidence/baseline.json').write_text(json.dumps({'baseline':'native-n07','baseline_sha256':sha(B/'dist/AkashicCinematicMenu-0.4.0-alpha.3.jar'),'artist_track_requested':'Fluxxwave (Slowed + Reverb)','track_file_provided':False,'rights_asset_note':'Do not download/rip or substitute a recording. Engine can be built without copyrighted track.'},indent=2))
