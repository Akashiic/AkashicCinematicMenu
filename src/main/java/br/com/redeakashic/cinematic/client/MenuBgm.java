package br.com.redeakashic.cinematic.client;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayDeque;
import br.com.redeakashic.cinematic.core.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.sound.*;
import net.minecraftforge.common.MinecraftForge;
/** One soundtrack voice, shared by frontend screens, using Minecraft's MUSIC/MASTER mixers. */
public final class MenuBgm {
    private static final ResourceLocation TRACK=new ResourceLocation("akashiccinematic","bgm.menu");
    private final MenuConfig config;private final LocalMenuMusicPack pack;
    private boolean reachedMain,failed,silencedTicker;private volatile boolean reloaded;
    private final ArrayDeque<ISound> observedVanilla=new ArrayDeque<ISound>();
    private MenuTrack sound;private long started,retryAt,lastPlaying;private int failures;private boolean heard;
    public static int starts,stops,blocked;public static float gain;public static String status="UNCONFIGURED";
    private MenuBgm(MenuConfig settings,LocalMenuMusicPack local){config=settings;pack=local;}
    @SuppressWarnings("unchecked") public static void install(MenuConfig settings,File configDirectory)throws ReflectiveOperationException{
        LocalMenuMusicPack pack=new LocalMenuMusicPack(new File(configDirectory,"akashiccinematic/bgm"));
        Minecraft mc=Minecraft.func_71410_x();Field f=Minecraft.class.getDeclaredField("field_110449_ao");f.setAccessible(true);
        ((List<Object>)f.get(mc)).add(pack);MenuBgm controller=new MenuBgm(settings,pack);
        MinecraftForge.EVENT_BUS.register(controller);FMLCommonHandler.instance().bus().register(controller);
        status=pack.ready()?"WAITING_MAIN_MENU":"TRACK_"+pack.status();System.out.println("[AkashicCinematic] Menu soundtrack "+status+"; local Ogg: "+pack.path());
    }
    public static int observedMusicEvents;public static String lastMusicEvent="none";
    public static ISound lastSound;public static boolean frontendActive;public static int reloadCount;
    private boolean frontend(Minecraft mc,GuiScreen gui){if(gui instanceof net.minecraft.client.gui.GuiDownloadTerrain||gui instanceof net.minecraft.client.multiplayer.GuiConnecting||gui instanceof net.minecraft.client.gui.GuiScreenWorking)return false;return MenuMusicPolicy.frontend(reachedMain,mc.field_71441_e!=null,gui==null?null:gui.getClass().getName());}
    private boolean ready(Minecraft mc){return config.bgmEnabled&&config.bgmVolume>0&&pack.ready()&&!failed&&!Boolean.getBoolean("akashic.cinematic.disabled")&&!Boolean.getBoolean("akashic.cinematic.disableBgm")&&mc.func_147118_V().func_147680_a(TRACK)!=null;}
    @SubscribeEvent(priority=EventPriority.LOWEST) public void open(GuiOpenEvent event){
        if(event.gui instanceof CinematicMenu)reachedMain=true;
        else if(event.gui!=null&&ScreenPolicy.accepts(event.gui.getClass().getName(),true))reachedMain=false;
        Minecraft mc=Minecraft.func_71410_x();if(mc!=null&&sound!=null&&!frontend(mc,event.gui))sound.aim(0,config.bgmFadeOut);
    }
    @SubscribeEvent public void loaded(SoundLoadEvent event){reloaded=true;}
    @SubscribeEvent(priority=EventPriority.HIGHEST) public void play(PlaySoundEvent17 event){
        Minecraft mc=Minecraft.func_71410_x();if(mc==null||event.sound==null||mc.field_71474_y==null)return;
        ResourceLocation loc=event.sound.func_147650_b();
        if(TRACK.equals(loc)||("minecraft".equals(loc.func_110624_b())&&loc.func_110623_a().startsWith("music."))){
            observedMusicEvents++;lastMusicEvent=loc+" category="+event.category+" frontend="+frontend(mc,mc.field_71462_r)+" ready="+ready(mc)+" priorNull="+(event.result==null);
            if(observedMusicEvents<=12)System.out.println("[AkashicCinematic] BGM_EVENT "+lastMusicEvent);
        }
        if(event.sound==sound)return;
        if(MenuMusicPolicy.suppressVanilla(frontend(mc,mc.field_71462_r),ready(mc),loc.func_110624_b(),loc.func_110623_a(),event.category==SoundCategory.MUSIC)){
            event.result=null;blocked++;
        }else if("minecraft".equals(loc.func_110624_b())&&loc.func_110623_a().startsWith("music.")){
            synchronized(observedVanilla){if(!observedVanilla.contains(event.sound)){while(observedVanilla.size()>=8)observedVanilla.removeFirst();observedVanilla.addLast(event.sound);}}
        }
    }
    private void silenceTicker(Minecraft mc)throws ReflectiveOperationException{
        ISound[] previous;synchronized(observedVanilla){previous=observedVanilla.toArray(new ISound[0]);observedVanilla.clear();}
        for(ISound voice:previous)mc.func_147118_V().func_147683_b(voice);
        Field tf=Minecraft.class.getDeclaredField("field_147126_aw");tf.setAccessible(true);Object ticker=tf.get(mc);if(ticker==null)return;
        Field sf=MusicTicker.class.getDeclaredField("field_147678_c");sf.setAccessible(true);Object value=sf.get(ticker);
        if(value instanceof ISound){ISound playing=(ISound)value;ResourceLocation loc=playing.func_147650_b();
            if("minecraft".equals(loc.func_110624_b())&&loc.func_110623_a().startsWith("music."))mc.func_147118_V().func_147683_b(playing);}
    }
    private void stop(Minecraft mc){if(sound!=null){sound.done=true;mc.func_147118_V().func_147683_b(sound);sound=null;gain=0;stops++;}}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.func_71410_x();if(mc==null||mc.field_71474_y==null)return;
        if(mc.field_71462_r instanceof CinematicMenu)reachedMain=true;
        if(reloaded){reloaded=false;stop(mc);pack.refresh();failed=false;failures=0;retryAt=System.nanoTime()+500_000_000L;silencedTicker=false;reloadCount++;}
        boolean front=frontend(mc,mc.field_71462_r);frontendActive=front;long now=System.nanoTime();
        boolean audible=mc.field_71474_y.func_151438_a(SoundCategory.MASTER)>0&&mc.field_71474_y.func_151438_a(SoundCategory.MUSIC)>0;
        try{
            if(sound!=null&&sound.done){stop(mc);retryAt=now;}
            if(mc.field_71441_e!=null){stop(mc);reachedMain=false;silencedTicker=false;status="GAMEPLAY_VANILLA";return;}
            if(!audible){stop(mc);status="MUTED";return;}
            if(!ready(mc)){stop(mc);silencedTicker=false;status=failed?"PLAYBACK_FAILED":pack.ready()?"WAITING_SOUND_REGISTRY_OR_DISABLED":"TRACK_"+pack.status();return;}
            if(!front)silencedTicker=false;
            if(sound!=null){
                sound.aim(front?1:0,front?config.bgmFadeIn:config.bgmFadeOut);
                if(sound.fade.silent(now)){stop(mc);status="FRONTEND_EXITED";return;}
                boolean playing=mc.func_147118_V().func_147692_c(sound);if(playing){lastPlaying=now;if(now-started>2_000_000_000L)heard=true;}
                if(now-started>10_000_000_000L&&!playing&&now-lastPlaying>1_500_000_000L){
                    stop(mc);retryAt=now+2_000_000_000L;if(!heard&&++failures>=2)failed=true;
                    status=failed?"PLAYBACK_FAILED":"WAITING_AUDIO_RESTART";return;
                }
                status=front?"PLAYING":"FADING_OUT";
            }else if(front&&now>=retryAt){
                if(!silencedTicker){silenceTicker(mc);silencedTicker=true;}
                sound=new MenuTrack(config.bgmVolume,config.bgmFadeIn);lastSound=sound;heard=false;started=now;lastPlaying=now;
                mc.func_147118_V().func_147682_a(sound);starts++;status="FADING_IN";
                System.out.println("[AkashicCinematic] Menu BGM started; single streaming voice #"+starts);
            }
        }catch(ReflectiveOperationException|RuntimeException|LinkageError ex){
            failed=true;stop(mc);status="PLAYBACK_FAILED";System.err.println("[AkashicCinematic] Menu BGM unavailable; vanilla music released: "+ex);
        }
    }
    private static final class MenuTrack extends PositionedSound implements ITickableSound {
        final MusicFade fade=new MusicFade();final double level;boolean done;
        MenuTrack(double volume,double seconds){super(TRACK);level=volume;field_147662_b=.0001f;field_147663_c=1;field_147666_i=ISound.AttenuationType.NONE;field_147659_g=true;field_147665_h=0;fade.aim(1,seconds,System.nanoTime());}
        void aim(double value,double seconds){fade.aim(value,seconds,System.nanoTime());}
        @Override public void func_73660_a(){long now=System.nanoTime();field_147662_b=(float)(level*fade.sample(now));gain=field_147662_b;if(fade.silent(now))done=true;}
        @Override public boolean func_147667_k(){return done;}
    }
}
