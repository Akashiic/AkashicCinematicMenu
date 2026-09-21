package br.com.redeakashic.cinematic.client;

import br.com.redeakashic.cinematic.core.BgmFrontendPolicy;
import br.com.redeakashic.cinematic.core.BgmPlayback;
import br.com.redeakashic.cinematic.core.MenuSoundGate;
import br.com.redeakashic.cinematic.core.VorbisHeader;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Forge 1.7.10 adapter. Source is prepared against the previously inspected SRG API.
 * Full compilation and playback in the exact modpack are required before promotion.
 */
public final class MenuBgm {
    public static final ResourceLocation TRACK=new ResourceLocation("akashiccinematic","bgm.menu");
    private static final ResourceLocation AUDIO=new ResourceLocation("akashiccinematic","sounds/bgm/menu.ogg");
    private static MenuBgm instance;
    public static volatile String status="NOT_INSTALLED", resourceStatus="UNCHECKED";
    public static volatile double gain;
    public static volatile int starts,stops,blocked,failures;
    public static volatile ISound lastSound;
    private final MenuBgmSettings settings;
    private final Engine backend=new Engine();
    private final BgmPlayback playback;
    private final AtomicBoolean reload=new AtomicBoolean();
    private final ArrayDeque<ISound> trackedVanilla=new ArrayDeque<ISound>();
    private volatile boolean reachedMain, faulted;
    private MenuBgm(MenuBgmSettings settings){this.settings=settings;playback=new BgmPlayback(backend,settings.volume,settings.fadeIn,settings.fadeOut,3,settings.loop);}

    public static synchronized void install(File configDirectory){
        if(instance!=null||Boolean.getBoolean("akashic.cinematic.disabled"))return;
        MenuBgm candidate=new MenuBgm(MenuBgmSettings.read(configDirectory));
        // Register before ordinary music can start. Never replace Minecraft's MusicTicker.
        MinecraftForge.EVENT_BUS.register(candidate);
        FMLCommonHandler.instance().bus().register(candidate);
        instance=candidate;status="ARMED_FOR_MAIN_MENU";
        System.out.println("[AkashicCinematic] BGM controller registered; waiting for the main menu and audio resource.");
    }
    private static String screen(Minecraft mc){GuiScreen g=mc.field_71462_r;return g==null?null:g.getClass().getName();}
    private boolean frontend(Minecraft mc){return BgmFrontendPolicy.frontend(reachedMain,mc.field_71441_e!=null,screen(mc));}

