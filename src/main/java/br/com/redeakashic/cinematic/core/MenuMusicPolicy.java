package br.com.redeakashic.cinematic.core;
/** Pure context policy. Worlds take precedence over every UI state. */
public final class MenuMusicPolicy {
    private MenuMusicPolicy(){}
    public static boolean frontend(boolean reachedMain,boolean world,String screen){
        if(!reachedMain||world||screen==null)return false;
        return !screen.equals("net.minecraft.client.multiplayer.GuiConnecting")
            &&!screen.equals("net.minecraft.client.gui.GuiDownloadTerrain")
            &&!screen.equals("net.minecraft.client.gui.GuiScreenWorking");
    }
    public static boolean suppressVanilla(boolean frontend,boolean replacementReady,String domain,String path,boolean musicCategory){
        return frontend&&replacementReady&&musicCategory&&"minecraft".equals(domain)&&path!=null&&path.startsWith("music.");
    }
}
