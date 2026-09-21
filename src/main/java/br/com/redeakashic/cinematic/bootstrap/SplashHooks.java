package br.com.redeakashic.cinematic.bootstrap;
import java.util.Iterator;
import br.com.redeakashic.cinematic.core.LoadState;
import br.com.redeakashic.cinematic.render.*;
import org.lwjgl.opengl.Display;
/** Called only from Forge's GL-owning splash thread. Never starts threads or takes Forge locks. */
@SuppressWarnings("deprecation")
public final class SplashHooks {
    private static NativeVisuals visuals;
    private static boolean failed;
    private static long started,lastPoll;
    private static int previousWidth,previousHeight,geometryLogs;
    public static volatile boolean finished;
    public static volatile boolean rendered;
    public static volatile double elapsedSeconds;
    public static volatile LoadState last=LoadState.unknown();
    private SplashHooks(){}
    @SuppressWarnings("try")
    public static void frame(){
        if(failed||Boolean.getBoolean("akashic.cinematic.disabled")||Boolean.getBoolean("akashic.cinematic.disableSplash"))return;
        int w=Display.getWidth(),h=Display.getHeight();if(w<160||h<120)return;
        if((w!=previousWidth||h!=previousHeight)&&geometryLogs++<40){previousWidth=w;previousHeight=h;System.out.println("[AkashicCinematic] SPLASH_GEOMETRY "+w+"x"+h);}
        try(RawScope scope=new RawScope(w,h)){
            long now=System.nanoTime();if(started==0){started=EarlyWindowHooks.startedNanos>0?EarlyWindowHooks.startedNanos:now;System.out.println("[AkashicCinematic] FORGE_OVERLAY_STARTED epoch="+System.currentTimeMillis());}
            if(now-lastPoll>100_000_000L){last=sample();lastPoll=now;}
            String profile=NativeLayout.choose(w,h);
            if(visuals==null||!visuals.layout.profile.equals(profile)){if(visuals!=null)visuals.close();visuals=new NativeVisuals(profile);}
            elapsedSeconds=(now-started)/1.0e9;visuals.transform(w,h);visuals.startup(last,elapsedSeconds,1,!Boolean.getBoolean("akashic.cinematic.reduceMotion"));rendered=true;
        }catch(RuntimeException|LinkageError ex){
            ex.printStackTrace();failed=true;rendered=false;System.err.println("[AkashicCinematic] Splash skin disabled; retaining Forge renderer: "+ex);
        }
    }
    /** Kept in the existing excluded bootstrap package: never redirected by Angelica. */
    public static void present(){
        // LWJGL updates cached width/height/wasResized here. swapBuffers + processMessages alone does not.
        // Called at the original FML present site; no second swap and no new lock/context owner.
        Display.update();
    }
    public static LoadState sample(){
        Iterator<cpw.mods.fml.common.ProgressManager.ProgressBar> it=cpw.mods.fml.common.ProgressManager.barIterator();cpw.mods.fml.common.ProgressManager.ProgressBar root=null,leaf=null;int count=0;
        while(it.hasNext()&&count++<256){cpw.mods.fml.common.ProgressManager.ProgressBar b=it.next();if(root==null)root=b;leaf=b;}
        if(leaf==null)return LoadState.unknown();
        return new LoadState(leaf.getTitle(),leaf.getMessage(),root==leaf?"FORGE / "+leaf.getTitle():root.getTitle()+" / "+root.getMessage(),leaf.getStep(),leaf.getSteps());
    }
    @SuppressWarnings("try")
    public static void finish(){
        try(RawScope scope=new RawScope(Math.max(1,Display.getWidth()),Math.max(1,Display.getHeight()))){if(visuals!=null){visuals.close();visuals=null;}}
        catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] Splash cleanup: "+ex);}
        finally{finished=true;}
    }
}
