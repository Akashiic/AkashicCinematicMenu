package br.com.redeakashic.cinematic.bootstrap;
import br.com.redeakashic.cinematic.core.LoadState;
import br.com.redeakashic.cinematic.render.NativeLayout;
import br.com.redeakashic.cinematic.render.NativeVisuals;
import br.com.redeakashic.cinematic.render.RawScope;
import org.lwjgl.opengl.Display;
/** One main-thread frame after the GL compatibility layer is ready. No threads or timers. */
public final class EarlyWindowHooks {
    public static volatile long startedNanos,presentedEpochMillis;
    public static volatile boolean attempted,presented,failed;
    private EarlyWindowHooks(){}
    @SuppressWarnings("try") public static void graphicsReady(){
        if(attempted||Boolean.getBoolean("akashic.cinematic.disabled")||Boolean.getBoolean("akashic.cinematic.disableSplash"))return;
        attempted=true;startedNanos=System.nanoTime();long epoch=System.currentTimeMillis();
        System.out.println("[AkashicCinematic] EARLY_GRAPHICS_READY epoch="+epoch);
        if(!Display.isCreated()){failed=true;return;}
        int w=Display.getWidth(),h=Display.getHeight();if(w<1||h<1){failed=true;return;}
        try(RawScope scope=new RawScope(w,h)){
            NativeVisuals art=new NativeVisuals(NativeLayout.choose(w,h));
            try{art.transform(w,h);art.startup(new LoadState("Preparando o ambiente","Inicializando os recursos do cliente.","AGUARDANDO O PROGRESSO DO FORGE",0,0),0,1,false);}
            finally{art.close();}
        }catch(RuntimeException|LinkageError ex){failed=true;System.err.println("[AkashicCinematic] Early frame unavailable; Forge remains in control: "+ex);return;}
        try{Display.update();presented=true;presentedEpochMillis=System.currentTimeMillis();
            System.out.println("[AkashicCinematic] EARLY_FRAME_PRESENTED epoch="+presentedEpochMillis+" costMs="+((System.nanoTime()-startedNanos)/1_000_000L));
        }catch(RuntimeException|LinkageError ex){failed=true;System.err.println("[AkashicCinematic] Early frame present failed: "+ex);}
    }
}
