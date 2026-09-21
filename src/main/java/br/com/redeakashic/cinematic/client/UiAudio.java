package br.com.redeakashic.cinematic.client;
import br.com.redeakashic.cinematic.core.UiCueGate;
import br.com.redeakashic.cinematic.core.UiCueGate.Cue;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
/** Pre-rendered cathedral cues: bounded voices and soft steals, sharing the existing Minecraft mixer. */
public final class UiAudio {
    private static final UiCueGate gate=new UiCueGate();private static final ArrayDeque<UiSound> voices=new ArrayDeque<UiSound>();
    private static MenuConfig config;private static boolean greeted,failed;
    public static int submitted,suppressed,missing,softReleases,activeVoiceCount;public static ISound lastSound;
    private UiAudio(){}
    static void configure(MenuConfig c){config=c;}
    static void greet(){if(!greeted&&ready()){greeted=true;play(Cue.ENTER);}}
    private static boolean ready(){Minecraft mc=Minecraft.func_71410_x();return config!=null&&config.sfxEnabled&&config.sfxVolume>0&&!failed&&mc!=null&&mc.field_71474_y!=null&&mc.func_147118_V()!=null&&Display.isActive()&&mc.field_71474_y.func_151438_a(SoundCategory.MASTER)>0;}
    public static void play(Cue cue){
        if(!ready())return;if(!gate.allow(cue,System.nanoTime())){suppressed++;return;}
        try{
            SoundHandler handler=Minecraft.func_71410_x().func_147118_V();long now=System.nanoTime();
            for(Iterator<UiSound> it=voices.iterator();it.hasNext();){UiSound old=it.next();if(old.done||(now-old.born>400_000_000L&&!handler.func_147692_c(old)))it.remove();}
            ResourceLocation id=new ResourceLocation("akashiccinematic","ui."+cue.name().toLowerCase(Locale.ROOT));
            if(handler.func_147680_a(id)==null){if(missing++==0)System.err.println("[AkashicCinematic] UI cue missing; silent fallback.");return;}
            int actions=0;for(UiSound old:voices)if(old.cue!=Cue.FOCUS&&!old.releasing)actions++;
            for(UiSound old:voices){
                if(old.cue==Cue.FOCUS&&!old.releasing){old.release(now);softReleases++;}
                else if(cue!=Cue.FOCUS&&actions>=2&&!old.releasing&&old.cue!=Cue.FOCUS){old.release(now);softReleases++;actions--;}
            }
            if(voices.size()>=6){suppressed++;return;}
            UiSound sound=new UiSound(id,(float)config.sfxVolume,cue,now);voices.addLast(sound);lastSound=sound;handler.func_147682_a(sound);submitted++;activeVoiceCount=voices.size();
        }catch(RuntimeException|LinkageError ex){failed=true;System.err.println("[AkashicCinematic] UI audio disabled after failure: "+ex);}
    }
    private static final class UiSound extends PositionedSound implements ITickableSound {
        final Cue cue;final long born;final float level;boolean releasing,done;long releaseStart;
        UiSound(ResourceLocation id,float volume,Cue type,long now){super(id);cue=type;born=now;level=volume;field_147662_b=volume;field_147663_c=1;field_147666_i=ISound.AttenuationType.NONE;field_147659_g=false;field_147665_h=0;}
        void release(long now){releasing=true;releaseStart=now;}
        @Override public void func_73660_a(){if(releasing){double p=Math.min(1,Math.max(0,(System.nanoTime()-releaseStart)/90_000_000.0));field_147662_b=(float)(level*(1-p)*(1-p));if(p>=1)done=true;}}
        @Override public boolean func_147667_k(){return done;}
    }
}
