package br.com.redeakashic.cinematic.client;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.apache.logging.log4j.Logger;
/** Registered after ordinary mod initialization because Bogo also uses LOWEST priority. */
public final class LateBogoNoticeGate {
    private final MenuHook menu;private final Logger log;
    LateBogoNoticeGate(MenuHook menu,Logger log){this.menu=menu;this.log=log;}
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onOpen(GuiOpenEvent event){
        if(Minecraft.func_71410_x().field_71441_e!=null)return;
        if(BogoWarningCompat.consume(event.gui,log)){
            event.gui=new GuiMainMenu();
            // Reuse menu policy without posting a new GUI event or overwriting other warnings.
            menu.onOpen(event);
        }
    }
}
