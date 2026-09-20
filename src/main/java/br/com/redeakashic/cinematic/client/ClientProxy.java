package br.com.redeakashic.cinematic.client;

import br.com.redeakashic.cinematic.CommonProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public final class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (Boolean.getBoolean("akashic.cinematic.disabled")) {
            event.getModLog().info("Cinematic menu bypassed by JVM kill switch.");
            return;
        }
        try {
            MenuConfig config = MenuConfig.read(event.getSuggestedConfigurationFile());
            if (!config.enabled) { return; }
            MinecraftForge.EVENT_BUS.register(new MenuHook(config, event.getModLog()));
            event.getModLog().info("Cinematic alpha registered. Native splash remains unchanged.");
        } catch (RuntimeException ex) {
            event.getModLog().error("Cinematic configuration rejected; existing menu preserved.", ex);
        } catch (LinkageError ex) {
            event.getModLog().error("Cinematic integration unavailable; existing menu preserved.", ex);
        }
    }
}
