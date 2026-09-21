package br.com.redeakashic.cinematic.client;
import java.lang.reflect.Field;
import java.util.*;
import br.com.redeakashic.cinematic.render.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.*;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
/** Presentation only. Native GuiConnecting/GuiDisconnected instances own networking and input. */
public final class ConnectionScreenTheme {
    private static final String CONNECTING="net.minecraft.client.multiplayer.GuiConnecting";
    private static final String DISCONNECTED="net.minecraft.client.gui.GuiDisconnected";
    private final Logger log;private BackdropRenderer backdrop;private boolean failed;
    public static int drawn;
    private final Map<String,Field> fields=new HashMap<String,Field>();
    public ConnectionScreenTheme(Logger log){this.log=log;}
    private Object field(Object instance,String name)throws ReflectiveOperationException{
        String key=instance.getClass().getName()+"/"+name;Field f=fields.get(key);
        if(f==null){Class<?> c=instance.getClass();while(c!=null){try{f=c.getDeclaredField(name);break;}catch(NoSuchFieldException ex){c=c.getSuperclass();}}if(f==null)throw new NoSuchFieldException(name);f.setAccessible(true);fields.put(key,f);}
        return f.get(instance);
    }
    private static boolean target(GuiScreen g){return g!=null&&(g.getClass().getName().equals(CONNECTING)||g.getClass().getName().equals(DISCONNECTED));}
    @SubscribeEvent(priority=EventPriority.LOWEST)
    @SuppressWarnings("try") public void draw(GuiScreenEvent.DrawScreenEvent.Pre event){
        Minecraft mc=Minecraft.func_71410_x();GuiScreen g=event.gui;
        if(failed||mc.field_71441_e!=null||!target(g))return;
        try{
            int gw=g.field_146294_l,gh=g.field_146295_m;if(gw<160||gh<120)return;
            boolean connecting=g.getClass().getName().equals(CONNECTING);
            String title=connecting?"A TRAVESSIA":String.valueOf(field(g,"field_146306_a"));
            List<String> lines=new ArrayList<String>();
            if(connecting){lines.add(I18n.func_135052_a(field(g,"field_146371_g")==null?"connect.connecting":"connect.authorizing"));lines.add("br2.xmxcloud.net:10511");}
            else{Object raw=field(g,"field_146305_g");if(!(raw instanceof List<?>))return;for(Object line:(List<?>)raw){if(!(line instanceof String))return;lines.add((String)line);}}
            if(lines.size()>32||lines.size()*10>gh-95)return; // Keep native behavior for exceptionally long server errors.
            Object rawButtons=field(g,"field_146292_n");if(!(rawButtons instanceof List<?>))return;
            List<GuiButton> buttons=new ArrayList<GuiButton>();for(Object b:(List<?>)rawButtons){if(!(b instanceof GuiButton))return;buttons.add((GuiButton)b);}
            int width=mc.field_71443_c,height=mc.field_71440_d;
            try(RawScope scope=new RawScope(width,height)){
                if(backdrop==null)backdrop=new BackdropRenderer();backdrop.draw(width,height,1);
                GL11.glScaled((double)width/gw,(double)height/gh,1);
                NativeVisuals.rect(0,0,gw,gh,0x060911,.58);
                int widest=mc.field_71466_p.func_78256_a(title),buttonBottom=0;
                for(String line:lines)widest=Math.max(widest,mc.field_71466_p.func_78256_a(line));
                for(GuiButton button:buttons)if(button.field_146125_m)buttonBottom=Math.max(buttonBottom,button.field_146129_i+button.field_146121_g);
                int top=Math.max(12,gh/2-58),boxWidth=Math.min(gw-16,Math.max(Math.max(280,gw*2/3),widest+32)),left=(gw-boxWidth)/2;
                int bodyTop=connecting?top+35:gh/2-lines.size()*9/2,bodyBottom=bodyTop+lines.size()*9;
                if(!connecting)top=bodyTop-30;
                int bottom=Math.min(gh-8,Math.max(bodyBottom+16,buttonBottom+14));
                NativeVisuals.rect(left,top,boxWidth,bottom-top,0x10141D,.88);
                NativeVisuals.rect(left,top,boxWidth,1,0xC4AA7C,.8);NativeVisuals.rect(left,bottom-1,boxWidth,1,0xC4AA7C,.25);
                centered(mc,title,gw/2,top+14,0xE8E4D8);
                int y=bodyTop;for(String line:lines){centered(mc,line,gw/2,y,0xACB2BB);y+=9;}
                for(GuiButton button:buttons)if(button.field_146125_m){
                    int x=button.field_146128_h,by=button.field_146129_i,bw=button.field_146120_f,bh=button.field_146121_g;
                    boolean hover=event.mouseX>=x&&event.mouseX<x+bw&&event.mouseY>=by&&event.mouseY<by+bh;
                    NativeVisuals.rect(x,by,bw,bh,hover?0x30333A:0x171C25,.95);NativeVisuals.rect(x,by,bw,1,0xC4AA7C,hover?.85:.3);
                    centered(mc,button.field_146126_j,x+bw/2,by+(bh-8)/2,button.field_146124_l?0xE8E4D8:0x767E89);
                }
            }
            event.setCanceled(true);drawn++;
        }catch(ReflectiveOperationException|RuntimeException|LinkageError ex){failed=true;log.warn("Connection theme unavailable; native rendering preserved.",ex);dispose();}
    }
    private static void centered(Minecraft mc,String text,int x,int y,int color){
        mc.field_71466_p.func_78276_b(text,x-mc.field_71466_p.func_78256_a(text)/2,y,color);
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public void onOpen(GuiOpenEvent event){if(!target(event.gui))dispose();}
    @SuppressWarnings("try") private void dispose(){
        if(backdrop==null)return;Minecraft mc=Minecraft.func_71410_x();
        try(RawScope scope=new RawScope(Math.max(1,mc.field_71443_c),Math.max(1,mc.field_71440_d))){backdrop.close();}
        catch(RuntimeException|LinkageError ex){log.warn("Connection theme cleanup failed.",ex);}finally{backdrop=null;}
    }
}