    @SubscribeEvent public void reloaded(SoundLoadEvent event){reload.set(true);}
    @SubscribeEvent(priority=EventPriority.HIGHEST) public void soundEarly(PlaySoundEvent17 e){filter(e);}
    @SubscribeEvent(priority=EventPriority.LOWEST) public void sound(PlaySoundEvent17 e){filter(e);}
    private void filter(PlaySoundEvent17 event){
        if(event.result==null)return;ResourceLocation loc=event.result.func_147650_b();if(loc==null)return;
        if(!MenuSoundGate.competing(loc.func_110624_b(),loc.func_110623_a(),event.category==SoundCategory.MUSIC))return;
        Minecraft mc=Minecraft.func_71410_x();boolean muted=mc!=null&&mc.field_71474_y!=null&&(mc.field_71474_y.func_151438_a(SoundCategory.MASTER)<=0||mc.field_71474_y.func_151438_a(SoundCategory.MUSIC)<=0);
        boolean replace=mc!=null&&!faulted&&settings.enabled&&settings.volume>0&&frontend(mc)&&(playback.snapshot().suppressVanilla||(muted&&backend.validated));
        if(replace){event.result=null;blocked++;return;}
        synchronized(trackedVanilla){if(!trackedVanilla.contains(event.result)){if(trackedVanilla.size()==16)trackedVanilla.removeFirst();trackedVanilla.addLast(event.result);}}
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.func_71410_x();
        if(mc==null||mc.field_71474_y==null||mc.func_147118_V()==null)return;
        long now=System.nanoTime();
        try{
            if(reload.getAndSet(false)){
                playback.reset(now);backend.invalidate();faulted=false;
                synchronized(trackedVanilla){trackedVanilla.clear();}
            }
            String current=screen(mc);if(BgmFrontendPolicy.root(current)&&mc.field_71441_e==null)reachedMain=true;
            boolean enabled=settings.enabled&&!faulted&&!Boolean.getBoolean("akashic.cinematic.disabled");
            boolean audible=mc.field_71474_y.func_151438_a(SoundCategory.MASTER)>0&&mc.field_71474_y.func_151438_a(SoundCategory.MUSIC)>0;
            BgmPlayback.Context context=BgmFrontendPolicy.context(enabled,reachedMain,mc.field_71441_e!=null,current);
            playback.update(context,audible,now);
            if(enabled&&settings.volume>0&&!audible&&frontend(mc)&&backend.available())backend.silenceTrackedVanilla();
            BgmPlayback.Snapshot snapshot=playback.snapshot();gain=snapshot.gain;starts=snapshot.starts;stops=snapshot.stops;failures=snapshot.failedStarts;
            status=(!reachedMain?"WAITING_MAIN_MENU":snapshot.phase.name());
        }catch(RuntimeException|LinkageError ex){
            if(!faulted)System.err.println("[AkashicCinematic] BGM adapter disabled after failure: "+ex);
            faulted=true;status="ADAPTER_FAILED";
            try{backend.stopLast();}catch(RuntimeException|LinkageError ignored){/* Do not stop any other sound. */}
        }
    }
    private final class Engine implements BgmPlayback.Backend {
        private volatile boolean validated;
        private long nextValidation;
        private String lastResourceProblem="";
        private MenuTrack owned;
        void invalidate(){validated=false;nextValidation=0;lastResourceProblem="";resourceStatus="UNCHECKED";}
        @Override public boolean available(){
            Minecraft mc=Minecraft.func_71410_x();if(mc==null||mc.func_147118_V()==null)return false;
            if(validated)return true;
            long now=System.nanoTime();if(now<nextValidation)return false;nextValidation=now+1_000_000_000L;
            try{
                SoundEventAccessorComposite accessor=mc.func_147118_V().func_147680_a(TRACK);
                if(accessor==null||accessor.func_148728_d()!=SoundCategory.MUSIC){problem("MISSING_MUSIC_EVENT");return false;}
                IResource resource=mc.func_110442_L().func_110536_a(AUDIO);
                try(InputStream in=resource.func_110527_b()){VorbisHeader.read(in);}
                validated=true;resourceStatus="READY";return true;
            }catch(Exception ex){problem("MISSING_OR_INVALID_OGG");return false;}
        }
        private void problem(String value){resourceStatus=value;if(!value.equals(lastResourceProblem)){
            lastResourceProblem=value;System.err.println("[AkashicCinematic] BGM "+value+"; vanilla music remains available.");}}
        @Override public Object start(float bootstrapGain){
            if(owned!=null)throw new IllegalStateException("Duplicate BGM owner");
            MenuTrack candidate=new MenuTrack(bootstrapGain,settings.loop);owned=candidate;lastSound=candidate;
            try{Minecraft.func_71410_x().func_147118_V().func_147682_a(candidate);return candidate;}
            catch(RuntimeException|LinkageError ex){candidate.done=true;owned=null;try{Minecraft.func_71410_x().func_147118_V().func_147683_b(candidate);}catch(RuntimeException ignored){}throw ex;}
        }
        @Override public boolean playing(Object voice){return Minecraft.func_71410_x().func_147118_V().func_147692_c((ISound)voice);}
        @Override public void gain(Object voice,float value){((MenuTrack)voice).setGain(value);}
        @Override public void stop(Object voice){
            MenuTrack track=(MenuTrack)voice;track.done=true;
            try{Minecraft.func_71410_x().func_147118_V().func_147683_b(track);}finally{if(owned==track)owned=null;}
        }
        void stopLast(){if(owned!=null)stop(owned);}
        @Override public void silenceTrackedVanilla(){
            ISound[] voices;synchronized(trackedVanilla){voices=trackedVanilla.toArray(new ISound[trackedVanilla.size()]);trackedVanilla.clear();}
            SoundHandler handler=Minecraft.func_71410_x().func_147118_V();
            for(ISound voice:voices)if(handler.func_147692_c(voice))handler.func_147683_b(voice);
        }
    }
    private static final class MenuTrack extends PositionedSound implements ITickableSound {
        private volatile boolean done;
        MenuTrack(float gain,boolean loop){
            super(TRACK);field_147662_b=gain;field_147663_c=1;
            field_147659_g=loop;field_147665_h=0;field_147666_i=ISound.AttenuationType.NONE;
        }
        void setGain(float value){field_147662_b=Math.max(0,Math.min(1,value));}
        @Override public void func_73660_a(){/* The main-thread lifecycle controller updates gain. */}
        @Override public boolean func_147667_k(){return done;}
    }
}
