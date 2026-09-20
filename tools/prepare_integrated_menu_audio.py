from pathlib import Path
import shutil,hashlib,json
P=Path(__file__).resolve().parents[1];B=P/'candidates/native-n08-bgm-r1';C=P/'candidates/native-n09-audio-fixed';assert not C.exists(),'Existing candidate must be inspected'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();assert sha(B/'dist/AkashicCinematicMenu-0.5.0-alpha.1.jar')=='c9248a3060139e5334facd6186e9c09f6826af650508e6c20d1438bc52975b0d'
C.mkdir();(C/'evidence').mkdir();(C/'docs').mkdir()
for n in ['src','tools']:shutil.copytree(B/n,C/n)
track=P/'candidates/native-n08-bgm-6c79c577/local-assets/akashiccinematic/bgm/menu.ogg';assert sha(track)=='6e91e747a99148084b854012c48ce93974beb6275dc4add2951989beff0ae4a6'
t=C/'src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg';t.parent.mkdir(exist_ok=True);shutil.copy2(track,t)
for n in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=C/n;s=p.read_text(encoding='utf-8');assert '0.5.0-alpha.1' in s,n;p.write_text(s.replace('0.5.0-alpha.1','0.5.2-alpha.1'),encoding='utf-8',newline='\n')
p=C/'src/main/java/br/com/redeakashic/cinematic/client/MenuBgm.java';s=p.read_text();s=s.replace('import br.com.redeakashic.cinematic.core.MenuMusicPolicy;','import br.com.redeakashic.cinematic.core.MenuSoundGate;')
a=s.index('    @SubscribeEvent(priority=EventPriority.LOWEST) public void sound(');b=s.index('    @SubscribeEvent public void tick(',a)
replacement='''    @SubscribeEvent(priority=EventPriority.HIGHEST) public void soundEarly(PlaySoundEvent17 e){filter(e);}
    @SubscribeEvent(priority=EventPriority.LOWEST) public void sound(PlaySoundEvent17 e){filter(e);}
    private void filter(PlaySoundEvent17 event){
        if(event.result==null)return;ResourceLocation loc=event.result.func_147650_b();if(loc==null)return;
        if(!MenuSoundGate.competing(loc.func_110624_b(),loc.func_110623_a(),event.category==SoundCategory.MUSIC))return;
        Minecraft mc=Minecraft.func_71410_x();boolean muted=mc!=null&&mc.field_71474_y!=null&&(mc.field_71474_y.func_151438_a(SoundCategory.MASTER)<=0||mc.field_71474_y.func_151438_a(SoundCategory.MUSIC)<=0);
        boolean replace=mc!=null&&!faulted&&settings.enabled&&settings.volume>0&&frontend(mc)&&(playback.snapshot().suppressVanilla||(muted&&backend.validated));
        if(replace){event.result=null;blocked++;return;}
        synchronized(trackedVanilla){if(!trackedVanilla.contains(event.result)){if(trackedVanilla.size()==16)trackedVanilla.removeFirst();trackedVanilla.addLast(event.result);}}
    }
'''
s=s[:a]+replacement+s[b:];s=s.replace('        private boolean validated;','        private volatile boolean validated;');anchor='            playback.update(context,audible,now);';assert s.count(anchor)==1;s=s.replace(anchor,anchor+'\n            if(enabled&&settings.volume>0&&!audible&&frontend(mc)&&backend.available())backend.silenceTrackedVanilla();');p.write_text(s,encoding='utf-8',newline='\n')
(C/'evidence/baseline.json').write_text(json.dumps({'installed_base_sha256':sha(B/'dist/AkashicCinematicMenu-0.5.0-alpha.1.jar'),'recording_sha256':sha(t),'no_daily_mutation':True,'changes':'Bundle exact provided recording; category-independent known music IDs plus mod MUSIC events; menu-only ownership; respect MUSIC/MASTER zero; preserve settings and frontend policy'},indent=2));print('INTEGRATED_CANDIDATE_PREPARED',C,flush=True)
