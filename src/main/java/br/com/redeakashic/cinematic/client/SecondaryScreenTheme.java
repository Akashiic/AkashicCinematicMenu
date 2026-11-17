package br.com.redeakashic.cinematic.client;
import br.com.redeakashic.cinematic.core.SecondaryScreenPolicy;
import br.com.redeakashic.cinematic.render.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.ResourceLocation;
/** Presentation-only theme for audited vanilla front-end screens. */
public final class SecondaryScreenTheme {
    public static final SecondaryScreenTheme INSTANCE=new SecondaryScreenTheme();
    private static final ResourceLocation BUTTONS=new ResourceLocation("akashiccinematic","textures/gui/widgets_forbidden.png");
    private static final ResourceLocation LIST=new ResourceLocation("akashiccinematic","textures/gui/list_surface.png");
    private static final ResourceLocation VANILLA_BUTTONS=new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation VANILLA_LIST=new ResourceLocation("textures/gui/options_background.png");
    private static boolean enabled;
    private static SecondaryFrameRenderer renderer;
    public static int frames;
    public static String lastSection="none";
    private SecondaryScreenTheme(){}
    public static void configure(boolean value){enabled=value;}
    private static boolean supported(GuiScreen screen){
        return enabled&&screen!=null&&SecondaryScreenPolicy.supports(screen.getClass().getName());
    }
    private static boolean current(){
        Minecraft mc=Minecraft.func_71410_x();
        return mc!=null&&supported(mc.field_71462_r);
    }
    @SuppressWarnings("try")
    public static boolean drawBackground(GuiScreen screen,int tint){
        if(!supported(screen)||Boolean.getBoolean("akashic.cinematic.disabled"))return false;
        Minecraft mc=Minecraft.func_71410_x();if(mc==null)return false;
        int w=mc.field_71443_c,h=mc.field_71440_d;if(w<160||h<120)return false;
        try(RawScope scope=new RawScope(w,h)){
            if(renderer==null)renderer=new SecondaryFrameRenderer();
            String section=SecondaryScreenPolicy.label(screen.getClass().getName());
            renderer.draw(w,h,section);frames++;lastSection=section;return true;
        }catch(RuntimeException|LinkageError ex){
            System.err.println("[AkashicCinematic] Secondary theme background fallback: "+ex);
            release();return false;
        }
    }
    public static int listPaints,overlayPaints;
    /** Called right after a vanilla list drew its container background (transformer anchor, see SecondaryThemeTransformer). */
    public static void listBackgroundDrawn(GuiSlot slot){paintList(slot,true,0,0);}
    /** Called at the end of GuiSlot.overlayBackground(startY,endY): the band above/below the list gets the frame back. */
    public static void listOverlayDrawn(GuiSlot slot,int startY,int endY){paintList(slot,false,startY,endY);}
    @SuppressWarnings("try")
    private static void paintList(GuiSlot slot,boolean background,int startY,int endY){
        if(slot==null||!current()||Boolean.getBoolean("akashic.cinematic.disabled"))return;
        Minecraft mc=Minecraft.func_71410_x();GuiScreen screen=mc.field_71462_r;
        int w=mc.field_71443_c,h=mc.field_71440_d;if(w<160||h<120||screen==null)return;
        double sx=(double)w/Math.max(1,screen.field_146294_l),sy=(double)h/Math.max(1,screen.field_146295_m);
        try(RawScope scope=new RawScope(w,h)){
            if(renderer==null)renderer=new SecondaryFrameRenderer();
            String section=SecondaryScreenPolicy.label(screen.getClass().getName());
            if(background){
                double x=slot.field_148152_e*sx,y=slot.field_148153_b*sy,rw=(slot.field_148151_d-slot.field_148152_e)*sx,rh=(slot.field_148154_c-slot.field_148153_b)*sy;
                renderer.region(w,h,section,x,y,rw,rh);renderer.listSurface(w,h,x,y,rw,rh);listPaints++;
            }else{
                renderer.region(w,h,section,0,startY*sy,w,(endY-startY)*sy);overlayPaints++;
            }
        }catch(RuntimeException|LinkageError ex){
            System.err.println("[AkashicCinematic] Secondary list theme fallback: "+ex);
            release();
        }
    }
    public static ResourceLocation buttonTexture(){return current()?BUTTONS:VANILLA_BUTTONS;}
    public static ResourceLocation listTexture(){return current()?LIST:VANILLA_LIST;}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;
        Minecraft mc=Minecraft.func_71410_x();
        if(renderer!=null&&(mc==null||!supported(mc.field_71462_r)))release();
    }
    @SuppressWarnings("try") private static void release(){
        Minecraft mc=Minecraft.func_71410_x();
        try{
            if(mc!=null&&mc.field_71443_c>0&&mc.field_71440_d>0){
                try(RawScope scope=new RawScope(mc.field_71443_c,mc.field_71440_d)){
                    if(renderer!=null)renderer.close();
                }
            }
        }catch(RuntimeException|LinkageError ex){
            System.err.println("[AkashicCinematic] Secondary theme cleanup: "+ex);
        }finally{renderer=null;}
    }
}
