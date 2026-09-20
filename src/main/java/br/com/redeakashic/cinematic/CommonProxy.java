package br.com.redeakashic.cinematic;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/** No Minecraft client or LWJGL references on the common/server side. */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) { /* client-side feature only */ }
}
