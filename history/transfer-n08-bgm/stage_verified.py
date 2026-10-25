from pathlib import Path
import base64,hashlib,io,json,zipfile,shutil
P=Path(__file__).resolve().parents[1];T=Path(__file__).resolve().parent;R=P/'candidates/native-n08-bgm-r1';B=P/'candidates/native-n07';partial=P/'candidates/native-n08'
raw=base64.b64decode(''.join((T/f'part{i}.b64').read_text().strip() for i in range(4)),validate=True)
assert len(raw)==16006 and hashlib.sha256(raw).hexdigest()=='b76de5670adcbd892142d7556e8ddcedd3d0959b85cda4626e105a16fbc60bc6'
with zipfile.ZipFile(io.BytesIO(raw)) as z:
    assert z.testzip() is None
    assert all(n.startswith('src/') and n.endswith('.java') and '..' not in Path(n).parts for n in z.namelist())
    assert not R.exists(),'Candidate exists; inspect receipt instead of re-running staging'
    assert hashlib.sha256((B/'dist/AkashicCinematicMenu-0.4.0-alpha.3.jar').read_bytes()).hexdigest()=='14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac'
    R.mkdir();(R/'evidence').mkdir();(R/'docs').mkdir()
    for name in ['src','tools']:shutil.copytree(B/name,R/name)
    for name in ['bootstrap/LoadingThemeTransformer.java','client/TransitionTheme.java']:
        rel='src/main/java/br/com/redeakashic/cinematic/'+name;p=partial/rel;assert p.read_text(encoding='utf-8').rstrip().endswith('}');shutil.copy2(p,R/rel)
    z.extractall(R)
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.4.0-alpha.3' in s;p.write_text(s.replace('0.4.0-alpha.3','0.5.0-alpha.1'),encoding='utf-8',newline='\n')
j=R/'src/main/java/br/com/redeakashic/cinematic'
p=j/'client/MenuConfig.java';s=p.read_text(encoding='utf-8');s=s.replace('suppressBogoConflict,themeConnections;','suppressBogoConflict,themeConnections,themeTransitions;');anchor='        themeConnections=c.get(';assert s.count(anchor)==1;s=s.replace(anchor,'        themeTransitions=c.get("screens","themeLoadingTransitions",true,"Only audited Aether, vanilla loading and terrain backgrounds.").getBoolean(true);\n'+anchor);p.write_text(s,encoding='utf-8',newline='\n')
p=j/'client/ClientProxy.java';s=p.read_text(encoding='utf-8');anchor='            UiAudio.configure(config);';assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n            TransitionTheme.configure(config.themeTransitions);\n            if(config.themeTransitions)cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new TransitionTheme());\n            try{MenuBgm.install(event.getSuggestedConfigurationFile().getParentFile());}\n            catch(RuntimeException|LinkageError ex){event.getModLog().error("BGM unavailable; original music retained.",ex);}')
p.write_text(s,encoding='utf-8',newline='\n')
p=j/'bootstrap/CinematicLoadingPlugin.java';s=p.read_text();anchor='new String[]{';assert s.count(anchor)==1;s=s.replace(anchor,anchor+'"br.com.redeakashic.cinematic.bootstrap.LoadingThemeTransformer",');p.write_text(s,newline='\n')
p=R/'src/main/resources/assets/akashiccinematic/sounds.json';events=json.loads(p.read_text());assert 'bgm.menu' not in events;events['bgm.menu']={'category':'music','sounds':[{'name':'akashiccinematic:bgm/menu','stream':True}]};p.write_text(json.dumps(events,indent=2),encoding='utf-8')
receipt={'base':'native-n07','partial_n08_untouched':True,'overlay_sha256':hashlib.sha256(raw).hexdigest(),'original_mp3_provided':True,'original_mp3_sha256':'1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408','ogg_sha256':'66db58689c1745385109a2ee9e5b5757f2d57f3f692d20ed13e9a7d084999db3','audio_in_pc_not_yet_verified':True}
(R/'evidence/staging-receipt.json').write_text(json.dumps(receipt,indent=2));print('STAGED_AND_INTEGRATED',R,flush=True)
