from pathlib import Path
S=Path(__file__).resolve().parents[1]/'src/main/java/br/com/redeakashic/cinematic'
p=S/'CommonProxy.java';s=p.read_text();anchor='public class CommonProxy {';assert s.count(anchor)==1;s=s.replace(anchor,anchor+'\n    public void complete() { /* client lifecycle only */ }');p.write_text(s,newline='\n')
p=S/'AkashicCinematicMenu.java';s=p.read_text();anchor='    @Mod.EventHandler\n    public void preInit';assert s.count(anchor)==1;s=s.replace(anchor,'    @Mod.EventHandler public void complete(cpw.mods.fml.common.event.FMLLoadCompleteEvent event){proxy.complete();}\n'+anchor);p.write_text(s,newline='\n')
p=S/'client/ClientProxy.java';s=p.read_text();anchor='public final class ClientProxy extends CommonProxy {';assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n    private MenuHook menu;private MenuConfig settings;private org.apache.logging.log4j.Logger logger;\n    @Override public void complete(){if(menu!=null&&settings.suppressBogoConflict)MinecraftForge.EVENT_BUS.register(new LateBogoNoticeGate(menu,logger));}')
s=s.replace('            MinecraftForge.EVENT_BUS.register(new MenuHook(config, event.getModLog()));','            settings=config;logger=event.getModLog();menu=new MenuHook(config,logger);MinecraftForge.EVENT_BUS.register(menu);')
p.write_text(s,newline='\n');print('Late notice gate wired at loadComplete; no private Bogo flag modification')
