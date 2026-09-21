package br.com.redeakashic.cinematic.client;

import br.com.redeakashic.cinematic.CommonProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public final class ClientProxy extends CommonProxy {
    private MenuHook menu;private MenuConfig settings;private org.apache.logging.log4j.Logger logger;
    @Override public void complete(){if(menu!=null&&settings.suppressBogoConflict)MinecraftForge.EVENT_BUS.register(new LateBogoNoticeGate(menu,logger));}
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (Boolean.getBoolean("akashic.cinematic.disabled")) {
            event.getModLog().info("Cinematic menu bypassed by JVM kill switch.");
            return;
        }
        try {
            MenuConfig config = MenuConfig.read(event.getSuggestedConfigurationFile());
            if (!config.enabled) { return; }
            UiAudio.configure(config);
            TransitionTheme.configure(config.themeTransitions);
            if(config.themeTransitions)cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new TransitionTheme());
            try{MenuBgm.install(event.getSuggestedConfigurationFile().getParentFile());}
            catch(RuntimeException|LinkageError ex){event.getModLog().error("BGM unavailable; original music retained.",ex);}
            settings=config;logger=event.getModLog();menu=new MenuHook(config,logger);MinecraftForge.EVENT_BUS.register(menu);
            if(config.themeConnections)MinecraftForge.EVENT_BUS.register(new ConnectionScreenTheme(event.getModLog()));
            cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(ConnectionCancellationGuard.INSTANCE);
            event.getModLog().info("Cinematic menu registered. Splash overlay status is reported by bootstrap.");
        } catch (RuntimeException ex) {
            event.getModLog().error("Cinematic configuration rejected; existing menu preserved.", ex);
        } catch (LinkageError ex) {
            event.getModLog().error("Cinematic integration unavailable; existing menu preserved.", ex);
        }
    }
}
