from pathlib import Path
import json,zipfile
R=Path(__file__).resolve().parents[1];J=R/'src/main/java/br/com/redeakashic/cinematic';backup=R/'evidence/alpha1-source-before-audio-boundary.zip';assert not backup.exists()
with zipfile.ZipFile(backup,'x',compression=zipfile.ZIP_DEFLATED) as z:
    for p in J.rglob('*.java'):z.write(p,p.relative_to(R).as_posix())
p=J/'core/MenuMusicPolicy.java';s=p.read_text();assert '&&replacementReady&&musicCategory&&' in s;s=s.replace('&&replacementReady&&musicCategory&&','&&replacementReady&&');p.write_text(s,newline='\n')
p=J/'client/MenuBgm.java';s=p.read_text(encoding='utf-8');s=s.replace('    public static ISound lastSound;','    public static int observedMusicEvents;public static String lastMusicEvent="none";\n    public static ISound lastSound;')
s=s.replace('@SubscribeEvent(priority=EventPriority.LOWEST) public void play','@SubscribeEvent(priority=EventPriority.HIGHEST) public void play')
s=s.replace('if(mc==null||event.result==null||event.sound==sound||event.sound==null||mc.field_71474_y==null)return;','if(mc==null||event.sound==null||mc.field_71474_y==null)return;')
anchor='        ResourceLocation loc=event.sound.func_147650_b();';assert s.count(anchor)==1
extra='''        if(TRACK.equals(loc)||("minecraft".equals(loc.func_110624_b())&&loc.func_110623_a().startsWith("music."))){
            observedMusicEvents++;lastMusicEvent=loc+" category="+event.category+" frontend="+frontend(mc,mc.field_71462_r)+" ready="+ready(mc)+" priorNull="+(event.result==null);
            if(observedMusicEvents<=12)System.out.println("[AkashicCinematic] BGM_EVENT "+lastMusicEvent);
        }
        if(event.sound==sound)return;'''
s=s.replace(anchor,anchor+'\n'+extra);p.write_text(s,encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.5.1-alpha.1' in s;p.write_text(s.replace('0.5.1-alpha.1','0.5.1-alpha.2'),encoding='utf-8',newline='\n')
p=R/'src/test/java/br/com/redeakashic/cinematic/core/MenuMusicTests.java';s=p.read_text();anchor='        System.out.println("RESULT music_checks="';assert s.count(anchor)==1;s=s.replace(anchor,'        check(MenuMusicPolicy.suppressVanilla(true,true,"minecraft","music.menu",false),"resource identity remains authoritative if another mod changes category metadata");\n'+anchor);p.write_text(s,newline='\n')
(R/'evidence/audio-boundary-review.json').write_text(json.dumps({'first_run':'verify1 reached menu and faded supplied audio; rejected vanilla suppression/continuous playback test','change':'Guard exact minecraft music namespace before other listeners; do not depend on mutable category metadata. Log bounded event decisions.','no_aether_world_music_changes':True,'aether_menu_setting_observed':False,'real_cause_of_prior_restart':'not yet conclusively isolated; event diagnostics required','original_recording_changed':False},indent=2));print('ALPHA2_BOUNDARY_AND_DIAGNOSTICS_READY',flush=True)
