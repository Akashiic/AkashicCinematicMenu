package br.com.redeakashic.cinematic.client;

import br.com.redeakashic.cinematic.core.ScreenPolicy;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

/** No coremod, no mixin, and no recursive displayGuiScreen call inside GuiOpenEvent. */
public final class MenuHook {
    private final MenuConfig config;
    private final Logger log;
    private boolean disabledForSession;
    public MenuHook(MenuConfig config, Logger log) { this.config = config; this.log = log; }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onOpen(GuiOpenEvent event) {
        if(!disabledForSession && config.suppressBogoConflict && Minecraft.func_71410_x().field_71441_e==null && BogoWarningCompat.consume(event.gui,log))event.gui=new net.minecraft.client.gui.GuiMainMenu();
        if (disabledForSession || event.gui == null
                || !ScreenPolicy.accepts(event.gui.getClass().getName(), config.replaceDummyCore)) { return; }
        GuiScreen original = event.gui;
        try {
            if (Keyboard.isCreated() && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                    || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
                disabledForSession = true;
                log.info("Cinematic bypass: SHIFT held. Existing menu retained for this session.");
                return;
            }
            event.gui = new CinematicMenu(original, this, config);
            log.info("Cinematic replacing exact menu class: " + original.getClass().getName());
        } catch (RuntimeException ex) { event.gui = original; failWithoutReplacement(ex); }
        catch (LinkageError ex) { event.gui = original; failWithoutReplacement(ex); }
    }
    private void failWithoutReplacement(Throwable ex) {
        disabledForSession = true;
        log.error("Cinematic hook failed; interception disabled for this session.", ex);
    }
    public void fallback(Minecraft mc, GuiScreen original, Throwable reason) {
        disabledForSession = true;
        if (reason != null) { log.error("Cinematic renderer/route failed. Returning to original menu.", reason); }
        else { log.info("Cinematic F8 bypass: returning to original menu for this session."); }
        // Latch is set BEFORE opening the prior screen, preventing a fallback loop.
        mc.func_147108_a(original);
    }
    public void dispatched(String action) { log.info("Cinematic dispatch: " + action); }
}
