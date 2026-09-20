package br.com.redeakashic.cinematic.client;
import br.com.redeakashic.cinematic.core.UiCueGate;
import br.com.redeakashic.cinematic.core.UiCueGate.Cue;
import java.util.ArrayDeque;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
/** Reuses Minecraft's audio engine, MASTER mixer and resource lifecycle. */
public final class UiAudio {
    private static final UiCueGate gate=new UiCueGate();
    private static final ArrayDeque<ISound> voices=new ArrayDeque<ISound>();
    private static MenuConfig config;private static boolean greeted,failed;
    public static int submitted,suppressed,missing;
    public static ISound lastSound;
    private UiAudio(){}
    static void configure(MenuConfig c){config=c;}
    static void greet(){if(!greeted&&ready()){greeted=true;play(Cue.ENTER);}}
    private static boolean ready(){
        Minecraft mc=Minecraft.func_71410_x();
        return config!=null&&config.sfxEnabled&&config.sfxVolume>0&&!failed&&mc!=null&&mc.field_71474_y!=null&&mc.func_147118_V()!=null&&Display.isActive()
            &&mc.field_71474_y.func_151438_a(SoundCategory.MASTER)>0;
    }
    public static void play(Cue cue){
        if(!ready())return;
        if(!gate.allow(cue,System.nanoTime())){suppressed++;return;}
        try{
            SoundHandler handler=Minecraft.func_71410_x().func_147118_V();
            ResourceLocation resource=new ResourceLocation("akashiccinematic","ui."+cue.name().toLowerCase(Locale.ROOT));
            if(handler.func_147680_a(resource)==null){if(missing++==0)System.err.println("[AkashicCinematic] UI sound resource unavailable; silent fallback.");return;}
            while(voices.size()>=3)handler.func_147683_b(voices.removeFirst());
            UiSound sound=new UiSound(resource,(float)config.sfxVolume);voices.addLast(sound);lastSound=sound;
            handler.func_147682_a(sound);submitted++;
        }catch(RuntimeException|LinkageError ex){failed=true;System.err.println("[AkashicCinematic] UI audio disabled after playback failure: "+ex);}
    }
    private static final class UiSound extends PositionedSound {
        UiSound(ResourceLocation resource,float volume){
            super(resource);field_147662_b=volume;field_147663_c=1;
            field_147666_i=ISound.AttenuationType.NONE;field_147659_g=false;field_147665_h=0;
        }
    }
}
