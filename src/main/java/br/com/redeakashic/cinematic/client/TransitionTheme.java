package br.com.redeakashic.cinematic.client;
import br.com.redeakashic.cinematic.render.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.renderer.OpenGlHelper;
/** Called after the dirt quad, before native progress/messages/trivia. Does not present or pump packets. */
public final class TransitionTheme {
    private static boolean enabled,failed;private static BackdropRenderer backdrop;private static UiTypography text;
    private static long lastFrame;public static int aetherFrames,vanillaFrames,terrainFrames,etfuturumFrames;
    private static final java.nio.IntBuffer viewport=org.lwjgl.BufferUtils.createIntBuffer(16);public static String lastOrigin="none";
    static void configure(boolean value){enabled=value;}
    public static void loading(Framebuffer target,String origin){
        Minecraft mc=Minecraft.func_71410_x();if(mc==null)return;
        int w=target!=null&&OpenGlHelper.func_148822_b()?target.field_147621_c:mc.field_71443_c;
        int h=target!=null&&OpenGlHelper.func_148822_b()?target.field_147618_d:mc.field_71440_d;draw(w,h,origin);
    }
    public static void etfuturum(){viewport.clear();org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_VIEWPORT,viewport);draw(viewport.get(2),viewport.get(3),"etfuturum");}
    public static void terrain(){Minecraft mc=Minecraft.func_71410_x();if(mc!=null)draw(mc.field_71443_c,mc.field_71440_d,"terrain");}
    @SuppressWarnings("try") private static void draw(int w,int h,String origin){
        if(!enabled||failed||w<160||h<120||Boolean.getBoolean("akashic.cinematic.disabled"))return;
        try(RawScope scope=new RawScope(w,h)){
            if(backdrop==null)backdrop=new BackdropRenderer();if(text==null)text=new UiTypography();
            backdrop.draw(w,h,1);NativeVisuals.rect(0,0,w,h,NativeVisuals.INK,.68);
            double scale=Math.max(.65,Math.min(2,Math.min(w/1366.0,h/768.0)));
            text.centered("THE END OF FORBIDDEN",w*.5,h*.14,32*scale,w*.80,true,2*scale,NativeVisuals.IVORY,.92);
            NativeVisuals.rect(w*.37,h*.25,w*.26,1,NativeVisuals.GOLD,.65);
            NativeVisuals.rect(w*.10,h*.38,w*.80,h*.26,NativeVisuals.INK,.58);
            text.centered("A TRAVESSIA",w*.5,h*.77,13*scale,w*.8,false,3*scale,NativeVisuals.GOLD,.9);
            text.centered("REDE AKASHIC",w*.5,h*.83,10*scale,w*.8,false,2*scale,NativeVisuals.MUTED,.85);
            lastFrame=System.nanoTime();lastOrigin=origin;
            if(origin.equals("etfuturum"))etfuturumFrames++;else if(origin.equals("aether"))aetherFrames++;else if(origin.equals("terrain"))terrainFrames++;else vanillaFrames++;
        }catch(RuntimeException|LinkageError ex){failed=true;System.err.println("[AkashicCinematic] Transition background failed; original renderer retained: "+ex);dispose();}
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase==TickEvent.Phase.END&&backdrop!=null&&System.nanoTime()-lastFrame>1_000_000_000L)dispose();
    }
    @SuppressWarnings("try") private static void dispose(){
        Minecraft mc=Minecraft.func_71410_x();if(mc==null)return;
        try(RawScope scope=new RawScope(Math.max(1,mc.field_71443_c),Math.max(1,mc.field_71440_d))){
            if(backdrop!=null)backdrop.close();if(text!=null)text.close();
        }catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] Transition cleanup: "+ex);}
        finally{backdrop=null;text=null;}
    }
}
