package br.com.redeakashic.cinematic;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AkashicCinematicMenu.ID, name = "Akashic Cinematic Menu",
     version = AkashicCinematicMenu.VERSION, acceptedMinecraftVersions = "[1.7.10]",
     acceptableRemoteVersions = "*")
public final class AkashicCinematicMenu {
    public static final String ID = "akashiccinematicmenu";
    public static final String VERSION = "0.1.0-alpha.1";
    @SidedProxy(clientSide = "br.com.redeakashic.cinematic.client.ClientProxy",
                serverSide = "br.com.redeakashic.cinematic.CommonProxy")
    public static CommonProxy proxy;
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }
}
